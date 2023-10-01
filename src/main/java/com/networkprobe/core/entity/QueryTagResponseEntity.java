package com.networkprobe.core.entity;

import com.networkprobe.core.SingletonType;
import com.networkprobe.core.Template;
import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.exception.ClientRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Singleton(creationType = SingletonType.DYNAMIC, order = 201)
public class QueryTagResponseEntity implements ResponseEntity<String> {

    @ManagedDependency
    private Template template;

    @Override
    public String getContent(List<String> arguments) {

        if (arguments.isEmpty())
            throw new ClientRequestException("Não foi informado tags para pesquisa.");

        JSONArray jsonArray = new JSONArray();

        template.getCommands()
                .values()
                .parallelStream()
                .filter(cmd -> {
                    for (String tag : arguments) {
                        return cmd.getTags().contains(tag);
                    }
                    return false;
                }).forEach(cmd ->
                        jsonArray.put(cmd.getName()));

        return new JSONObject()
                .put("query_commands", jsonArray)
                .toString();
    }

    @Override
    public String getRawContent() {
        return "{}";
    }

    @Override
    public boolean isCachedOnce() {
        return false;
    }
}
