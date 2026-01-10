package com.tfmg_energy_connector;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mod(TfmgAe2Bridge.MODID)
public class TfmgAe2Bridge {
    public static final String MODID = "tfmg_energy_connector";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<Block> CONVERTER_BLOCK = BLOCKS.register("energy_converter",
            () -> new ConverterBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops()));

    public static final DeferredItem<Item> CONVERTER_ITEM = ITEMS.register("energy_converter",
            () -> new BlockItem(CONVERTER_BLOCK.get(), new Item.Properties()));

    public static final java.util.function.Supplier<BlockEntityType<ConverterBlockEntity>> CONVERTER_BE = BLOCK_ENTITIES.register("energy_converter",
            () -> BlockEntityType.Builder.of(ConverterBlockEntity::new, CONVERTER_BLOCK.get()).build(null));

    public TfmgAe2Bridge(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        // ВАЖНО: Регистрируем ModEvents ОДИН РАЗ здесь.
        // Это заставит работать и энергию, и вкладку без вылетов.
        modEventBus.register(ModEvents.class);
    }
}