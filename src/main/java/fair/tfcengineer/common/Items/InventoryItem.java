package fair.tfcengineer.common.Items;

import com.dunk.tfc.Core.TFC_Core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class InventoryItem implements IInventory {

    protected ItemStack[] itemStorage;
    protected ItemStack invItem;

    public InventoryItem(ItemStack stack) {
        itemStorage = new ItemStack[15];

        this.invItem = stack;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        readFromNBT(stack.getTagCompound());
    }

    public void tickContent(World world, int x, int y, int z, float decayFactor) {
        TFC_Core.handleItemTicking(itemStorage, world, x, y, z, decayFactor, false, false);
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
                itemStorage[i] = null;
            }
        }
        markDirty();
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
        return "Lunch box";
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
    public void markDirty() {
//        for (int i = 0; i < getSizeInventory(); i++) { // Makes sure that there is no invalid items in inventory
//            if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
//                itemStorage[i] = null;
//            }
//        }
        writeToNBT(invItem.getTagCompound());
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
        // Cannot place a lunchbox within a lunchbox
        return itemStack.getItem().getItemUseAction(itemStack) == EnumAction.eat;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList items = nbt.getTagList("itemStorage", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound data = items.getCompoundTagAt(i);
            int slot = data.getByte("slot") & 0xFF;
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(data));
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList items = new NBTTagList();
        int totalItems = 0;
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null) {
                totalItems++;
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("slot", (byte) i);
                getStackInSlot(i).writeToNBT(data);
                items.appendTag(data);
            }
        }
        nbt.setTag("itemStorage", items);
        nbt.setInteger("totalItems", totalItems);
    }
}
