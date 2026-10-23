package meowmel.pollution.common.block.blocks;

import meowmel.pollution.common.block.tile.TileEntityBloodyStarlightContainer;
import meowmel.pollution.common.entity.dragon.DragonContainmentData;
import meowmel.pollution.common.entity.dragon.DragonContainmentService;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;

import static meowmel.pollution.common.CommonProxy.Pollution_TAB;

public class BlockBloodyStarlightContainer extends Block {
    private static final AxisAlignedBB BOX = new AxisAlignedBB(0.08, 0, 0.08, 0.92, 0.95, 0.92);

    public BlockBloodyStarlightContainer() {
        super(Material.IRON);
        setRegistryName("pollution", "bloody_starlight_container");
        setTranslationKey("pollution.bloody_starlight_container");
        setCreativeTab(Pollution_TAB);
        setHardness(2.0F);
        setResistance(2000.0F);
        setLightOpacity(0);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new TileEntityBloodyStarlightContainer(); }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.INVISIBLE; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) { return BOX; }
    @Override public EnumPushReaction getPushReaction(IBlockState state) { return EnumPushReaction.BLOCK; }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityBloodyStarlightContainer
                && ((TileEntityBloodyStarlightContainer) tile).isOccupied() ? 10 : 5;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float x, float y, float z) {
        if (!player.getHeldItem(hand).isEmpty()) return false;
        if (world.isRemote) return true;
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityBloodyStarlightContainer)) return true;
        TileEntityBloodyStarlightContainer container = (TileEntityBloodyStarlightContainer) tile;
        if (player.isSneaking() && container.isOccupied()) {
            if (!DragonContainmentService.canEdit(player, pos, ItemStack.EMPTY)) {
                DragonContainmentService.message(player, "permission");
            } else if (DragonContainmentService.release(player, container.getPayload(), pos.up())) {
                container.takePayload();
            }
        } else if (!container.isOccupied()) {
            DragonContainmentService.message(player, "empty");
        } else {
            NBTTagCompound display = container.getPayload().getCompoundTag("Display");
            player.sendStatusMessage(new TextComponentTranslation("pollution.containment.details",
                    display.getString("Name").isEmpty() ? new TextComponentTranslation("entity.pollution.ender_dragon.name")
                            : display.getString("Name"),
                    new TextComponentTranslation("entity.pollution.ender_dragon.stage."
                            + display.getString("Stage").toLowerCase(Locale.ROOT)),
                    display.getFloat("Health"), display.getFloat("MaxHealth"), DragonContainmentData.progress(display)), true);
        }
        return true;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        // Keep the TE until the harvest callback has collected the payload.
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity tile, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, tile, tool);
        if (tile instanceof TileEntityBloodyStarlightContainer) ((TileEntityBloodyStarlightContainer) tile).takePayload();
        world.setBlockToAir(pos);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) { return false; }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(this);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBloodyStarlightContainer) {
            DragonContainmentData.set(stack, ((TileEntityBloodyStarlightContainer) tile).getPayload());
        }
        drops.add(stack);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
        // Also covers world.destroyBlock(pos, true), which drops before calling breakBlock.
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && !world.restoringBlockSnapshots && tile instanceof TileEntityBloodyStarlightContainer) {
            ((TileEntityBloodyStarlightContainer) tile).takePayload();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        // Creative breaking and forced replacement bypass normal harvest. Save occupied contents once.
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && !world.restoringBlockSnapshots && tile instanceof TileEntityBloodyStarlightContainer
                && ((TileEntityBloodyStarlightContainer) tile).isOccupied()) {
            ItemStack stack = new ItemStack(this);
            DragonContainmentData.set(stack, ((TileEntityBloodyStarlightContainer) tile).takePayload());
            spawnAsEntity(world, pos, stack);
        }
        super.breakBlock(world, pos, state);
    }

    @Override public boolean canDropFromExplosion(Explosion explosion) { return false; }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBloodyStarlightContainer
                && ((TileEntityBloodyStarlightContainer) tile).isOccupied()) return;
        super.onBlockExploded(world, pos, explosion);
    }
}
