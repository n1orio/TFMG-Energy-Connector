package com.tfmg_energy_connector;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

// МЫ УДАЛИЛИ ОТСЮДА @EventBusSubscriber, чтобы не было двойной регистрации
public class ModEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Регистрация возможности принимать энергию
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                TfmgAe2Bridge.CONVERTER_BE.get(),
                (be, context) -> be.getEnergyStorage()
        );
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Добавляем блок в инвентарь креатива
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(TfmgAe2Bridge.CONVERTER_ITEM.get());
        }
    }
}