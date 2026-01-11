package com.tfmg_energy_connector

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.DeferredHolder
import java.util.function.Supplier

@Mod(TfmgAe2Bridge.MODID)
class TfmgAe2Bridge(bus: IEventBus) {
    companion object {
        const val MODID = "tfmg_energy_connector"

        val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)
        val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MODID)
        val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID)

        val CONVERTER_BLOCK = BLOCKS.register("energy_converter", Supplier {
            ConverterBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops())
        })

        val CONVERTER_ITEM = ITEMS.register("energy_converter", Supplier {
            BlockItem(CONVERTER_BLOCK.get(), Item.Properties())
        })

        val CONVERTER_BE_TYPE: DeferredHolder<BlockEntityType<*>, BlockEntityType<ConverterBlockEntity>> =
            BLOCK_ENTITIES.register("energy_converter", Supplier {
                BlockEntityType.Builder.of({ p, s -> ConverterBlockEntity(p, s) }, CONVERTER_BLOCK.get()).build(null)
            })
    }

    init {
        BLOCKS.register(bus)
        ITEMS.register(bus)
        BLOCK_ENTITIES.register(bus)

        // Регистрируем наш отдельный файл с событиями
        bus.register(ModEvents)
    }
}