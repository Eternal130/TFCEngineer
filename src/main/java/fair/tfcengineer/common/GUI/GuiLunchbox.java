package fair.tfcengineer.common.GUI;

import cofh.lib.gui.GuiBase;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.Containers.ContainerLunchbox;
import fair.tfcengineer.common.Items.InventoryItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiLunchbox extends GuiBase {

    protected ResourceLocation resourceLocation;

    protected IInventory playerInv;
    protected InventoryItem invItem;

    public GuiLunchbox(IInventory playerInv, InventoryItem invItem) {
        super(new ContainerLunchbox(playerInv, invItem));
        this.playerInv = playerInv;
        this.invItem = invItem;

        resourceLocation = new ResourceLocation(TFCEngineer.ModID + ":" + "textures/gui/lunchbox.png");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(resourceLocation);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawElements(partialTicks, false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawElements(0f, true);
    }

}
