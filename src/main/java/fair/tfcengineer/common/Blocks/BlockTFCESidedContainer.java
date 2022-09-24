package fair.tfcengineer.common.Blocks;

import com.dunk.tfc.api.Enums.EnumItemReach;
import com.dunk.tfc.api.Enums.EnumSize;
import com.dunk.tfc.api.Enums.EnumWeight;
import com.dunk.tfc.api.Interfaces.ISize;
import fair.tfcengineer.TFCEngineer;
import fair.tfcengineer.common.TileEntities.interfaces.SidedBlockTE;
import fair.tfcengineer.common.TileEntities.machines.ElectricForgeTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockTFCESidedContainer extends BlockContainer implements ISize {

    private IIcon[][] icons;
    private boolean hasSetIcons;

    public BlockTFCESidedContainer(Material mat, int maxStates) {
        super(mat);
        icons = new IIcon[maxStates][6];
    }

    @Override
    public EnumSize getSize(ItemStack itemStack) {
        return EnumSize.TINY;
    }

    @Override
    public EnumWeight getWeight(ItemStack itemStack) {
        return EnumWeight.LIGHT;
    }

    @Override
    public EnumItemReach getReach(ItemStack itemStack) {
        return EnumItemReach.SHORT;
    }

    @Override
    public boolean canStack() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new ElectricForgeTE(meta);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof IInventory) {
            dropInventoryItems(world, x, y, z, (IInventory) te);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    protected void dropInventoryItems(World world, int x, int y, int z, IInventory te) {
        for (int i = 0; i < te.getSizeInventory(); i++) {
            if (te.getStackInSlot(i) != null) {
                EntityItem item = new EntityItem(world, x + 0.5d, y + 0.5d, z + 0.5d, te.getStackInSlot(i));
                item.motionX = 0;
                item.motionY = 0;
                item.motionZ = 0;
                world.spawnEntityInWorld(item);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
        // This does not consider player pitch, so front cannot be bottom or top
        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int meta;
        switch (l) {
            case 0:
                meta = 2;
                break;
            case 1:
                meta = 5;
                break;
            case 2:
                meta = 3;
                break;
            case 3:
                meta = 4;
                break;
            default:
                meta = 2;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof SidedBlockTE) {
            ((SidedBlockTE) te).setFrontSide(meta);
        }
    }

    protected void registerSidedIcons(IIconRegister reg, int state, String texBot, String texTop, String texFront, String texBack, String texRight, String texLeft) {
        hasSetIcons = true;
        icons[state][0] = reg.registerIcon(TFCEngineer.ModID + ":" + texBot);
        icons[state][1] = reg.registerIcon(TFCEngineer.ModID + ":" + texTop);
        icons[state][2] = reg.registerIcon(TFCEngineer.ModID + ":" + texFront);
        icons[state][3] = reg.registerIcon(TFCEngineer.ModID + ":" + texBack);
        icons[state][4] = reg.registerIcon(TFCEngineer.ModID + ":" + texRight);
        icons[state][5] = reg.registerIcon(TFCEngineer.ModID + ":" + texLeft);
    }

    /**
     * Override registerBlockIcons to define the different sides.
     * Use registerSidedIcons in the method
     * Example = registerSidedIcons(reg, 0, "bottom", "top", "front", "back", "right", "left")
     * Sides: 0 = bottom, 1 = top, 2 = front, 3 = back, 4 = right, 5 = left
     */

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        hasSetIcons = false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return hasSetIcons;
    }

    public int getState(int meta) {
        return meta / 6;
    }

    public int getFrontSide(int meta) {
        return meta % 6;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (!hasSetIcons) return super.getIcon(side, meta);
        int state = getState(meta);
        if (side < 2) return icons[state][side]; // Top and bottom
        int front = getFrontSide(meta);
        if (front == side) return icons[state][2]; // Front always easy to get
        if (front == 2) { // North front
            if (side == 3) return icons[state][3]; // Back
            if (side == 4) return icons[state][4]; // Right
            if (side == 5) return icons[state][5]; // Left
        } else if (front == 3) { // South front
            if (side == 2) return icons[state][3]; // Back
            if (side == 5) return icons[state][4]; // Right
            if (side == 4) return icons[state][5]; // Left
        } else if (front == 4) { // West front
            if (side == 5) return icons[state][3]; // Back
            if (side == 3) return icons[state][4]; // Right
            if (side == 2) return icons[state][5]; // Left
        } else if (front == 5) { // East front
            if (side == 4) return icons[state][3]; // Back
            if (side == 2) return icons[state][4]; // Right
            if (side == 3) return icons[state][5]; // Left
        }
        return icons[state][side];
    }

    @Override
    public int getRenderType() {
        return TFCEBlocks.sidedBlockRenderId;
    }



}
