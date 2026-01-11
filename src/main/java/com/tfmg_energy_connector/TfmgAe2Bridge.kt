package com.tfmg_energy_connector

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.capabilities.Capabilities
import java.util.function.Supplier

@Mod(TfmgAe2Bridge.MODID)
class TfmgAe2Bridge(bus: IEventBus) {
    companion object {
        const val MODID = "tfmg_energy_connector"

        val BLOCKS = DeferredRegister.createBlocks(MODID)
        val ITEMS = DeferredRegister.createItems(MODID)
        val BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID)

        val CONVERTER_BLOCK = BLOCKS.register("energy_converter", Supplier {
            ConverterBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops())
        })

        val CONVERTER_ITEM = ITEMS.register("energy_converter", Supplier {
            BlockItem(CONVERTER_BLOCK.get(), Item.Properties())
        })

        val CONVERTER_BE_TYPE = BLOCK_ENTITIES.register("energy_converter", Supplier {
            BlockEntityType.Builder.of({ p, s -> ConverterBlockEntity(p, s) }, CONVERTER_BLOCK.get()).build(null)
        })
    }

    init {
        BLOCKS.register(bus)
        ITEMS.register(bus)
        BLOCK_ENTITIES.register(bus)

        // Регистрация через стандартный addListener
        bus.addListener(this::registerCaps)
        bus.addListener(ModEvents::addCreative)
    }

    private fun registerCaps(event: RegisterCapabilitiesEvent) {
        // Мы регистрируем Capability максимально "глупым" способом, чтобы оно работало всегда
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            CONVERTER_BE_TYPE.get()
        ) { be, side ->
            // ЛОГ НА АНГЛИЙСКОМ (чтобы не было кракозябр)
            System.out.println("!!! [MOD LOG] TFMG IS ASKING FOR ENERGY AT SIDE: $side !!!")
            (be as ConverterBlockEntity).energyStorage
        }
    }
}