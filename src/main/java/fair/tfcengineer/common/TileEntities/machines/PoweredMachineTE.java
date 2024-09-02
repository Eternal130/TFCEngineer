package fair.tfcengineer.common.TileEntities.machines;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.lib.util.helpers.ServerHelper;
import fair.tfcengineer.common.TileEntities.MachineBaseTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class PoweredMachineTE extends MachineBaseTE implements IEnergyHandler {

    protected EnergyStorage energyStorage;
    private int energyUsedLastTick;
    private float workLastTick;
    private int lastEnergy, lastEnergyUsage, tickCounter;

    public PoweredMachineTE(int frontSide, EnergyStorage energyStorage) {
        super(frontSide);
        this.energyStorage = energyStorage;
        tickCounter = 0;
        lastEnergy = energyStorage.getEnergyStored();
    }

    public boolean shouldUpdateClients() {
        return lastEnergy != energyStorage.getEnergyStored() || lastEnergyUsage != energyUsedLastTick;
    }

    public void updateClients() {
        lastEnergy = energyStorage.getEnergyStored();
        lastEnergyUsage = energyUsedLastTick;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // Always set values before sending packet
    }

    @Override
    public void updateEntity() {
        if (ServerHelper.isClientWorld(worldObj)) return;

        tickCounter++;
        if (tickCounter >= 5) { // Check if client needs update ever 5 ticks
            tickCounter = 0;
            if (shouldUpdateClients()) {
                updateClients();
            }
        }

        float workAmount = getWorkAmount();
        workLastTick = workAmount;
        int runCost = getRunCost(workAmount);
        energyUsedLastTick = 0;

        if (isActive()) {
            energyUsedLastTick = runCost;
            energyStorage.modifyEnergyStored(-runCost);
            if (!canActivate()) setActive(false);
        }
    }

    public float getWorkAmount() {
        return 0;
    }

    public int getRunCost(float workAmount) {
        return 10; // RF per tick
    }

    @Override
    protected boolean canActivate() {
        return super.canActivate() && energyStorage.getEnergyStored() > 0;
    }

    public int getEnergyUsedLastTick() {
        return energyUsedLastTick;
    }

    public float getWorkLastTick() {
        return workLastTick;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return energyStorage.getMaxEnergyStored();
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energyStorage.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        energyStorage.writeToNBT(nbt);
    }

    protected void writeToPacket(NBTTagCompound nbt) {
        super.writeToPacket(nbt);
        nbt.setInteger("energyUsage", energyUsedLastTick);
    }

    protected void readFromPacket(NBTTagCompound nbt) {
        super.readFromPacket(nbt);
        energyUsedLastTick = nbt.getInteger("energyUsage");
    }
}
