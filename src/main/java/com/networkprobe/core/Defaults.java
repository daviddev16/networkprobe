package com.networkprobe.core;

import com.networkprobe.core.model.Key;

import java.util.ArrayList;
import java.util.List;

public class Defaults {

    public static final List<String> COMMANDS = new ArrayList<String>()
    {
        { add(Key.CMD_UNAUTHORIZED); add(Key.CMD_UNKNOWN); }
    };

    public static final String NPS_VERSION = "1.7-SNAPSHOT";

}
