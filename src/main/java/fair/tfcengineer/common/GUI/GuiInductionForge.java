package fair.tfcengineer.common.GUI;

import cofh.lib.gui.GuiBase;
import com.dunk.tfc.Core.TFC_Core;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.Containers.ContainerPoweredForge;
import fair.tfcengineer.common.GUI.elements.ElementTFCEEnergyMachine;
import fair.tfcengineer.common.GUI.elements.ElementTooltip;
import fair.tfcengineer.common.Network.MachineInteractPacket;
import fair.tfcengineer.common.TileEntities.machines.InductionForgeTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiInductionForge extends GuiBase {

    protected ResourceLocation resourceLocation;

    protected IInventory playerInv;
    protected InductionForgeTE te;
    protected int curMeta;

    protected ModeButton modeMelt, modeWork;
    protected ElementTFCEEnergyMachine energyStored;

    public GuiInductionForge(IInventory playerInv, InductionForgeTE te) {
        super(new ContainerPoweredForge(playerInv, te));
        this.playerInv = playerInv;
        this.te = te;
        curMeta = te.getBlockMetadata();

        resourceLocation = new ResourceLocation(TFCEngineer.ModID + ":" + "textures/gui/inductionforge.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(modeWork = new ModeButton(0, guiLeft + 35, guiTop + 20, 0, resourceLocation));
        buttonList.add(modeMelt = new ModeButton(1, guiLeft + 35, guiTop + 36, 1, resourceLocation));
        addElement(energyStored = new ElementTFCEEnergyMachine(this, te, 7, 9));
        addElement(new ElementTooltip(this, 35, 20, 16, 16, TFC_Core.translate("gui.Induction.Mode1")));
        addElement(new ElementTooltip(this, 35, 36, 16, 16, TFC_Core.translate("gui.Induction.Mode2")));
    }

    // Client side code, send a packet to notify server about button presses
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == modeWork) {
            TFCEngineer.network.sendToServer(new MachineInteractPacket(te.xCoord, te.yCoord, te.zCoord, 0));
        } else if (button == modeMelt) {
            TFCEngineer.network.sendToServer(new MachineInteractPacket(te.xCoord, te.yCoord, te.zCoord, 1));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(resourceLocation);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawElements(partialTicks, false);
        drawTexturedModalRect(guiLeft + 84, guiTop + 66, 176, te.isActive() ? 8 : 0, 8, 8); // Active indicator
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawElements(0f, true);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        // For some reason, when meta data changes, the tile entity of this is not valid anymore, so find a new.
        if (te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord) != curMeta) {
            TileEntity newTE = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord);
            if (newTE instanceof InductionForgeTE) {
                updateTileEntity((InductionForgeTE) newTE);
            } else {
                mc.thePlayer.closeScreen();
            }
            curMeta = te.getBlockMetadata();
        }
        modeWork.updateCurrentMode(te.getMode());
        modeMelt.updateCurrentMode(te.getMode());
    }

    public void updateTileEntity(InductionForgeTE te) {
        this.te = te;
        energyStored.updateTilEntity(te);
    }

    private static class ModeButton extends GuiButton {

        private int mode;
        private int currentMode;
        private ResourceLocation texture;

        public ModeButton(int id, int xPos, int yPos, int mode, ResourceLocation texture) {
            super(id, xPos, yPos, "");
            this.mode = mode;
            this.texture = texture;
            width = 16;
            height = 16;
        }

        public void updateCurrentMode(int currentMode) {
            this.currentMode = currentMode;
        }

        @Override
        public void drawButton(Minecraft mc, int x, int y) {
            if (visible) {
                mc.getTextureManager().bindTexture(texture);
                GL11.glColor4f(1f, 1f, 1f, 1f);
//                boolean mouseOver = x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + height;
//                int state = getHoverState(mouseOver);
                mouseDragged(mc, x, y);
                drawTexturedModalRect(xPosition, yPosition, 176 + (currentMode == mode ? 0 : 16), 16 + mode * 16, 16, 16);
            }
        }
    }

}
