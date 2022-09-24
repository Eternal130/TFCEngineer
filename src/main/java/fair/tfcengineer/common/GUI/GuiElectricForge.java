package fair.tfcengineer.common.GUI;

import cofh.lib.gui.GuiBase;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.Containers.ContainerPoweredForge;
import fair.tfcengineer.common.GUI.buttons.ButtonDecrease;
import fair.tfcengineer.common.GUI.buttons.ButtonIncrease;
import fair.tfcengineer.common.GUI.elements.ElementTFCEEnergyMachine;
import fair.tfcengineer.common.GUI.elements.ElementTFCETempGauge;
import fair.tfcengineer.common.Network.MachineInteractPacket;
import fair.tfcengineer.common.TileEntities.machines.ElectricForgeTE;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiElectricForge extends GuiBase {

    protected ResourceLocation resourceLocation;

    protected IInventory playerInv;
    protected ElectricForgeTE te;
    protected int curMeta;

    protected GuiButton butTempUp, butTempDown;
    protected ElementTFCEEnergyMachine energyStored;
    protected ElementTFCETempGauge tempGauge;

    public GuiElectricForge(IInventory playerInv, ElectricForgeTE te) {
        super(new ContainerPoweredForge(playerInv, te));
        this.playerInv = playerInv;
        this.te = te;
        curMeta = te.getBlockMetadata();

        resourceLocation = new ResourceLocation(TFCEngineer.ModID + ":" + "textures/gui/electricforge.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(butTempUp = new ButtonIncrease(0, guiLeft + 42, guiTop + 15));
        buttonList.add(butTempDown = new ButtonDecrease(1, guiLeft + 42, guiTop + 45));
        addElement(energyStored = new ElementTFCEEnergyMachine(this, te, 7, 9));
        addElement(tempGauge = new ElementTFCETempGauge(this, te.getHeatingTemperature(), 28, 12));
    }

    // Client side code, send a packet to notify server about button presses
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == butTempUp) {
            TFCEngineer.network.sendToServer(new MachineInteractPacket(te.xCoord, te.yCoord, te.zCoord, 0));
        } else if (button == butTempDown) {
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
            if (newTE instanceof ElectricForgeTE) {
                updateTileEntity((ElectricForgeTE) newTE);
            } else {
                mc.thePlayer.closeScreen();
            }
            curMeta = te.getBlockMetadata();
        }
        tempGauge.updateTemp(te.getHeatingTemperature());
    }

    public void updateTileEntity(ElectricForgeTE te) {
        this.te = te;
        energyStored.updateTilEntity(te);
        tempGauge.updateTemp(te.getHeatingTemperature());
    }
}
