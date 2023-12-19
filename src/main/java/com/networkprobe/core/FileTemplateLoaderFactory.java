package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.exception.UnsupportedFileTypeException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Singleton(creationType = SingletonType.DYNAMIC, order = 50)
@Documented(done = false)
public class FileTemplateLoaderFactory {

    private final Map<String, Class<? extends FileTemplateLoader>> extensionLoaders = new HashMap<>();

    {
        extensionLoaders.put("json", JsonTemplateLoader.class);
        extensionLoaders.put("yaml", YamlTemplateLoader.class);
        extensionLoaders.put("yml", YamlTemplateLoader.class);
    }

    public FileTemplateLoaderFactory() {
        SingletonDirectory.denyInstantiation(this);
    }

    public FileTemplateLoader chooseLoaderFrom(File templateFile) {
        String fileExtensions = getExtension(templateFile).toLowerCase();
        Class<? extends FileTemplateLoader> templateLoaderClass = extensionLoaders.get(fileExtensions);
        checkIsLoaderFound(templateFile, templateLoaderClass);
        return SingletonDirectory.getSingleOf(templateLoaderClass);
    }

    private void checkIsLoaderFound(File templateFile, Class<? extends FileTemplateLoader> templateLoaderClass) {
        if (templateLoaderClass == null) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            extensionLoaders.keySet().forEach(stringJoiner::add);
            throw new UnsupportedFileTypeException("Não foi possível localizar um " +
                    "carregador para o tipo de template \"" + getExtension(templateFile) + "\". " +
                    "{ Tipos suportados: " + stringJoiner + " }");
        }
    }

    public String getExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static FileTemplateLoaderFactory getInstance() {
        return SingletonDirectory.getSingleOf(FileTemplateLoaderFactory.class);
    }

    private Map<String, Class<? extends FileTemplateLoader>> getExtensionLoaders() {
        return extensionLoaders;
    }

}
