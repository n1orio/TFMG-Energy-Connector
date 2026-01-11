package com.tfmg_energy_connector

import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

object ModEvents {
    fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            val item: Item = TfmgAe2Bridge.CONVERTER_ITEM.get()
            event.accept(item)
        }
    }
}