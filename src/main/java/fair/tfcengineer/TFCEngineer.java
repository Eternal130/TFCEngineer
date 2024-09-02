package fair.tfcengineer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import fair.tfcengineer.common.Blocks.TFCEBlocks;
import fair.tfcengineer.common.Items.TFCEItems;

@Mod(modid = TFCEngineer.ModID, name = TFCEngineer.ModName, version = TFCEngineer.Version)
public class TFCEngineer {

    public static final String ModID = "tfcengineer";
    public static final String ModName = "TFC Engineer";
    public static final String Version = "1.1.9";
    public static final String SERVER_PROXY_CLASS = "fair.tfcengineer.CommonProxy";
    public static final String CLIENT_PROXY_CLASS = "fair.tfcengineer.ClientProxy";

    @Mod.Instance(ModID)
    public static TFCEngineer instance;

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Block, item, tile entities, entities registering
        // Assigning ore dic names
        // Config logic
        System.out.println("TFCEngineer: Pre init");

        TFCEConfigs.preInit(event);

        TFCEItems.registerItems();

        TFCEBlocks.registerBlocks();
        TFCEBlocks.registerTileEntities();

        proxy.registerGUIHandler();

        proxy.registerNetworkChannel();
        proxy.registerPackets();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // World generators, recipes, event handlers registering
        // IMC messages
        System.out.println("TFCEngineer: Init");

        TFCERecipes.registerRecipes();
//        MinecraftForge.EVENT_BUS.register(TFCERecipes.class);
        FMLCommonHandler.instance().bus().register(new TFCERecipes());

        proxy.registerRendering();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Mod compatibility
        System.out.println("TFCEngineer: Post init");

    }
}
