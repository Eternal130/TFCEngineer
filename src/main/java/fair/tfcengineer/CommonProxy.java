package fair.tfcengineer;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import fair.tfcengineer.common.GUI.GuiHandler;
import fair.tfcengineer.common.Network.MachineInteractPacket;

public class CommonProxy {

    public void registerRendering() {
        // Not on server
    }

    public void registerGUIHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(TFCEngineer.instance, new GuiHandler());
    }

    public void registerPackets() {
        TFCEngineer.network.registerMessage(MachineInteractPacket.Handler.class, MachineInteractPacket.class, 0, Side.SERVER);
    }

    public void registerNetworkChannel() {
        TFCEngineer.network = NetworkRegistry.INSTANCE.newSimpleChannel("TFCEngineerChannel");
    }
}
