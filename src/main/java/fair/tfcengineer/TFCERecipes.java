package fair.tfcengineer;

import com.dunk.tfc.api.TFCBlocks;
import com.dunk.tfc.api.TFCItems;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import fair.tfcengineer.common.Blocks.TFCEBlocks;
import fair.tfcengineer.common.Items.TFCEItems;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class TFCERecipes {

    public static void registerRecipes() {
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TFCEItems.lunchbox), new ItemStack(TFCEItems.insulationCloth, 1), new ItemStack(TFCEItems.insulationCloth, 1), "plateDoubleSteel", "itemKnife"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TFCEItems.insulationCloth, 1), "CCC", "BBB", "LLL", 'C', "materialCloth", 'B', new ItemStack(TFCItems.burlapCloth, 1), 'L', "materialLeather"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TFCEBlocks.fridge, 1), "ICI", "CRC", "ICI", 'C', new ItemStack(TFCEItems.insulationCloth, 1), 'I', "plateDoubleSteel", 'R', new ItemStack(Blocks.snow, 1)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TFCEBlocks.deepFreezer, 1), "SCS", "CFC", "SCS", 'C', new ItemStack(TFCEItems.insulationCloth, 1), 'S', "plateDoubleBlackSteel", 'F', new ItemStack(TFCEBlocks.fridge, 1)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TFCEBlocks.electricForge, 1), "IFI", "FSF", "IFI", 'F', new ItemStack(TFCBlocks.fireBrick, 1), 'I', "plateDoubleSteel", 'S', "toolFlintSteel"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TFCEBlocks.inductionForge, 1), "SFS", "FEF", "SFS", 'F', new ItemStack(TFCBlocks.fireBrick, 1), 'S', "plateDoubleBlackSteel", 'E', new ItemStack(TFCEBlocks.electricForge, 1, OreDictionary.WILDCARD_VALUE)));
    }

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent e) {
        Item item = e.crafting.getItem();
        IInventory craftMatrix = e.craftMatrix;
        if (craftMatrix != null) {

            // Make sure knifes are not consumed in crafting lunchboxes
            if (item == TFCEItems.lunchbox) { // If the result is lunchbox
                final int knifeOreId = OreDictionary.getOreID("itemKnife");
                for (int i = 0; i < craftMatrix.getSizeInventory(); i++) { // Find the knife in the recipe
                    if (craftMatrix.getStackInSlot(i) == null) continue;
                    int[] itemOreIds = OreDictionary.getOreIDs(craftMatrix.getStackInSlot(i));
                    for (int j = 0; j < itemOreIds.length; j++) { // Check one of the oreIds of the item matches the knife oreId
                        if (itemOreIds[j] == knifeOreId) {
                            ItemStack tfcKnife = craftMatrix.getStackInSlot(i).copy();
                            tfcKnife.damageItem(1, e.player); // Add 1 damage to knife
                            if (tfcKnife.getItemDamage() != 0) {
                                craftMatrix.setInventorySlotContents(i, tfcKnife);
                                craftMatrix.getStackInSlot(i).stackSize = 2; // Have to be stack size 2, because 1 will be consumed
                            }
                        }
                    }
                }
            }
        }
    }

}
