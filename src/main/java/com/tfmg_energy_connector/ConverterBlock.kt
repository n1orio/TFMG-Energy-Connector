package com.tfmg_energy_connector

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.jetbrains.annotations.Nullable

class ConverterBlock(properties: Properties) : Block(properties), EntityBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ConverterBlockEntity(pos, state)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos) as? ConverterBlockEntity
        }
    }

    override fun appendHoverText(stack: net.minecraft.world.item.ItemStack, context: net.minecraft.world.item.Item.TooltipContext, tooltip: MutableList<net.minecraft.network.chat.Component>, flag: net.minecraft.world.item.TooltipFlag) {
        tooltip.add(Component.translatable("tooltip.tfmg_energy_connector.energy_converter.desc"))

        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.tfmg_energy_connector.energy_converter.shift"))
        } else {
            tooltip.add(Component.literal("§8[Нажми Shift для подробностей]"))
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (state.block != newState.block) {
            if (!level.isClientSide) {
                val be = level.getBlockEntity(pos) as? ConverterBlockEntity
                be?.onRemoved()
            }
            super.onRemove(state, level, pos, newState, isMoving)
        }
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (level.isClientSide) null
        else BlockEntityTicker { _, _, _, be -> (be as? ConverterBlockEntity)?.tick() }
    }
}