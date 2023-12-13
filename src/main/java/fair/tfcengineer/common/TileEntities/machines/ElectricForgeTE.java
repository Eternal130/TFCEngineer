package fair.tfcengineer.common.TileEntities.machines;

import cofh.api.energy.EnergyStorage;
import com.dunk.tfc.api.HeatIndex;
import com.dunk.tfc.api.Interfaces.IHeatSourceTE;
import com.dunk.tfc.api.TFC_ItemHeat;
import fair.tfcengineer.TFCEConfigs;
import fair.tfcengineer.common.Network.MachineInteractPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ElectricForgeTE extends PoweredForgeBaseTE implements IHeatSourceTE {

    protected int heatingTemp;

    public ElectricForgeTE() { // Every tile entity have to have an empty constructor for loading
        this(2);
    }

    public ElectricForgeTE(int frontSide) {
        super(frontSide, new EnergyStorage((int) (4000 * TFCEConfigs.electricForgePowerMod), (int) (40 * TFCEConfigs.electricForgePowerMod)));
        heatingTemp = 1600;
    }

    public void interact(MachineInteractPacket packet) {
        if (packet.getFlag() == 0) { // Temperature up
            increaseTemperature();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        } else if (packet.getFlag() == 1) { // Temperature down
            decreaseTemperature();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    protected boolean canActivate() {
        return super.canActivate() && heatingTemp > 0;
    }

    public int getRunCost(float workAmount) {
        // The more it is heating, the more power it will take
        // Using a power function with fraction as power so the increase is lower the higher the number
        // With this function, heating 1 item takes 5 RF/t, heating 5 takes ~11 RF/t, heating 9 takes 15 RF/t
        // The heatingTemp is added to the power cost, so it will take more power the higher the temperature
        return (int) (Math.pow(workAmount, 0.5) * 5 * TFCEConfigs.electricForgePowerMod * (1 + heatingTemp / 2000f));
    }

    public float getIncreasedTemp(ItemStack itemStack, HeatIndex index, float curTemp) {
        if (curTemp < heatingTemp) {
            curTemp += TFC_ItemHeat.getTempIncrease(itemStack,heatingTemp) * TFCEConfigs.electricForgeHeatRate;
            if (curTemp > heatingTemp) curTemp = heatingTemp;
        }
//        if (curTemp > index.meltTemp - 1f) curTemp = index.meltTemp - 1f;
        return curTemp;
    }

    public int getHeatingTemperature() {
        return heatingTemp;
    }

    public void setHeatingTemperature(int heatingTemp) {
        this.heatingTemp = Math.min(Math.max(heatingTemp, 0), 2000);
        markDirty();
    }

    public void increaseTemperature() {
        setHeatingTemperature(getHeatingTemperature() + 50);
    }

    public void decreaseTemperature() {
        setHeatingTemperature(getHeatingTemperature() - 50);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        heatingTemp = nbt.getInteger("heatingTemp");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("heatingTemp", heatingTemp);
    }

    @Override
    public String getInventoryName() {
        return "Electric Forge";
    }

    @Override
    public float getHeatSourceTemp() {
        if(isActive()) return getHeatingTemperature();
        else return 0;
    }
}
