package fair.tfcengineer.common.GUI.elements;

import cofh.lib.gui.GuiBase;
import com.dunk.tfc.Core.TFC_Core;
import com.dunk.tfc.api.TFC_ItemHeat;

import java.util.List;

public class ElementTFCETempGauge extends ElementTFCE {

    protected float currentTemp;
    protected float maxTemp;

    public ElementTFCETempGauge(GuiBase guiBase, float currentTemperature, int x, int y) {
        super(guiBase, x, y, 9, 52);
        this.currentTemp = currentTemperature;
        maxTemp = 2500;
        texW = 9;
        texH = 52;
        setSize(texW, texH);
    }

    public void updateTemp(float temperature) {
        this.currentTemp = temperature;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        gui.mc.getTextureManager().bindTexture(ELEMENTS_TEXTURE);
        float tempPercent = currentTemp / maxTemp;
        int tempPixels = Math.min((int) ((texH - 2) * tempPercent), texH - 2);
        int tempPixelsInv = Math.abs(tempPixels - (texH -2));
        gui.drawTexturedModalRect(posX, posY, 39, 1, texW, texH); // Gauge drawing
        gui.drawTexturedModalRect(posX - 3, posY - 2 + tempPixelsInv, 36, 54, 15, 5);
    }

    public void addTooltip(List<String> var1) {
        var1.add(TFC_Core.translate("gui.ElectricForge.TargetTemp"));
        if (currentTemp <= 0) var1.add(TFC_Core.translate("gui.ElectricForge.Off"));
        else var1.add(TFC_ItemHeat.getHeatColor(currentTemp, 10000000));
    }
}
