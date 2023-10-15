package com.networkprobe.core.entity;

import com.networkprobe.core.SingletonType;
import com.networkprobe.core.Template;
import com.networkprobe.core.annotation.CommandEntity;
import com.networkprobe.core.annotation.ManagedDependency;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.entity.base.ResponseEntity;
import com.networkprobe.core.exception.ClientRequestException;
import com.networkprobe.core.model.Command;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

@CommandEntity(commandName = "queryTags")
@Singleton(creationType = SingletonType.DYNAMIC, order = 200)
public class QueryTagResponseEntity implements ResponseEntity<String> {

    @ManagedDependency
    private Template template;

    @Override
    public String getContent(List<String> arguments) {

        if (arguments.isEmpty())
            throw new ClientRequestException("Não foi informado tags para pesquisa.");

        JSONArray jsonArray = new JSONArray();

        for (Command command : template.getCommands().values()) {
            if (containsTag(command, arguments)) {
                jsonArray.put(command.getName());
            }
        }

        return new JSONObject()
                .put("commands", jsonArray)
                .toString();
    }

    private boolean containsTag(Command command, List<String> arguments) {
        for (String argumentsTag : arguments) {
            if (command.getTags().contains(argumentsTag))
                return true;
        }
        return false;
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
