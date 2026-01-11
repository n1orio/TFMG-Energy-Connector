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

    // Создание сущности блока
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ConverterBlockEntity(pos, state)
    }

    // Вызывается, когда блок ставят в мир
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos) as? ConverterBlockEntity
            // Регистрируем блок в электрической сети TFMG
        }
    }

    override fun appendHoverText(stack: net.minecraft.world.item.ItemStack, context: net.minecraft.world.item.Item.TooltipContext, tooltip: MutableList<net.minecraft.network.chat.Component>, flag: net.minecraft.world.item.TooltipFlag) {
        // Добавляем основное описание
        tooltip.add(Component.translatable("tooltip.tfmg_energy_connector.energy_converter.desc"))

        // Если нажат Shift, показываем подробности
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.tfmg_energy_connector.energy_converter.shift"))
        } else {
            tooltip.add(Component.literal("§8[Нажми Shift для подробностей]"))
        }
    }

    // Вызывается, когда блок ломают
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (state.block != newState.block) {
            if (!level.isClientSide) {
                val be = level.getBlockEntity(pos) as? ConverterBlockEntity
                // Удаляем блок из электрической сети TFMG, чтобы не было ошибок
                be?.onRemoved()
            }
            super.onRemove(state, level, pos, newState, isMoving)
        }
    }

    // Взаимодействие (Правая кнопка мыши)
    // В NeoForge 1.21.1 этот метод должен быть protected
    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hit: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val be = level.getBlockEntity(pos) as? ConverterBlockEntity
            if (be != null) {
                // ТЕСТ: Если игрок присел (Shift), заряжаем блок вручную
                if (player.isCrouching) {
                    be.energyStorage.receiveEnergy(10000, false)
                    player.sendSystemMessage(Component.literal("§e[Test] §fРучная зарядка: +10,000 FE"))
                } else {
                    val energy = be.energyStorage.energyStored
                    val max = be.energyStorage.maxEnergyStored
                    val voltage = be.data.voltage // Напряжение из TFMG

                    player.sendSystemMessage(Component.literal("§6--- [Energy Converter] ---"))
                    player.sendSystemMessage(Component.literal("§fЗапас FE: §a$energy §7/ §2$max"))
                    player.sendSystemMessage(Component.literal("§fНапряжение сети TFMG: §b$voltage V"))

                    if (be.data.notEnoughPower) {
                        player.sendSystemMessage(Component.literal("§c⚠ В сети TFMG не хватает мощности!"))
                    }
                }
            }
        }
        return InteractionResult.SUCCESS
    }

    // Подключаем "тик" (обновление каждый кадр)
    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (level.isClientSide) null
        else BlockEntityTicker { _, _, _, be -> (be as? ConverterBlockEntity)?.tick() }
    }
}