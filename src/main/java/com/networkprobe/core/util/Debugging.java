package com.networkprobe.core.util;

import com.networkprobe.core.NetworkProbeOptions;
import com.networkprobe.core.annotation.miscs.Documented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Documented(done = false)
public final class Debugging {

    private static final Logger LOG = LoggerFactory.getLogger(Debugging.class);

    public static void log(Logger logger, String message, Object... args) {
        if (NetworkProbeOptions.isDebugSocketEnabled())
            logger.debug(message, args);
    }

    public static void log(Logger logger, String message) {
        log(logger, message, new Object[] {});
    }

    public static void log(String message) {
        log(LOG, message);
    }

}
