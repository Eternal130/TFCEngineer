package fair.tfcengineer.common.TileEntities.machines;

import cofh.api.energy.EnergyStorage;
import com.dunk.tfc.api.HeatIndex;
import com.dunk.tfc.api.Interfaces.IHeatSourceTE;
import com.dunk.tfc.api.TFC_ItemHeat;
import fair.tfcengineer.TFCEConfigs;
import fair.tfcengineer.common.Network.MachineInteractPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InductionForgeTE extends PoweredForgeBaseTE implements IHeatSourceTE {

    private int mode;

    public InductionForgeTE() { // Every tile entity have to have an empty constructor for loading
        this(2);
    }

    public InductionForgeTE(int frontSide) {
        super(frontSide, new EnergyStorage((int) (8000 * TFCEConfigs.inductionForgePowerMod), (int) (80 * TFCEConfigs.inductionForgePowerMod)));
        setModeWork();
    }

    public void interact(MachineInteractPacket packet) {
        if (packet.getFlag() == 0) { // Set mode work
            setModeWork();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        } else if (packet.getFlag() == 1) { // Set mode melt
            setModeMelt();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public int getRunCost(float workAmount) {
        // The more it is heating, the more power it will take
        // Using a power function with fraction as power so the increase is lower the higher the number
        // With this function, heating 1 item takes 10 RF/t, heating 5 takes ~22 RF/t, heating 9 takes 30 RF/t
        // The heatingTemp is 2000, so it takes 2 times more power than the electric forge
        return (int) (Math.pow(workAmount, 0.5) * 10 * TFCEConfigs.inductionForgePowerMod * 2);
    }

    public float getIncreasedTemp(ItemStack itemStack, HeatIndex index, float curTemp) {
        if (mode == 0) {
            curTemp += TFC_ItemHeat.getTempIncrease(itemStack,2000.0F) * TFCEConfigs.inductionForgeHeatRate;
            if (curTemp > index.meltTemp - 1f) curTemp = index.meltTemp - 1f;
        } else if (mode == 1) {
            curTemp += TFC_ItemHeat.getTempIncrease(itemStack,2000.0F) * TFCEConfigs.inductionForgeHeatRate;
            if (curTemp > index.meltTemp + 1) curTemp = index.meltTemp + 1;
        }
        return curTemp;
    }

    public void setModeWork() {
        mode = 0;
    }

    public void setModeMelt() {
        mode = 1;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        mode = nbt.getInteger("mode");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("mode", mode);
    }

    @Override
    public String getInventoryName() {
        return "Induction Forge";
    }

    @Override
    public float getHeatSourceTemp() {
        if(isActive()) return 2000;
        else return 0;
    }
}
