package fair.tfcengineer.common.GUI.elements;

import cofh.lib.gui.GuiBase;
import com.dunk.tfc.Core.TFC_Core;
import fair.tfcengineer.common.TileEntities.machines.FridgeBaseTE;
import fair.tfcengineer.common.TileEntities.machines.PoweredMachineTE;

import java.util.List;

public class ElementTFCEEnergyFridge extends ElementTFCEEnergyMachine {

    private FridgeBaseTE fridgeTE;

    public ElementTFCEEnergyFridge(GuiBase guiBase, FridgeBaseTE te, int x, int y) {
        super(guiBase, te, x, y);
        this.fridgeTE = te;
    }

    public void addTooltip(List<String> var1) {
        super.addTooltip(var1);
        var1.add(TFC_Core.translate("gui.DecayRate") + " " + (int) (fridgeTE.getDecayRateLastTick() * 100) + "%");
    }

    public void updateTilEntity(FridgeBaseTE te) {
        super.updateTilEntity(te);
        this.fridgeTE = te;
    }
}
