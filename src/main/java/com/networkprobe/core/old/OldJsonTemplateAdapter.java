package com.networkprobe.core.old;

import com.networkprobe.core.JsonTemplateLoader;
import com.networkprobe.core.BaseConfigurableTemplate;
import com.networkprobe.core.Defaults;
import com.networkprobe.core.SingletonDirectory;
import com.networkprobe.core.adapter.FileTemplateAdapter;
import com.networkprobe.core.annotation.Replaced;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.model.CidrNotation;
import com.networkprobe.core.model.Command;
import com.networkprobe.core.model.Route;
import com.networkprobe.core.exception.InvalidPropertyException;
import com.networkprobe.core.model.Key;
import com.networkprobe.core.ResponseEntityFactory;
import com.networkprobe.core.util.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.networkprobe.core.util.Validator.*;
import static java.lang.String.format;

//@Singleton(creationType = SingletonType.DYNAMIC, order = -401)
@Replaced(newer = JsonTemplateLoader.class)
public class OldJsonTemplateAdapter extends BaseConfigurableTemplate implements FileTemplateAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(OldJsonTemplateAdapter.class);

    public OldJsonTemplateAdapter()
    {
        SingletonDirectory.denyInstantiation(this);
    }

    @Override
    public void load(File jsonFile, ResponseEntityFactory responseEntityFactory) {
        try {
            checkIsReadable(jsonFile, "jsonFile");
            nonNull(responseEntityFactory, "responseEntityFactory");
            LOG.info("Carregando e validando informações do template json.");
            JSONObject jsonObject = new JSONObject(Utility.readFile(jsonFile));
            loadNetworkingSettings(jsonObject);
            loadAllRoutes(jsonObject);
            loadAllCommands(jsonObject, responseEntityFactory);
            LOG.info("Template carregado com sucesso.");
        } catch (Exception exception) {

            if (exception instanceof JSONException)
                LOG.error("Houve um erro na hora de processar o arquivo de configuração Json.");

            else if (exception instanceof IOException || exception instanceof SecurityException)
                LOG.error("Houve um erro na hora de ler o arquivo de configuração Json.");

            else if (exception instanceof InvalidPropertyException)
                LOG.error("Foi identificado um parâmetro inválido dentro das configurações.");

            LOG.error("Dentro do arquivo \"{}\", verifique a seguinte informação: " +
                    "\n{}\n", jsonFile.getName(), exception.getMessage());

            Runtime.getRuntime().exit(155);
        }
    }

    private void loadNetworkingSettings(final JSONObject jsonObject)
            throws JSONException {
        JSONObject networkingJsonObject = jsonObject.getJSONObject(Key.NETWORKING);
        /*setNetworking(
                new Networking.Builder()
                        .tcpBindAddress(
                                networkingJsonObject.getString(Key.TCP_BIND_ADDRESS))
                        .udpBroadcastAddress(
                                networkingJsonObject.getString(Key.UDP_BROADCAST_ADDRESS))
                        .enableDiscovery(
                                networkingJsonObject.getBoolean(Key.ENABLE_DISCOVERY))
                        .tcpSocketBacklog(
                                networkingJsonObject.getInt(Key.TCP_SOCKET_BACKLOG))
                        .udpRequestThreshold(
                                networkingJsonObject.getInt(Key.UDP_REQUEST_THRESHOLD))
                        .tcpConnectionThreshold(
                                networkingJsonObject.getInt(Key.TCP_CONNECTION_THRESHOLD))
                        .get()
        );*/
    }

    private void loadAllRoutes(final JSONObject jsonObject)
            throws JSONException, InvalidPropertyException {

        JSONArray routesJsonArray = jsonObject.getJSONArray(Key.ROUTES);

        /* adiciona as rotas padrões no mapa de rotas */
        getRoutes().put("any", Route.ANY);
        getRoutes().put("none", Route.NONE);

        for (Object routeObject : routesJsonArray) {

            if (!(routeObject instanceof JSONObject))
                throw incoherentPropertyTypeException(format("Um JSONObject é esperado na" +
                        " propriedade %s.", Key.ROUTES));

            JSONObject routeJsonObject = (JSONObject) routeObject;
            String name = routeJsonObject.getString(Key.NAME);

            Route route = new Route.Builder()
                    .cidr(routeJsonObject.getString(Key.CIDR))
                    .name(name)
                    .get();

            getRoutes().put(name, route);
        }
    }

    private void loadAllCommands(final JSONObject jsonObject,
                                 final ResponseEntityFactory responseEntityFactory)
            throws JSONException, InvalidPropertyException {

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
                    commandJsonObject.getBoolean(Key.CACHED_ONCE)
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

            for (CidrNotation cidrNotation : getAllValidRoutes(routesJsonArray))
                commandBuilder.network(cidrNotation);

            getCommands().put(commandName, commandBuilder.get());
        }

        /* VALIDANDO COMANDOS INTERNOS */
        for (String internalCommandName : Defaults.COMMANDS) {
            if (!getCommands().containsKey(internalCommandName))
                throw incoherentPropertyTypeException(format("O comando interno '%s' não foi encontrado, tente " +
                        "reinstalar o sistema novamente ou baixar o template padrão.", internalCommandName));
        }

        LOG.info("Os comandos internos foram verificados e não houve erros.");
    }

    /**
     * Retorna todas as rotas validas do comando
     * */
    private List<CidrNotation> getAllValidRoutes(final JSONArray jsonArray) {

        List<CidrNotation> cidrNotations = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            Object routeObject = jsonArray.get(i);

            if (!(routeObject instanceof String))
                throw incoherentPropertyTypeException(format("Um String é esperado no objeto de índice %d na " +
                        "propriedade %s.", i, Key.ROUTES));

            String routeName = (String) routeObject;
            Route commandRoute = getRoutes().get(routeName);

            if (commandRoute == null) {
                LOG.warn("A rota \"{}\" não existe no mapa de rotas, isso pode impedir o " +
                        "acesso desse comando.", routeName);
                continue;
            }
            cidrNotations.add(commandRoute.getCidr());
        }
        return cidrNotations;
    }

}
