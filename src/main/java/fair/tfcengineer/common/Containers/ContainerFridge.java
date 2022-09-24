package fair.tfcengineer.common.Containers;

import com.dunk.tfc.Containers.ContainerTFC;
import fair.tfcengineer.common.TileEntities.machines.FridgeBaseTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFridge extends ContainerTFC {

    protected FridgeBaseTE te;

    public ContainerFridge(IInventory playerInv, FridgeBaseTE te) {
        this.te = te;

        // Tile Entity, Slot 0-20, Slot IDs 0-27
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 7; x++) {
                addSlotToContainer(new Slot(te, y * 7 + x, 44 + x * 18, 8 + y * 18));
            }
        }

        // Player Inventory, Slot 9-35, Slot IDs 28-54
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player Inventory (hotbar), Slot 0-8, Slot IDs 55-64
        for (int x = 0; x < 9; ++x) {
            addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return te.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlotTFC(EntityPlayer player, int fromSlot) {
        ItemStack prev = null;
        Slot slot = (Slot) inventorySlots.get(fromSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack cur = slot.getStack();
            prev = cur.copy();

            // Do stuff
            if (fromSlot < 28) {
                // From TE Inventory to Player Inventory
                if (!mergeItemStack(cur, 28, 64, true))
                    return null;
            } else {
                // From Player Inventory to TE Inventory
                if (!mergeItemStack(cur, 0, 28, false))
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
}
