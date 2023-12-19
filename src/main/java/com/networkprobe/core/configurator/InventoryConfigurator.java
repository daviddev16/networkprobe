package com.networkprobe.core.configurator;

import com.networkprobe.core.*;
import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.annotation.miscs.Replaced;
import com.networkprobe.core.annotation.reflections.Singleton;
import com.networkprobe.core.configurator.Configurable;

@Documented
@Replaced(
        newer = SingletonDirectory.class,
        since = "1.7-SNAPSHOT",
        reference = {
                "InventoryConfigurator não será mais usado para extrair as funções de UsableNetworkDataInventory.",
                "A partir da versão 1.8, a inicialização de Data Inventories é feita pelo SingletonDirectory, através",
                "De instânciação de objetos e injeção de dependências."})

@Singleton(creationType = SingletonType.DYNAMIC, order = 300, enabled = false)
public class InventoryConfigurator implements Configurable {

    public InventoryConfigurator() {
        SingletonDirectory.denyInstantiation(this);
    }

    /**
     * Utiliza o {@link ClassMapperHandler} para mapear/extrair as funções da classe: {@link UsableNetworkDataInventory}
     * @throws Exception Caso ocorra algum erro na extração das funções da classe informada.
     * */
    public void configure() throws Exception {
       ClassMapperHandler.getInstance()
               .extract(UsableNetworkDataInventory.getInventory());
    }

}
