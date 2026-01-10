package com.tfmg_energy_connector;

public class ModEvents {
}
package com.tfmg_energy_connector;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = TfmgAe2Bridge.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Говорим игре, что наш блок имеет энергию
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                TfmgAe2Bridge.CONVERTER_BE.get(),
                (blockEntity, context) -> blockEntity.getEnergyStorage()
        );
    }
}