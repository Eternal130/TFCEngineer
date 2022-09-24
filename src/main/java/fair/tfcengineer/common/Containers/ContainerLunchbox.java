package fair.tfcengineer.common.Containers;

import com.dunk.tfc.Containers.ContainerTFC;
import com.dunk.tfc.Core.Player.PlayerInventory;
import fair.tfcengineer.common.Items.InventoryItem;
import fair.tfcengineer.common.Items.ItemLunchbox;
import fair.tfcengineer.common.Items.TFCEItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class ContainerLunchbox extends ContainerTFC {

    protected InventoryItem item;

    public ContainerLunchbox(IInventory playerInv, InventoryItem item) {
        this.item = item;

        // Tile Entity, Slot 0-14, Slot IDs 0-14
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 5; x++) {
                addSlotToContainer(new LunchBoxSlot(item, y * 5 + x, 44 + x * 18, 17 + y * 18));
            }
        }

        // Player Inventory, Slot 9-35, Slot IDs 15-41
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player Inventory (hotbar), Slot 0-8, Slot IDs 42-51
        for (int x = 0; x < 9; ++x) {
            addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return item.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlotTFC(EntityPlayer player, int fromSlot) {
        ItemStack prev = null;
        Slot slot = (Slot) inventorySlots.get(fromSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack cur = slot.getStack();
            prev = cur.copy();

            // Do stuff
            if (fromSlot < 15) {
                // From TE Inventory to Player Inventory
                if (!mergeItemStack(cur, 15, 51, true))
                    return null;
            } else {
                // From Player Inventory to TE Inventory
                if (!mergeItemStack(cur, 0, 15, false))
                    return null;
            }

            if (cur.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (cur.stackSize == prev.stackSize) return null;
            slot.onPickupFromSlot(player, cur);
        }
        return prev;
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
        // Makes sure that you cannot grab the item you are using to open this container
        if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) {
            return null;
        }
        return super.slotClick(slot, button, flag, player);
    }

    private static class LunchBoxSlot extends Slot {

        public LunchBoxSlot(IInventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack itemStack) {
            return itemStack.getItem() != TFCEItems.lunchbox && itemStack.getItem().getItemUseAction(itemStack) == EnumAction.eat;
        }
    }
}
