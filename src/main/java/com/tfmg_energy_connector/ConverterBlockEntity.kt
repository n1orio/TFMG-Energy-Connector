package com.tfmg_energy_connector

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage

class ConverterBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(TfmgAe2Bridge.CONVERTER_BE_TYPE.get(), pos, state) {

    val energyStorage = object : EnergyStorage(100000, 10000, 10000) {
        override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
            val res = super.receiveEnergy(maxReceive, simulate)
            if (res > 0 && !simulate) setChanged()
            return res
        }
    }

    fun tick() {
        val lvl = level ?: return
        if (lvl.isClientSide || isRemoved) return

        if (energyStorage.energyStored > 0) {
            for (dir in Direction.entries) {
                val cap = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(dir), dir.opposite)
                if (cap != null && cap.canReceive()) {
                    val push = energyStorage.extractEnergy(1000, true)
                    val accepted = cap.receiveEnergy(push, false)
                    energyStorage.extractEnergy(accepted, false)
                }
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("Energy", energyStorage.energyStored)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        energyStorage.receiveEnergy(tag.getInt("Energy"), false)
    }
}