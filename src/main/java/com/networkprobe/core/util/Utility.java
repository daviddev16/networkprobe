package com.networkprobe.core.util;

import com.networkprobe.core.config.model.Cidr;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.networkprobe.core.util.Validator.checkIsNotNull;

public final class Utility {

    public static @NotNull String readFile(File file) throws IOException {
        Validator.checkIsReadable(file, file.getName());
        return String.join("\n", Files.readAllLines(Paths.get(file.toURI()))).trim();
    }

    @Contract("_ -> new")
    public static @NotNull File toFile(String pathname) {
        return new File(Validator.checkIsNullOrEmpty(pathname, "pathname"));
    }

    @Contract("_ -> new")
    public static @NotNull JSONObject toJson(File file) throws IOException {
        return new JSONObject(readFile(file));
    }

    public static @NotNull String exceptionShortDescription(Exception exception) {
        checkIsNotNull(exception, "exception");
        return "[exception='" + exception.getClass().getSimpleName()
                + "', message='" + exception.getMessage() + "']";
    }

    public static @NotNull String sanitize(String str) {
        checkIsNotNull(str, "str");
        return str
                .replaceAll("[^a-zA-Z0-9_]", "") /* clear espcial chars */
                .replaceAll("\\s+", ""); /* remove blank spaces */
    }

}
