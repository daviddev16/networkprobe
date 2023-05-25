package com.networkprobe.core.config;

import com.networkprobe.core.api.ResponseEntityFactory;
import com.networkprobe.core.api.ResponseEntity;
import com.networkprobe.core.api.TemplateLoader;
import com.networkprobe.core.exception.InvalidPropertyException;
import com.networkprobe.core.config.model.Command;
import com.networkprobe.core.config.model.Networking;
import com.networkprobe.core.config.model.Route;
import com.networkprobe.core.util.Keys;
import com.networkprobe.core.util.Validator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.networkprobe.core.util.Utility.*;
import static com.networkprobe.core.util.Validator.checkIsNotNull;
import static java.lang.String.*;

public class JsonTemplateLoader implements TemplateLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonTemplateLoader.class);

    private Networking networking;
    private Map<String, Route> routes;
    private Map<String, Command> commands;

    @Override
    public void load(File jsonFile, ResponseEntityFactory responseEntityFactory) {
        try {
            Validator.checkIsReadable(jsonFile, "jsonFile");
            responseEntityFactory = checkIsNotNull(responseEntityFactory, "responseEntityFactory");
            JSONObject jsonObject = new JSONObject(readFile(jsonFile));

            LOGGER.info("Carregando e validando informações do template json.");
            networking = loadNetworkingSettings(jsonObject);

            routes = Collections.synchronizedMap(new HashMap<>());
            loadAllRoutes(jsonObject);

            commands = Collections.synchronizedMap(new HashMap<>());
            loadAllCommands(jsonObject, responseEntityFactory);

            LOGGER.info("Template carregado com sucesso.");
        } catch (Exception exception) {
            if (exception instanceof JSONException)
                LOGGER.error("Houve um erro na hora de processar o arquivo de configuração Json.");
            else if (exception instanceof IOException || exception instanceof SecurityException)
                LOGGER.error("Houve um erro na hora de ler o arquivo de configuração Json.");
            else if (exception instanceof InvalidPropertyException)
                LOGGER.error("Foi identificado um parâmetro inválido dentro das configurações.");

            LOGGER.error("Dentro do arquivo \"{}\", verifique a seguinte informação: " +
                            "\n\n{}\n\n", jsonFile.getName(), exception.getMessage());
            Runtime.getRuntime().exit(155);
        }
    }

    private Networking loadNetworkingSettings(final JSONObject jsonObject)
            throws JSONException {

        JSONObject networkingJsonObject = jsonObject.getJSONObject(Keys.NETWORKING);
        return new Networking.Builder()
                .tcpBindAddress(networkingJsonObject.getString(Keys.TCP_BIND_ADDRESS))
                .udpBroadcastAddress(networkingJsonObject.getString(Keys.UDP_BROADCAST_ADDRESS))
                .enableDiscovery(networkingJsonObject.getBoolean(Keys.ENABLE_DISCOVERY))
                .tcpSocketBacklog(networkingJsonObject.getInt(Keys.TCP_SOCKET_BACKLOG))
                .udpRequestThreshold(networkingJsonObject.getInt(Keys.UDP_REQUEST_THRESHOLD))
                .get();
    }

    private void loadAllRoutes(final JSONObject jsonObject)
            throws JSONException, InvalidPropertyException {

        JSONArray routesJsonArray = jsonObject.getJSONArray(Keys.ROUTES);
        for (Object routeObject : routesJsonArray) {

            if (!(routeObject instanceof JSONObject))
                throw incoherentPropertyTypeException(format("Um JSONObject é esperado na" +
                        " propriedade %s.", Keys.ROUTES));

            JSONObject routeJsonObject = (JSONObject) routeObject;
            String name = routeJsonObject.getString(Keys.NAME);

            Route route = new Route.Builder()
                    .cidr(routeJsonObject.getString(Keys.CIDR))
                    .name( sanitize(name) )
                    .get();

            getRoutes().put(name, route);
        }
    }

    private void loadAllCommands(final JSONObject jsonObject,
                                      final ResponseEntityFactory responseEntityFactory)
            throws JSONException, InvalidPropertyException {

        JSONArray commandsJsonArray = jsonObject.getJSONArray(Keys.COMMANDS);
        for (int i = 0; i < commandsJsonArray.length(); i++) {
            Object commandObject = commandsJsonArray.get(i);

            if (!(commandObject instanceof JSONObject))
                throw incoherentPropertyTypeException(format("Um JSONObject é esperado no objeto de índice %d na " +
                        "propriedade %s.", i, Keys.COMMANDS));

            JSONObject commandJsonObject = (JSONObject) commandObject;

            JSONArray routesJsonArray = commandJsonObject.getJSONArray(Keys.ROUTES);
            boolean cachedOnce = commandJsonObject.getBoolean(Keys.CACHED_ONCE);
            String rawContent = commandJsonObject.getString(Keys.RESPONSE);
            String name = commandJsonObject.getString(Keys.NAME);

            ResponseEntity<?> responseEntity = responseEntityFactory.responseEntityOf(rawContent, cachedOnce);

            Command.Builder commandBuilder = new Command.Builder()
                    .through(allValidRoutes(routesJsonArray))
                    .response(responseEntity)
                    .name(sanitize(name));

            getCommands().put(name, commandBuilder.get());
        }
    }

    private String[] allValidRoutes(final JSONArray jsonArray) {
        List<String> routes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object routeObject = jsonArray.get(i);
            if (!(routeObject instanceof String))
                throw incoherentPropertyTypeException(format("Um String é esperado no objeto de índice %d na " +
                        "propriedade %s.", i, Keys.ROUTES));

            String routeName = (String) routeObject;
            if (!getRoutes().containsKey(routeName)) {
                LOGGER.warn("A rota \"{}\" não existe no mapa de rotas, isso pode impedir o " +
                        "acesso desse comando.", routeName);
                continue;
            }
            routes.add(routeName);
        }
        return routes.toArray(routes.toArray(new String[routes.size()]));
    }

    private InvalidPropertyException incoherentPropertyTypeException(String message) {
        return new InvalidPropertyException( message );
    }

    @Override
    public Networking getNetworking() {
        return networking;
    }

    @Override
    public Map<String, Route> getRoutes() {
        return routes;
    }

    @Override
    public Map<String, Command> getCommands() {
        return commands;
    }

}
