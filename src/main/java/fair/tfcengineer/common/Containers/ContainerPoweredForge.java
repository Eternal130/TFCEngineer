package fair.tfcengineer.common.Containers;

import com.dunk.tfc.Containers.ContainerTFC;
import fair.tfcengineer.common.Containers.slots.SlotCeramicMold;
import fair.tfcengineer.common.Containers.slots.SlotForge;
import fair.tfcengineer.common.TileEntities.machines.PoweredForgeBaseTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPoweredForge extends ContainerTFC {

    protected PoweredForgeBaseTE te;

    public ContainerPoweredForge(IInventory playerInv, PoweredForgeBaseTE te) {
        this.te = te;

        // Tile Entity, Slot 0-8, Slot IDs 0-8
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new SlotForge(te, y * 3 + x, 62 + x * 18, 10 + y * 18));
            }
        }

        // Tile Entity, Slot 9-11
        for (int i = 0; i < 3; i++) {
            addSlotToContainer(new SlotCeramicMold(te, i + 9, 134, 10 + i * 18));
        }

        // Player Inventory, Slot 9-35, Slot IDs 12-38
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player Inventory (hotbar), Slot 0-8, Slot IDs 39-47
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
            if (fromSlot < 12) {
                // From TE Inventory to Player Inventory
                if (!mergeItemStack(cur, 12, 48, true))
                    return null;
            } else {
                // From Player Inventory to TE Inventory
                if (!mergeItemStack(cur, 0, 12, false))
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
