package fair.tfcengineer.common.Blocks;

import com.dunk.tfc.Core.TFCTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.GUI.GuiHandler;
import fair.tfcengineer.common.TileEntities.machines.DeepFreezerTE;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDeepFreezer extends BlockTFCESidedContainer {

    public BlockDeepFreezer() {
        super(Material.rock, 2);
        setBlockName("DeepFreezer");
        setHardness(2.0F);
        setResistance(6.0F);
        setStepSound(soundTypeMetal);
        this.setCreativeTab(TFCTabs.TFC_DEVICES);

        float min = 1f / 16f;
        float max = 15f / 16f;
        setBlockBounds(min, 0f, min, max, max, max);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new DeepFreezerTE(meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float sideX, float sideY, float sideZ) {
        if (!world.isRemote) {
            player.openGui(TFCEngineer.instance, GuiHandler.FridgeGUI, world, x, y, z);
            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        registerSidedIcons(reg, 0, // Off state
                "freezertop", "freezertop", // bot, top
                "freezerfront", "freezerside", // front, back
                "freezerside", "freezerside"); // right, left
        registerSidedIcons(reg, 1, // On state
                "freezertop", "freezertop",
                "freezerfronton", "freezerside",
                "freezerside", "freezerside");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (side == 0) return super.shouldSideBeRendered(blockAccess, x, y, z, side); // Bottom should not be rendered
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return TFCEBlocks.fridgeRenderId;
    }
}
