package fair.tfcengineer.common.TileEntities.machines;

import cofh.api.energy.EnergyStorage;
import cofh.lib.util.helpers.ServerHelper;
import com.dunk.tfc.Core.TFC_Core;
import com.dunk.tfc.api.HeatIndex;
import com.dunk.tfc.api.HeatRegistry;
import com.dunk.tfc.api.TFC_ItemHeat;
import fair.tfcengineer.TFCEConfigs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class FridgeBaseTE extends PoweredMachineTE implements IInventory {

    protected ItemStack[] itemStorage;
    private float decayRateLastTick;

    public FridgeBaseTE(int frontSide, EnergyStorage energyStorage) {
        super(frontSide, energyStorage);
        itemStorage = new ItemStack[28];
    }

    public float getDecayRate() {
        return 1f;
    }

    public float getDecayRateLastTick() {
        return decayRateLastTick;
    }

    public int getRunCost(float workAmount) {
        return 5; // RF per tick
    }

    public boolean shouldUpdateClients() {
        return super.shouldUpdateClients() || decayRateLastTick != getDecayRate();
    }

    public void updateClients() {
        decayRateLastTick = getDecayRate();
        super.updateClients(); // Always set values before sending packet
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (ServerHelper.isClientWorld(worldObj)) return;

        decayRateLastTick = getDecayRate();

        setActive(true); // Always active

        // Heat items in the fridge will decrease in heat 20 times faster
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack is = getStackInSlot(i);
            if (is != null) {
                HeatRegistry manager = HeatRegistry.getInstance();
                HeatIndex index = manager.findMatchingIndex(is);

                if (index != null) {
                    float temp = TFC_ItemHeat.getTemp(is);
                    if (isActive()){
                        TFC_ItemHeat.setTemp(is, temp - TFC_ItemHeat.getTempDecrease(is,0.0F) * 20f);
                        if(temp<10) is.getTagCompound().removeTag("temperature");
                    }
                }
            }
        }

        TFC_Core.handleItemTicking(this, worldObj, xCoord, yCoord, zCoord, getDecayRate(), false);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        NBTTagList items = nbt.getTagList("itemStorage", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound data = items.getCompoundTagAt(i);
            int slot = data.getByte("slot") & 0xFF;
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(data));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("slot", (byte) i);
                getStackInSlot(i).writeToNBT(data);
                items.appendTag(data);
            }
        }
        nbt.setTag("itemStorage", items);
    }

    @Override
    public int getSizeInventory() {
        return itemStorage.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemStorage[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (itemStorage[slot] != null) {
            if (itemStorage[slot].stackSize <= amount)
            {
                ItemStack itemstack = itemStorage[slot];
                itemStorage[slot] = null;
                markDirty();
                return itemstack;
            }
            ItemStack newStack = itemStorage[slot].splitStack(amount);
            if (itemStorage[slot].stackSize == 0)
                itemStorage[slot] = null;
            markDirty();
            return newStack;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack out = getStackInSlot(slot);
        setInventorySlotContents(slot, null);
        return out;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        itemStorage[slot] = itemStack;
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "Fridge";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return true;
    }

    protected void writeToPacket(NBTTagCompound nbt) {
        super.writeToPacket(nbt);
        nbt.setFloat("decayRate", decayRateLastTick);
    }

    protected void readFromPacket(NBTTagCompound nbt) {
        super.readFromPacket(nbt);
        decayRateLastTick = nbt.getFloat("decayRate");
    }

}
