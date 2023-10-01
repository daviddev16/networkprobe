package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.DependencyException;
import com.networkprobe.core.exception.UnsupportedFileTypeException;

import java.io.File;

@Singleton(creationType = SingletonType.DYNAMIC, order = 1200)
public class FileTemplateLoaderFactory {

    public FileTemplateLoader chooseLoaderFrom(File file) {

        if (file.getName().endsWith(".json"))
            return SingletonDirectory.getSingleOf(JsonTemplateLoader.class);

        else if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml"))
          return SingletonDirectory.getSingleOf(YamlTemplateLoader.class);

        throw new UnsupportedFileTypeException("Não foi possível localizar um " +
                "configurador para o tipo de template \"" + getExtension(file) + "\". " +
                "{ Tipos suportados: YML/JSON }");
    }

    public String getExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf('.'), fileName.length());
    }

    public static FileTemplateLoaderFactory getFactory() {
        return SingletonDirectory.getSingleOf(FileTemplateLoaderFactory.class);
    }

}
