package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.exception.ClientRequestException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.networkprobe.core.util.Utility.convertJsonArrayToList;

@Documented(done = false)
public class CommandRequest {

    public static final String KEY_CMD = "cmd";
    public static final String KEY_ARGUMENTS = "arguments";

    private String command;
    private List<String> arguments;

    public CommandRequest(String command, List<String> arguments) {
        if (command == null || command.isEmpty())
            throw new ClientRequestException("Requisição não reconhecida. " +
                    "A chave de informação do comando é inválida.");
        this.command = command;
        this.arguments = arguments;
    }

    public CommandRequest(String content) {
        try {
            loadFromJson(new JSONObject(content));
        } catch (JSONException e) {
            throw new ClientRequestException("O corpo da requisição não é " +
                    "um JSON válido.");
        }
    }

    public CommandRequest(JSONObject jsonObject) {
        loadFromJson(jsonObject);
    }

    private void loadFromJson(JSONObject jsonObject) {
        this.command = jsonObject.optString(KEY_CMD);
        this.arguments = convertJsonArrayToList(jsonObject
                .optJSONArray(KEY_ARGUMENTS), String.class);
    }

    public List<String> arguments() {
        return arguments;
    }

    public String command() {
        return command;
    }
}