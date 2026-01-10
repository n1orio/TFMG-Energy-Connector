package com.tfmg_energy_connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ConverterBlockEntity extends BlockEntity {

    // Хранилище: 100,000 емкость, 10,000 вход/выход
    private final EnergyStorage energyStorage = new EnergyStorage(100000, 10000, 10000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            setChanged(); // Сохраняем блок при изменении энергии
            return super.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            setChanged();
            return super.extractEnergy(maxExtract, simulate);
        }
    };

    public ConverterBlockEntity(BlockPos pos, BlockState blockState) {
        super(TfmgAe2Bridge.CONVERTER_BE.get(), pos, blockState);
    }

    // Этот метод вызывается 20 раз в секунду
    public void tick() {
        if (level == null || level.isClientSide) return;

        // Если есть энергия, пытаемся отдать её соседям
        if (energyStorage.getEnergyStored() > 0) {
            pushEnergyToNeighbors();
        }
    }

    private void pushEnergyToNeighbors() {
        // Проходим по всем 6 сторонам (Верх, Низ, Север, Юг...)
        for (Direction direction : Direction.values()) {
            // Если энергия кончилась - выходим
            if (energyStorage.getEnergyStored() <= 0) return;

            BlockPos neighborPos = worldPosition.relative(direction);

            // Ищем у соседа способность принимать энергию (FE)
            IEnergyStorage neighborEnergy = level.getCapability(Capabilities.Energy.BLOCK, neighborPos, direction.getOpposite());

            if (neighborEnergy != null && neighborEnergy.canReceive()) {
                // Сколько можем отдать? (Макс 1000 за тик или сколько есть)
                int maxPush = Math.min(energyStorage.getEnergyStored(), 1000);

                // Пробуем залить соседу
                int accepted = neighborEnergy.receiveEnergy(maxPush, false);

                // Убираем у себя то, что сосед принял
                energyStorage.extractEnergy(accepted, false);
            }
        }
    }

    // Сохранение при выходе из игры
    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energyStorage.getEnergyStored());
    }

    // Загрузка при входе
    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int energy = tag.getInt("Energy");
        // Сброс и установка (хак для EnergyStorage)
        energyStorage.extractEnergy(Integer.MAX_VALUE, false);
        energyStorage.receiveEnergy(energy, false);
    }

    // Геттер для регистрации Capability
    public IEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }
}