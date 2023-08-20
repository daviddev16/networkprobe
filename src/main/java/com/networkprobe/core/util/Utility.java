package com.networkprobe.core.util;

import java.io.Closeable;

public class Utility {

    public static void closeQuietly(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null)
                    closeable.close();
            }
        } catch (Exception ignored) {}
    }

}
