package com.networkprobe.core.entity;

import com.networkprobe.core.SingletonType;
import com.networkprobe.core.Template;
import com.networkprobe.core.annotation.CommandEntity;
import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.caching.CachedResponseEntity;
import com.networkprobe.core.caching.CachedValue;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.model.Command;
import com.networkprobe.core.model.Key;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@CommandEntity(commandName = "cacheMetadata")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
public class NPSMetadataResponseEntity implements ResponseEntity<String> {

    @ManagedDependency
    private Template template;

    @Override
    public String getContent(List<String> arguments) {
        return createCommandsMetadata().toString();
    }

    private JSONArray createCommandsMetadata() {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Command> entry : template.getCommands().entrySet()) {
            Command command = entry.getValue();
            ResponseEntity<?> responseEntity = command.getResponse();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Key.NAME, entry.getKey());
            jsonObject.put(Key.CACHED_ONCE, responseEntity.isCachedOnce());
            jsonObject.put("responseEntityType", responseEntity.getClass().getSimpleName());
            if (responseEntity instanceof CachedResponseEntity) {
                final CachedValue cachedValue = ((CachedResponseEntity) responseEntity).getCachedValue();
                jsonObject.put("responseEntityCacheElapsedTime",
                        ((CachedResponseEntity) responseEntity).getElapsedTime());
                JSONObject cachedValueJson = new JSONObject();
                cachedValueJson.put("currentValue", cachedValue.getValue());
                cachedValueJson.put("timestamp", cachedValue.getTimestamp());
                jsonObject.put("cachingInformation", cachedValueJson);
            }
            jsonObject.put("tags", command.getTags());
            jsonObject.put("routes", command.getRoutes());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public boolean isCachedOnce() {
        return false;
    }

    @Override
    public String getRawContent() {
        return "{}";
    }
}
