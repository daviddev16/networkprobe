package com.networkprobe.core.configurator;

import com.networkprobe.core.SingletonType;
import com.networkprobe.core.annotation.Singleton;
import com.networkprobe.core.model.Networking;
import com.networkprobe.core.util.Utility;
import com.networkprobe.core.util.Validator;

import java.net.*;
import java.util.*;

import static com.networkprobe.core.util.Utility.getMacAddress;

@Singleton(creationType = SingletonType.DYNAMIC, order = 2000)
public class TestingConfigurator {

    public TestingConfigurator()
    {
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getHardwareAddress() == null)
                    continue;
                System.out.println(networkInterface.getName() + ": " + getMacAddress(networkInterface.getHardwareAddress()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
