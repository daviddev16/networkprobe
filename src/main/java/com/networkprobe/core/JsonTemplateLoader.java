package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.model.*;
import com.networkprobe.core.entity.ResponseEntity;
import com.networkprobe.core.exception.InvalidPropertyException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.networkprobe.core.BaseConfigurableTemplate.incoherentPropertyTypeException;
import static com.networkprobe.core.util.Utility.readFile;
import static java.lang.String.format;

@Singleton(creationType = SingletonType.LAZY, order = 900)
public class JsonTemplateLoader implements FileTemplateLoader {

    public static final Logger LOG = LoggerFactory.getLogger(JsonTemplateLoader.class);

    @Override
    public ConfigurationHolder getConfigurator(File file,
                                               BaseConfigurableTemplate configurableTemplate) throws IOException {
        return new JsonConfigurationHolder(new JSONObject(readFile(file)), configurableTemplate);
    }

    @Override
    public boolean exceptionHandler(Exception exception) {

        if (exception instanceof JSONException)
            LOG.error("Houve um erro na hora de processar o arquivo de configuração Json.");

        else if (exception instanceof IOException || exception instanceof SecurityException)
            LOG.error("Houve um erro na hora de ler o arquivo de configuração Json.");

        else if (exception instanceof InvalidPropertyException)
            LOG.error("Foi identificado um parâmetro inválido dentro das configurações.");

        LOG.error("Houve um erro no carregamento do arquivo de template. Verifique: {}", exception.getMessage());

        return true;
    }

    @Override
    public void loadRouteSettings(ConfigurationHolder configurationHolder)
            throws JSONException, InvalidPropertyException {

        JSONObject jsonObject = ((JsonConfigurationHolder)configurationHolder).getJSONObject();

        JSONArray routesJsonArray = jsonObject.getJSONArray(Key.ROUTES);

        /* adiciona as rotas padrões no mapa de rotas */
        configurationHolder.getConfigurableTemplate()
                .getRoutes().put("any", Route.ANY);

        configurationHolder.getConfigurableTemplate()
                .getRoutes().put("none", Route.NONE);

        for (Object routeObject : routesJsonArray) {

            if (!(routeObject instanceof JSONObject))
                throw incoherentPropertyTypeException(format("Um JSONObject é esperado na" +
                        " propriedade %s.", Key.ROUTES));

            JSONObject routeJsonObject = (JSONObject) routeObject;
            String name = routeJsonObject.getString(Key.NAME);

            configurationHolder.getConfigurableTemplate()
                    .getRoutes().put(name,
                            new Route.Builder()
                                .cidr(routeJsonObject.getString(Key.CIDR))
                                .name(name)
                                .get());
        }
    }

    @Override
    public void loadNetworkingSettings(ConfigurationHolder configurationHolder) {
        JSONObject jsonObject = ((JsonConfigurationHolder)configurationHolder).getJSONObject();
        JSONObject jsonNetConfig = jsonObject.getJSONObject(Key.NETWORKING);
        configurationHolder.getConfigurableTemplate()
                .configureNetworking(
                        new Networking.Builder()
                            .tcpBindAddress(
                                    jsonNetConfig.getString(Key.TCP_BIND_ADDRESS))
                            .udpBroadcastAddress(
                                    jsonNetConfig.getString(Key.UDP_BROADCAST_ADDRESS))
                            .enableDiscovery(
                                    jsonNetConfig.getBoolean(Key.ENABLE_DISCOVERY))
                            .tcpSocketBacklog(
                                    jsonNetConfig.getInt(Key.TCP_SOCKET_BACKLOG))
                            .udpRequestThreshold(
                                    jsonNetConfig.getInt(Key.UDP_REQUEST_THRESHOLD))
                            .tcpConnectionThreshold(
                                    jsonNetConfig.getInt(Key.TCP_CONNECTION_THRESHOLD))
                                .get());
    }

    @Override
    public void loadCommandSettings(ConfigurationHolder configurationHolder, ResponseEntityFactory responseEntityFactory) {

        JSONObject jsonObject = ((JsonConfigurationHolder)configurationHolder).getJSONObject();

        JSONArray commandsJsonArray = jsonObject.getJSONArray(Key.COMMANDS);

        for (int i = 0; i < commandsJsonArray.length(); i++) {

            Object commandObject = commandsJsonArray.get(i);

            if (!(commandObject instanceof JSONObject))
                throw incoherentPropertyTypeException(format("Um JSONObject é esperado no objeto de" +
                        " índice %d na propriedade %s.", i, Key.COMMANDS));

            JSONObject commandJsonObject = (JSONObject) commandObject;

            JSONArray routesJsonArray = commandJsonObject.getJSONArray(Key.ROUTES);
            boolean cachedOnce = commandJsonObject.getBoolean(Key.CACHED_ONCE);
            String commandName = commandJsonObject.getString(Key.NAME);

            ResponseEntity<?> responseEntity = responseEntityFactory.responseEntityOf(
                    commandJsonObject.getString(Key.RESPONSE),
                    cachedOnce
            );

            Command.Builder commandBuilder = new Command.Builder()
                    .name(commandName)
                    .cachedOnce(cachedOnce)
                    .response(responseEntity);

            JSONArray tagsArray = commandJsonObject.optJSONArray(Key.TAGS);

            if (tagsArray != null) {
                for (Object tagObject : tagsArray) {
                    if (tagObject instanceof String)
                        commandBuilder.addTag(tagObject.toString());
                }
            }

            for (CidrNotation cidrNotation : getAllValidRoutes(routesJsonArray,
                    configurationHolder.getConfigurableTemplate()))
            {
                commandBuilder.network(cidrNotation);
            }

            configurationHolder.getConfigurableTemplate()
                    .getCommands().put(commandName, commandBuilder.get());
        }

        /* VALIDANDO COMANDOS INTERNOS */
        for (String internalCommandName : Defaults.COMMANDS) {
            if (!configurationHolder.getConfigurableTemplate()
                    .getCommands().containsKey(internalCommandName))
            {
                throw incoherentPropertyTypeException(format("O comando interno " +
                        "'%s' não foi encontrado, tente reinstalar o sistema novamente " +
                        "ou baixar o template padrão.", internalCommandName));
            }
        }
    }

    /**
     * Retorna todas as rotas validas do comando
     * */
    private List<CidrNotation> getAllValidRoutes(final JSONArray jsonArray, Template template) {

        List<CidrNotation> cidrNotations = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            Object routeObject = jsonArray.get(i);

            if (!(routeObject instanceof String))
                throw incoherentPropertyTypeException(format("Um String é esperado no objeto de índice %d na " +
                        "propriedade %s.", i, Key.ROUTES));

            String routeName = (String) routeObject;
            Route commandRoute = template.getRoutes().get(routeName);

            if (commandRoute == null) {
                LOG.warn("A rota \"{}\" não existe no mapa de rotas, isso pode impedir o " +
                        "acesso desse comando.", routeName);
                continue;
            }
            cidrNotations.add(commandRoute.getCidr());
        }
        return cidrNotations;
    }

    @Override
    public void onSuccessfully() {
        LOG.info("O template do tipo JSON foi carregado com sucesso.");
    }


}
