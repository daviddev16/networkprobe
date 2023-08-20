package com.networkprobe.core.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class IOUtil {

    public static @NotNull String readFile(File file) throws IOException {
        Validator.checkIsReadable(file, file.getName());
        return String.join("\n", Files.readAllLines(Paths.get(file.toURI()))).trim();
    }


}
