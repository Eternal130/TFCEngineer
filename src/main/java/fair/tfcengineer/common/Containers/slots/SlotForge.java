package fair.tfcengineer.common.Containers.slots;

import com.dunk.tfc.Blocks.Devices.BlockAnvil;
import com.dunk.tfc.Food.ItemFoodTFC;
import com.dunk.tfc.Items.ItemBlocks.ItemAnvil;
import com.dunk.tfc.Items.ItemMetalChunk;
import com.dunk.tfc.Items.ItemOre;
import com.dunk.tfc.Items.Pottery.ItemPotterySheetMold;
import com.dunk.tfc.Items.Pottery.ItemPotterySmallVessel;
import com.dunk.tfc.api.HeatRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotForge extends Slot {

    public SlotForge(IInventory inventory, int slotIndex, int xDisplayPos, int yDisplayPos) {
        super(inventory, slotIndex, xDisplayPos, yDisplayPos);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        HeatRegistry manager = HeatRegistry.getInstance();
        return !(manager.findMatchingIndex(itemstack) == null
                || itemstack.getItem() instanceof ItemOre
                || itemstack.getItem() instanceof ItemFoodTFC
                || itemstack.getItem() instanceof ItemMetalChunk
                || itemstack.getItem() instanceof ItemPotterySheetMold
                || itemstack.getItem() instanceof ItemPotterySmallVessel
                || itemstack.getItem() instanceof ItemAnvil);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
