package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;

import java.io.File;

@Documented(done = false)
public interface FileTemplateLoader {

    void onSuccessfully();

    String getAdapterName();

    void load(File templateFile, Template template, ResponseEntityFactory responseEntityFactory);

}
