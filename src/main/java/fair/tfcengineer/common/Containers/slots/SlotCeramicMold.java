package fair.tfcengineer.common.Containers.slots;

import com.dunk.tfc.api.TFCItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCeramicMold extends Slot {

    public SlotCeramicMold(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return itemstack.getItem() == TFCItems.ceramicMold && itemstack.getItemDamage() == 1;
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

}
