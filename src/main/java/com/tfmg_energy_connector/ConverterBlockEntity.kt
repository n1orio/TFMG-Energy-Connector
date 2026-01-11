package com.tfmg_energy_connector

import com.drmangotea.tfmg.content.electricity.base.IElectric
import com.drmangotea.tfmg.content.electricity.base.ElectricBlockValues
import com.drmangotea.tfmg.content.electricity.base.ElectricalNetwork
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage

class ConverterBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(TfmgAe2Bridge.CONVERTER_BE_TYPE.get(), pos, state), IElectric {

    // 1. Хранилище FE для NeoForge/AE2
    val energyStorage = object : EnergyStorage(100, 100, 100) {
        override fun canReceive() = true
        override fun canExtract() = true
    }

    // 2. Данные TFMG.
    // ИСПРАВЛЕНО: Передаем Long (позицию), как того требует конструктор в твоей версии
    private val electricValues = ElectricBlockValues(pos.asLong())

    // --- РАЗРЕШЕНИЕ КОНФЛИКТОВ (Minecraft vs TFMG) ---

    // Исправляем ошибку "inherits multiple implementations"
    // Мы явно указываем, что используем позицию из BlockEntity
    override fun getBlockPos(): BlockPos {
        return worldPosition
    }

    // Исправляем ошибку getPos (теперь возвращает Long)
    override fun getPos(): Long {
        return worldPosition.asLong()
    }

    // --- РЕАЛИЗАЦИЯ ИНТЕРФЕЙСА IELECTRIC ---

    override fun getData(): ElectricBlockValues = electricValues

    override fun getLevelAccessor(): LevelAccessor = level!!

    override fun sendStuff() {
        setChanged()
        level?.sendBlockUpdated(worldPosition, blockState, blockState, 3)
    }

    override fun setNetwork(networkId: Long) {
        data.electricalNetworkId = networkId
    }

    override fun resistance(): Float = 20f

    override fun getPowerUsage(): Int {
        val space = energyStorage.maxEnergyStored - energyStorage.energyStored
        return if (space > 0) minOf(space, 1024) else 0
    }

    override fun voltageGeneration(): Int = 0
    override fun powerGeneration(): Int = 0

    override fun onNetworkChanged(oldVoltage: Int, oldPower: Int) {
        setChanged()
    }

    override fun setVoltage(v: Int) {
        data.voltage = v
    }

    override fun setNetworkResistance(r: Float) {
        data.networkResistance = r.toInt()
    }

    // --- ЛОГИКА ТИКОВ ---

    fun tick() {
        val lvl = level ?: return
        if (lvl.isClientSide || isRemoved) return

        // 1. Обработка электричества TFMG
        tickElectricity()
        if (lvl.gameTime % 20 == 0L) {
            lazyTickElectricity()
        }

        // 2. Потребление из TFMG (если есть напряжение)
        if (data.voltage > 0 && !data.notEnoughPower) {
            val powerToTake = getPowerUsage()
            if (powerToTake > 0) {
                energyStorage.receiveEnergy(powerToTake, false)

            }
        }

        // 3. Раздача в AE2
        if (energyStorage.energyStored > 0) {
            for (dir in Direction.entries) {
                val cap = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(dir), dir.opposite)
                cap?.let {
                    if (it.canReceive()) {
                        val push = energyStorage.extractEnergy(100, true)
                        val accepted = it.receiveEnergy(push, false)
                        energyStorage.extractEnergy(accepted, false)
                    }
                }
            }
        }
    }

    // --- СОХРАНЕНИЕ ---

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("fe_stored", energyStorage.energyStored)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        energyStorage.receiveEnergy(tag.getInt("fe_stored"), false)
    }
}