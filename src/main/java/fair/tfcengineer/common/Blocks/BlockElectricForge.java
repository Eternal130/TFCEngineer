package fair.tfcengineer.common.Blocks;

import com.dunk.tfc.TileEntities.TEForge;
import com.dunk.tfc.api.Interfaces.IHeatSource;
import com.dunk.tfc.api.Interfaces.IHeatSourceTE;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.GUI.GuiHandler;
import fair.tfcengineer.common.TileEntities.MachineBaseTE;
import fair.tfcengineer.common.TileEntities.machines.ElectricForgeTE;
import fair.tfcengineer.common.TileEntities.machines.PoweredForgeBaseTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockElectricForge extends BlockTFCESidedContainer implements IHeatSource {

    public BlockElectricForge() {
        super(Material.rock, 2);
        setBlockName("ElectricForge");
        setHardness(2.0F);
        setResistance(6.0F);
        setStepSound(soundTypeMetal);

        float min = 1f / 16f;
        float max = 15f / 16f;
        setBlockBounds(min, 0f, min, max, max, max);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int state = getState(world.getBlockMetadata(x, y, z));
        return state > 0 ? 10 : 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new ElectricForgeTE(meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float sideX, float sideY, float sideZ) {
        if (!world.isRemote) {
            player.openGui(TFCEngineer.instance, GuiHandler.ElectricForgeGUI, world, x, y, z);
            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        registerSidedIcons(reg, 0, // Off state
                "electricforgetop", "electricforgetop", // bot, top
                "electricforgefront", "electricforgeside", // front, back
                "electricforgeside", "electricforgeside"); // right, left
        registerSidedIcons(reg, 1, // On state
                "electricforgetop", "electricforgetop",
                "electricforgefronton", "electricforgeside",
                "electricforgeside", "electricforgeside");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return TFCEBlocks.poweredForgeRenderId;
    }

    @Override
    public float getHeatSourceRadius() {
        return 5;
    }

    @Override
    public Class getTileEntityType() {
        return ElectricForgeTE.class;
    }
}
