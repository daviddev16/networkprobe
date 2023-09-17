package com.networkprobe.core;

import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.DependencyException;

import java.io.File;

@Singleton(creationType = SingletonType.DYNAMIC, order = 1200)
public class FileTemplateLoaderFactory {

    public FileTemplateLoader chooseLoaderFrom(File file) {

        if (file.getName().endsWith(".json"))
            return SingletonDirectory
                    .getSingleOf(JsonTemplateLoader.class);

        else if (file.getName().endsWith(".yaml"))
            return SingletonDirectory
                    .getSingleOf(YamlTemplateLoader.class);

        throw new DependencyException("Não foi possível localizar um " +
                "configurador para este tipo de arquivo.");
    }

    public static FileTemplateLoaderFactory getFactory() {
        return SingletonDirectory.getSingleOf(FileTemplateLoaderFactory.class);
    }

}
