package fair.tfcengineer.common.Items;

import com.dunk.tfc.Core.Player.FoodStatsTFC;
import com.dunk.tfc.Core.TFCTabs;
import com.dunk.tfc.Core.TFC_Core;
import com.dunk.tfc.api.Enums.EnumSize;
import com.dunk.tfc.api.Enums.EnumWeight;
import fair.tfcengineer.TFCEConfigs;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.GUI.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemLunchbox extends TFCEMatItem {

    public ItemLunchbox() {
        setMaxStackSize(1);
        setUnlocalizedName("Lunchbox");
        setTextureName(TFCEngineer.ModID + ":" + "lunchbox");
        stackable = false;
        weight = EnumWeight.MEDIUM;
        size = EnumSize.SMALL;
        this.setCreativeTab(TFCTabs.TFC_TOOLS);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack is) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack is) {
        return EnumAction.eat;
    }

    public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            if (!world.isRemote) {
                player.openGui(TFCEngineer.instance, GuiHandler.LunchboxGUI, world, 0, 0, 0);
            }
            return is;
        }
        FoodStatsTFC foodStats = TFC_Core.getPlayerFoodStats(player);
        if (foodStats.needFood()) { // Check if there is any food items in box
            InventoryItem storage = new InventoryItem(is);
            for (int i = 0; i < storage.getSizeInventory(); i++) {
                ItemStack cs = storage.getStackInSlot(i);
                if (cs != null) {
                    if (cs.getItem().getItemUseAction(cs) == EnumAction.eat) {
                        player.setItemInUse(is, getMaxItemUseDuration(is));
                        break;
                    }
                }
            }
        }
        return is;
    }

    @Override
    public ItemStack onEaten(ItemStack is, World world, EntityPlayer player) {
        InventoryItem storage = new InventoryItem(is);
        for (int i = 0; i < storage.getSizeInventory(); i++) {
            ItemStack cs = storage.getStackInSlot(i);
            if (cs != null) {
                if (cs.getItem().getItemUseAction(cs) == EnumAction.eat) {
                    cs.getItem().onEaten(cs, world, player);
                    break;
                }
            }
        }
        storage.markDirty();
        return is;
    }

    @Override
    public boolean onUpdate(ItemStack is, World world, int x, int y, int z) {
        InventoryItem storage = new InventoryItem(is);
        storage.tickContent(world, x, y, z, TFCEConfigs.lunchboxDecayRate);
        return true;
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List list, boolean flag) {
        if (is.hasTagCompound()) {
            int totalItems = is.getTagCompound().getInteger("totalItems");
            list.add(TFC_Core.translate("gui.Lunchbox.Part1") + " " + totalItems + " " + TFC_Core.translate("gui.Lunchbox.Part2"));
        }
        super.addInformation(is, player, list, flag);
    }

    public void addExtraInformation(ItemStack is, EntityPlayer player, List<String> arraylist) {
        if (TFC_Core.showShiftInformation()) {
            arraylist.add(TFC_Core.translate("gui.Help"));
            arraylist.add(TFC_Core.translate("gui.Lunchbox.Help1"));
            arraylist.add(TFC_Core.translate("gui.Lunchbox.Help2"));
        } else {
            arraylist.add(TFC_Core.translate("gui.ShowHelp"));
        }
    }
}
