package com.tfmg_energy_connector

import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

object ModEvents {

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            TfmgAe2Bridge.CONVERTER_BE_TYPE.get()
        ) { be, _ -> (be as ConverterBlockEntity).energyStorage }
    }

    @SubscribeEvent
    fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            val item: Item = TfmgAe2Bridge.CONVERTER_ITEM.get()
            event.accept(item)
        }
    }
}