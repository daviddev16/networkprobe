package com.networkprobe.core;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;
import com.networkprobe.core.annotation.Feature;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.entity.ResponseEntity;
import com.networkprobe.core.exception.InvalidPropertyException;
import com.networkprobe.core.model.*;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.networkprobe.core.util.Utility.asBoolean;

/* TODO: */
@Singleton(creationType = SingletonType.LAZY, order = 900)
public class YamlTemplateLoader implements FileTemplateLoader {

    public static final Logger LOG = LoggerFactory.getLogger(YamlTemplateLoader.class);

    @Override
    public ConfigurationHolder getConfigurator(File file,
                                               BaseConfigurableTemplate configurableTemplate) throws IOException
    {
        YamlMapping yamlMapping = Yaml.createYamlInput(file)
                .readYamlMapping();
        return new YamlConfigurationHolder(yamlMapping, configurableTemplate);
    }

    @Override
    public boolean exceptionHandler(Exception exception) {
        exception.printStackTrace();
        return true;
    }

    @Override
    /*TODO:*/
    public void loadRouteSettings(ConfigurationHolder configurationHolder)
            throws JSONException, InvalidPropertyException {

        YamlMapping yamlMapping = ((YamlConfigurationHolder)configurationHolder).getYAMLMapping();
        YamlMapping networkingMapping = yamlMapping.yamlMapping(Key.NETWORKING);

        /* adiciona as rotas padrões no mapa de rotas */
        configurationHolder.getConfigurableTemplate()
                .getRoutes().put("any", Route.ANY);

        configurationHolder.getConfigurableTemplate()
                .getRoutes().put("none", Route.NONE);

        YamlSequence routesSequence = networkingMapping.yamlSequence(Key.ROUTES);
        for (int i = 0; i < routesSequence.size(); i++) {
            YamlMapping routeMapping = routesSequence.yamlMapping(i);
            String name = routeMapping.string(Key.NAME);
            configurationHolder.getConfigurableTemplate()
                    .getRoutes().put(name, new Route.Builder()
                                    .cidr(routeMapping.string(Key.CIDR))
                                    .name(name)
                                    .get());
        }

    }

    @Override
    public void loadNetworkingSettings(ConfigurationHolder configurationHolder) {

        YamlMapping yamlMapping = ((YamlConfigurationHolder)configurationHolder).getYAMLMapping();
        YamlMapping networkingMapping = yamlMapping.yamlMapping(Key.NETWORKING);

        YamlMapping discoveryServiceMapping = networkingMapping.yamlMapping("discoveryService");
        YamlMapping exchangeServiceMapping = networkingMapping.yamlMapping("exchangeService");

        configurationHolder.getConfigurableTemplate()
                .configureNetworking(
                        new Networking.Builder()
                                .tcpBindAddress(
                                        exchangeServiceMapping.string(Key.TCP_BIND_ADDRESS))
                                .udpBroadcastAddress(
                                        discoveryServiceMapping.string(Key.UDP_BROADCAST_ADDRESS))
                                .enableDiscovery(
                                        asBoolean(discoveryServiceMapping.string(Key.ENABLE_DISCOVERY),
                                                Key.ENABLE_DISCOVERY))
                                .tcpSocketBacklog(
                                        exchangeServiceMapping.integer(Key.TCP_SOCKET_BACKLOG))
                                .udpRequestThreshold(
                                        discoveryServiceMapping.integer(Key.UDP_REQUEST_THRESHOLD))
                                .tcpConnectionThreshold(
                                        exchangeServiceMapping.integer(Key.TCP_CONNECTION_THRESHOLD))
                                .get());
    }

    @Override
    /*TODO:*/
    public void loadCommandSettings(ConfigurationHolder configurationHolder,
                                    ResponseEntityFactory responseEntityFactory) {

        YamlMapping yamlMapping = ((YamlConfigurationHolder)configurationHolder).getYAMLMapping();
        YamlSequence commandsSequence = yamlMapping.yamlSequence("commands");
        for (int i = 0; i < commandsSequence.size(); i++) {

            YamlMapping commandMapping = commandsSequence.yamlMapping(i);
            String commandName = commandMapping.string(Key.NAME);
            boolean cachedOnce = asBoolean(commandMapping.string(Key.CACHED_ONCE), Key.CACHED_ONCE);

            ResponseEntity<?> responseEntity = responseEntityFactory.responseEntityOf(
                    commandMapping.string(Key.RESPONSE),
                    cachedOnce
            );

            YamlSequence routesSequence = commandMapping.yamlSequence(Key.ROUTES);

            Command.Builder commandBuilder = new Command.Builder()
                    .name(commandName)
                    .cachedOnce(cachedOnce)
                    .response(responseEntity);

            YamlSequence tagsSequence = commandMapping.yamlSequence(Key.TAGS);
            if (tagsSequence != null && !tagsSequence.isEmpty()) {
                for (int j = 0; j < tagsSequence.size(); j++) {
                    commandBuilder.addTag(tagsSequence.string(j));
                }
            }

            for (CidrNotation cidrNotation : getAllValidRoutes(routesSequence,
                    configurationHolder.getConfigurableTemplate()))
            {
                System.out.println("\n\n"+commandName);
                System.out.println(cidrNotation.toString()+"\n\n");
                commandBuilder.network(cidrNotation);
            }

            configurationHolder.getConfigurableTemplate()
                    .getCommands().put(commandName, commandBuilder.get());
        }

    }

    /**
     * Retorna todas as rotas validas do comando
     * */
    private List<CidrNotation> getAllValidRoutes(final YamlSequence commandRoutes, Template template) {
        List<CidrNotation> cidrNotations = new ArrayList<>();
        for (int i = 0; i < commandRoutes.size(); i++) {
            String routeName = commandRoutes.string(i);
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
        LOG.info("O template do tipo YAML foi carregado com sucesso.");
    }

}
