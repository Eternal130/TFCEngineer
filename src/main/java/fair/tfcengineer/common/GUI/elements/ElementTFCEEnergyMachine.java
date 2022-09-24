package fair.tfcengineer.common.GUI.elements;

import cofh.lib.gui.GuiBase;
import com.dunk.tfc.Core.TFC_Core;
import fair.tfcengineer.common.TileEntities.machines.PoweredMachineTE;

import java.util.List;

public class ElementTFCEEnergyMachine extends ElementTFCEEnergy {

    private PoweredMachineTE te;

    public ElementTFCEEnergyMachine(GuiBase guiBase, PoweredMachineTE te, int x, int y) {
        super(guiBase, te.getEnergyStorage(), x, y);
        this.te = te;
    }

    public void addTooltip(List<String> var1) {
        super.addTooltip(var1);
        var1.add(TFC_Core.translate("gui.EnergyUsage") + " " + te.getEnergyUsedLastTick() + " RF/t");
    }

    public void updateTilEntity(PoweredMachineTE te) {
        this.te = te;
        updateEnergyStorage(te.getEnergyStorage());
    }
}
