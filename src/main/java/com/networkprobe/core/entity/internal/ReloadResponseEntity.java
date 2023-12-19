package com.networkprobe.core.entity.internal;

import com.networkprobe.core.BaseFileTemplateAdapter;
import com.networkprobe.core.SingletonDirectory;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.miscs.Feature;
import com.networkprobe.core.annotation.reflections.CommandEntity;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.entity.base.ResponseEntity;

import java.util.List;

@CommandEntity(commandName = "reloadSchema")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
@Feature(notImplemented = false)
public class ReloadResponseEntity implements ResponseEntity<String> {

    @Override
    public String getContent(List<String> arguments) {
        long startTime = System.currentTimeMillis();
        SingletonDirectory.getSingleOf(BaseFileTemplateAdapter.class).reload();
        return "Done in " + (System.currentTimeMillis() - startTime) + "ms";
    }

    @Override
    public boolean isCachedOnce() {
        return false;
    }

    @Override
    public String getRawContent() {
        return "";
    }

}
