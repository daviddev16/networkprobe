package com.networkprobe.core;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.domain.CidrNotation;
import com.networkprobe.core.domain.Command;
import com.networkprobe.core.domain.Networking;
import com.networkprobe.core.domain.Route;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.exception.InvalidPropertyException;
import com.networkprobe.core.util.Key;
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

    private YamlMapping yamlMapping;

    @Override
    public void load(File templateFile, Template template, ResponseEntityFactory responseEntityFactory) {
        try {

            yamlMapping = Yaml.createYamlInput(templateFile).readYamlMapping();
            template.clearTemplateSchema();
            loadNetworkingSettings(template);
            loadRouteSettings(template);
            loadCommandSettings(template, responseEntityFactory);

        } catch (Exception exception) {

            if (exception instanceof YamlReadingException)
                LOG.error("Houve um erro na hora de processar o arquivo de configuração Yaml.");

            else if (exception instanceof IOException || exception instanceof SecurityException)
                LOG.error("Houve um erro na hora de ler o arquivo de configuração Yaml.");

            else if (exception instanceof InvalidPropertyException)
                LOG.error("Foi identificado um parâmetro inválido dentro das configurações.");

            LOG.error("Houve um erro no carregamento do arquivo de template. Verifique: {}", exception.getMessage());
            ExceptionHandler.handleUnexpected(LOG, exception, Reason.NPS_FILE_LOADER_EXCEPTION);
        }
    }

    public void loadRouteSettings(Template template) throws JSONException, InvalidPropertyException {

        YamlMapping networkingMapping = yamlMapping.yamlMapping(Key.NETWORKING);

        template.getRoutes().put("any", Route.ANY);
        template.getRoutes().put("none", Route.NONE);

        YamlSequence routesSequence = networkingMapping.yamlSequence(Key.ROUTES);

        for (int i = 0; i < routesSequence.size(); i++) {

            YamlMapping routeMapping = routesSequence.yamlMapping(i);
            String name = routeMapping.string(Key.NAME);

            Route routeBuilt = new Route.Builder()
                    .cidr(routeMapping.string(Key.CIDR))
                    .name(name)
                    .get();

            template.getRoutes().put(name, routeBuilt);
        }

    }

    public void loadNetworkingSettings(Template template) {

        YamlMapping networkingMapping = yamlMapping.yamlMapping(Key.NETWORKING);
        YamlMapping discoveryServiceMapping = networkingMapping.yamlMapping("discoveryService");
        YamlMapping exchangeServiceMapping = networkingMapping.yamlMapping("exchangeService");

        template.configureNetworking(
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

    public void loadCommandSettings(Template template, ResponseEntityFactory responseEntityFactory) {

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
            if (routesSequence == null)
                throw new NullPointerException("Rotas não definidas para o comando \"" + commandName + "\"");

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

            for (CidrNotation cidrNotation : getAllValidRoutes(routesSequence, template)) {
                commandBuilder.network(cidrNotation);
            }

            template.getCommands().put(commandName, commandBuilder.get());
        }

    }

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

    @Override
    public String getAdapterName() {
        return getClass().getSimpleName();
    }

}
