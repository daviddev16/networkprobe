package com.networkprobe.core.configurator;

import com.networkprobe.core.*;
import com.networkprobe.core.annotation.Singleton;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;

/*
* Configurator's
* Configuradores serão usados para configurar trechos de códigos dinâmicamente, sem poluir o Launcher
* */
@Singleton(creationType = SingletonType.DYNAMIC, order = 2000)
public class InventoryConfigurator {

    public InventoryConfigurator() {
        try {
            configure();
        } catch (InstanceAlreadyExistsException | IllegalAccessException e) {
            ExceptionHandler.handleUnexpected(LoggerFactory
                    .getLogger(InventoryConfigurator.class), e,
                    Reason.NPS_CONFIGURATOR_EXCEPTION);
        }
    }

    private void configure()
            throws InstanceAlreadyExistsException, IllegalAccessException
    {
        ClassMapperHandler.getInstance().extract(UsableNetworkDataInventory.getInventory());
    }

}
