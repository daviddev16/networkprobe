package com.networkprobe.alterdata;

import com.networkprobe.core.SingletonDirectory;
import com.networkprobe.core.SingletonType;
import com.networkprobe.core.UsableNetworkDataInventory;
import com.networkprobe.core.annotation.miscs.Defintion;
import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.reflections.ClassInventory;
import com.networkprobe.core.annotation.reflections.Data;
import com.networkprobe.core.annotation.reflections.Handled;
import com.networkprobe.core.annotation.reflections.Singleton;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.lang.String.*;

/**
 * MISC: Essa classe é responsável por gerar funções que criam a configuração ERP.INI e envia
 * para o cliente em forma de base64 para ser decodificado e transformado em arquivo.
 * */
@ClassInventory
@Singleton(creationType = SingletonType.DYNAMIC, order = -449)
@Documented(done = false)
public class BimerInventory {

    @Handled
    private UsableNetworkDataInventory networkDataInventory;

    @Data(name = "GetDynamicBimerBase64Data")
    @Defintion("${GetDynamicBimerBase64Data|<MAC_ADDRESS>|<INET4_INDEX>|<CONNECTION_NAME>|<DATABASE_NAME>}")
    public String getDynamicBimerBase64Data(String macAddress, int addressIndex,
                                     String connectionName, String databaseName) {

        String usedAddress = networkDataInventory.getActiveAddress(macAddress, addressIndex, "IPV4");
        return getBimerBase64Data(usedAddress, connectionName, databaseName);
    }

    @Data(name = "GetBimerBase64Data")
    @Defintion("${GetBimerBase64Data|<SERVER_ADDRESS>|<CONNECTION_NAME>|<DATABASE_NAME>}")
    public String getBimerBase64Data(String serverAddress, String connectionName, String databaseName) {
        return Base64.getEncoder().encodeToString(
                createBimerIniStructure(serverAddress, connectionName, databaseName)
                        .getBytes(StandardCharsets.UTF_8));
    }

    private String createBimerIniStructure(String serverAddress, String connectionName, String databaseName) {
        return new StringBuilder()
                .append(format("[%s]\n", connectionName))
                .append(format("SERVER NAME=%s\n", serverAddress))
                .append(format("DATABASE NAME=%s\n", databaseName))
                .append("[DEFAULT]\n")
                .append(format("Connection=%s\n", connectionName))
                .toString();
    }

}
