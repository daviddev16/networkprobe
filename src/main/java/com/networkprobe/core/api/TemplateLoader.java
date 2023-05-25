package com.networkprobe.core.api;

import java.io.File;

public interface TemplateLoader extends Template {

    void load(File file, ResponseEntityFactory responseEntityFactory);

}
