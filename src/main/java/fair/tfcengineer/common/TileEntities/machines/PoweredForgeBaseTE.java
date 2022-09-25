package fair.tfcengineer.common.TileEntities.machines;

import cofh.api.energy.EnergyStorage;
import cofh.lib.util.helpers.ServerHelper;
import com.dunk.tfc.Core.TFC_Core;
import com.dunk.tfc.Food.ItemFoodTFC;
import com.dunk.tfc.Items.ItemBloom;
import com.dunk.tfc.Items.ItemMeltedMetal;
import com.dunk.tfc.Items.ItemOre;
import com.dunk.tfc.Items.ItemTerra;
import com.dunk.tfc.Items.Pottery.ItemPotteryMoldBase;
import com.dunk.tfc.Items.Pottery.ItemPotterySheetMold;
import com.dunk.tfc.api.HeatIndex;
import com.dunk.tfc.api.HeatRegistry;
import com.dunk.tfc.api.Interfaces.ISmeltable;
import com.dunk.tfc.api.TFCItems;
import com.dunk.tfc.api.TFC_ItemHeat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class PoweredForgeBaseTE extends PoweredMachineTE implements IInventory {

    protected ItemStack[] itemStorage;

    public PoweredForgeBaseTE(int frontSide, EnergyStorage energyStorage) {
        super(frontSide, energyStorage);
        itemStorage = new ItemStack[12];
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (ServerHelper.isClientWorld(worldObj)) return;

        setActive(getWorkLastTick() > 0); // Automatically activates when there's work to do

        for (int i = 0; i < 9; i++) {
            tickSlot(i);
            cookSlot(i, getCeramicMoldStack());
        }

        TFC_Core.handleItemTicking(this, worldObj, xCoord, yCoord, zCoord, false);
    }

    @Override
    public float getWorkAmount() {
        int workAmount = 0;
        for (int i = 0; i < 9; i++) {
            if (getStackInSlot(i) != null && HeatRegistry.getInstance().findMatchingIndex(getStackInSlot(i)) != null) workAmount++;
        }
        return workAmount;
    }

    // Returns the temperature after it has been heated this tick
    public float getIncreasedTemp(ItemStack itemStack, HeatIndex index, float curTemp) {
    	return curTemp;
    }

    public void tickSlot(int slot) {
        ItemStack is = getStackInSlot(slot);
        if (is != null) {
            HeatRegistry manager = HeatRegistry.getInstance();
            HeatIndex index = manager.findMatchingIndex(is);

            if (index != null) {
                float temp = TFC_ItemHeat.getTemp(is);
                if (isActive())TFC_ItemHeat.setTemp(is, getIncreasedTemp(is, index, temp),true);
                if (HeatRegistry.getInstance().isTemperatureWorkable(is) || HeatRegistry.getInstance().isTemperatureWeldable(is))
				{
					is = TFC_ItemHeat.setHoldTemperature(is);
				}
				else
				{
					is = TFC_ItemHeat.removeHoldTemperature(is);
				}
            }
        }
    }

    public void cookSlot(int slot, ItemStack moldItemStack) {
        ItemStack is = getStackInSlot(slot);
        if (is != null) {
            HeatRegistry manager = HeatRegistry.getInstance();
            HeatIndex index = manager.findMatchingIndex(is);
            if (index != null) {
                ItemStack isCopy = is.copy();
                float temp = TFC_ItemHeat.getTemp(is);

                if (temp > index.meltTemp) {
                    if (!(is.getItem() instanceof ItemMeltedMetal)) {
                        setInventorySlotContents(slot, index.getMorph());
                    }
                    if (getStackInSlot(slot) != null) {
                        HeatIndex morphIndex = manager.findMatchingIndex(getStackInSlot(slot));
                        if (morphIndex != null) {
                            // Apply old temperature to direct morphs that can continue to be heated.
                            TFC_ItemHeat.setTemp(getStackInSlot(slot), temp,true);
                        }
                    } else if (index.hasOutput()) {
                        ItemStack output = index.getOutput(isCopy, new Random());
                        if (isCopy.getItem() instanceof ISmeltable) {
                            ISmeltable isSmelt = (ISmeltable) isCopy.getItem();
                            int units = isSmelt.getMetalReturnAmount(isCopy);
    						if (isCopy.getItem() instanceof ItemBloom)
    							units = Math.min(100, units);

                            while (units > 0 && moldItemStack != null && moldItemStack.stackSize > 0) {
                                ItemStack outputCopy = new ItemStack(
    									isSmelt.getMetalType(isCopy).getResultFromMold(moldItemStack.getItem()));// meltedItem.copy();
                                TFC_ItemHeat.setTemp(outputCopy, temp,true);
    							outputCopy.setItemDamage(
    									isSmelt.getMetalType(isCopy).getBaseValueForResult(moldItemStack.getItem()));
    							((ItemPotteryMoldBase) (outputCopy.getItem())).setToMinimumUnits(outputCopy);
    							((ItemPotteryMoldBase) (outputCopy.getItem())).addUnits(outputCopy,
    									((ItemPotteryMoldBase) (moldItemStack.getItem())).getUnits(moldItemStack));

                                while (units > 100) { // If there is more than 1 output
                                    units-= 100;
                                    moldItemStack.stackSize--;
                                    fillEmptySlot(outputCopy);
                                } if (units > 0) { // Put the last item in the forge cooking slot, replacing the input
                                	((ItemPotteryMoldBase) (outputCopy.getItem())).addUnits(outputCopy, units);
                                    units = 0;
                                    moldItemStack.stackSize--;
                                    fillEmptySlot(outputCopy.copy());
                                }
                            }
                        } else {
                            setInventorySlotContents(slot, output);
                        }


                        if (TFC_ItemHeat.isCookable(is) > -1) {
                            // if the input is a new item, then apply the old temperature to it
                            TFC_ItemHeat.setTemp(is, temp);
                        }
                    }
                }
            }
        }
        
    }

    // Finds empty slot in forge main inventory and puts item there, or drops the item in front of the forge.
    private void fillEmptySlot(ItemStack is) {
        for (int i = 0; i < 9; i++) {
            if (getStackInSlot(i) == null && isItemValidForSlot(i, is)) {
                setInventorySlotContents(i, is);
                return;
            }
        }
        double xOff = 0.5d; double yOff = 1.5d; double zOff = 0.5d;
        if (frontSide == 2) { // North
            xOff = 0.5d; yOff = 0.5d; zOff = -0.5d;
        } else if (frontSide == 3) { // South
            xOff = 0.5d; yOff = 0.5d; zOff = 1.5d;
        } else if (frontSide == 4) { // West
            xOff = -0.5d; yOff = 0.5d; zOff = 0.5d;
        } else if (frontSide == 5) { // East
            xOff = 1.5d; yOff = 0.5d; zOff = 0.5d;
        }
        EntityItem ei = new EntityItem(worldObj, xCoord + xOff, yCoord + yOff, zCoord + zOff, is); // This needs to be in front of oven
        ei.motionX = 0;
        ei.motionY = 0;
        ei.motionZ = 0;
        worldObj.spawnEntityInWorld(ei);
    }

    public ItemStack getCeramicMoldStack() {
        for (int i = 9; i < 12; i++) {
            ItemStack is = getStackInSlot(i);
            if (is != null && is.getItem() == TFCItems.ceramicMold && is.getItemDamage() == 1) return is;
        }
        return null;
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
        return "Electric Forge";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
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
        if (itemStack.getItem() == TFCItems.ceramicMold && itemStack.getItemDamage() == 1) { // If the item is a mold only valid in mold slots
            return slot >= 9 && slot <= 11;
        }
        if (slot >= 9 && slot <= 11) return false;
        HeatRegistry manager = HeatRegistry.getInstance();
        return !(manager.findMatchingIndex(itemStack) == null
                || itemStack.getItem() instanceof ItemOre
                || itemStack.getItem() instanceof ItemFoodTFC);
//        return true;
    }


}
