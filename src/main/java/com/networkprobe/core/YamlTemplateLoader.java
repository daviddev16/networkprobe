package com.networkprobe.core;

import com.networkprobe.core.annotation.Feature;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.InvalidPropertyException;
import com.networkprobe.core.model.Key;
import com.networkprobe.core.model.Networking;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.networkprobe.core.util.Utility.*;
import static com.networkprobe.core.util.Utility.asString;
import static java.lang.String.format;

@Feature
@Singleton(creationType = SingletonType.LAZY, order = 900)
public class YamlTemplateLoader implements FileTemplateLoader {

    public static final Logger LOG = LoggerFactory.getLogger(YamlTemplateLoader.class);

    @Override
    public ConfigurationHolder getConfigurator(File file,
                                               BaseConfigurableTemplate configurableTemplate) throws FileNotFoundException
    {
        Yaml yaml = new Yaml();
        Map<String, Object> configurationMap = yaml.load(new FileReader(file));
        return ConfigurationHolder.of(configurationMap, configurableTemplate);
    }

    @Override
    public boolean exceptionHandler(Exception exception) {

        exception.printStackTrace();
        /*if (exception instanceof JSONException)
            LOG.error("Houve um erro na hora de processar o arquivo de configuração Json.");

        else if (exception instanceof IOException || exception instanceof SecurityException)
            LOG.error("Houve um erro na hora de ler o arquivo de configuração Json.");

        else if (exception instanceof InvalidPropertyException)
            LOG.error("Foi identificado um parâmetro inválido dentro das configurações.");

        LOG.error("Dentro do arquivo \"{}\", verifique a seguinte informação: " +
                "\n{}\n", "jsonFile.getName()", exception.getMessage());*/

        return true;
    }

    @Override
    /*TODO:*/
    public void loadRouteSettings(ConfigurationHolder configurationHolder)
            throws JSONException, InvalidPropertyException {

        Map<String, Object> yamlConfig = configurationHolder.getYAMLMap();


    }

    @Override
    public void loadNetworkingSettings(ConfigurationHolder configurationHolder) {
        Map<String, Object> yamlConfig = configurationHolder.getYAMLMap();
        Map<String, Object> networkingConfig = mapOf(yamlConfig, "networking");
        Map<String, Object> exchangeServiceConfig = mapOf(networkingConfig, "exchangeService");
        Map<String, Object> discoveryServiceConfig = mapOf(networkingConfig, "discoveryService");
        configurationHolder.getConfigurableTemplate()
                .configureNetworking(
                        new Networking.Builder()
                                .tcpBindAddress(
                                        asString(exchangeServiceConfig.get(Key.TCP_BIND_ADDRESS)))
                                .udpBroadcastAddress(
                                        asString(discoveryServiceConfig.get(Key.UDP_BROADCAST_ADDRESS)))
                                .enableDiscovery(
                                       asBoolean(discoveryServiceConfig.get(Key.ENABLE_DISCOVERY)))
                                .tcpSocketBacklog(
                                        asInt(exchangeServiceConfig.get(Key.TCP_SOCKET_BACKLOG)))
                                .udpRequestThreshold(
                                        asInt(discoveryServiceConfig.get(Key.UDP_REQUEST_THRESHOLD)))
                                .tcpConnectionThreshold(
                                        asInt(exchangeServiceConfig.get(Key.TCP_CONNECTION_THRESHOLD)))
                                .get());
    }

    @Override
    /*TODO:*/
    public void loadCommandSettings(ConfigurationHolder configurationHolder,
                                    ResponseEntityFactory responseEntityFactory) {

        Map<String, Object> yamlConfig = configurationHolder.getYAMLMap();
    }

    /*
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
    } */

    @Override
    public void onSuccessfully() {
        LOG.info("O template do tipo YAML foi carregado com sucesso.");
    }


}
