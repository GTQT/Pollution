package meowmel.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * Stable player-placeable ritual crystals. Unlike Astral Sorcery collector
 * crystals, these do not require hidden crystal NBT or an AS network tile.
 */
@ParametersAreNonnullByDefault
public class POConstellationCrystal extends VariantBlock<POConstellationCrystal.CrystalType> {

    private static final AxisAlignedBB RITUAL_CRYSTAL_BOX =
            new AxisAlignedBB(0.24D, 0.03D, 0.24D, 0.76D, 1.0D, 0.76D);
    private static final AxisAlignedBB TOWER_CORE_BOX =
            new AxisAlignedBB(0.12D, 0.06D, 0.12D, 0.88D, 0.98D, 0.88D);

    public POConstellationCrystal() {
        super(Material.GLASS);
        setTranslationKey("constellation_crystal");
        setHardness(3.0F);
        setResistance(8.0F);
        setSoundType(SoundType.GLASS);
        setHarvestLevel("pickaxe", 3);
        setLightLevel(0.85F);
        setDefaultState(getState(CrystalType.RITUAL_CRYSTAL));
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
                                    @Nonnull BlockPos pos, @Nonnull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new meowmel.pollution.common.block.tile.TileEntityConstellationCrystal();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (getState(state) != CrystalType.TOWER_CORE) return false;
        if (world.isRemote) return true;
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof meowmel.pollution.common.block.tile.TileEntityConstellationCrystal
                && ((meowmel.pollution.common.block.tile.TileEntityConstellationCrystal) tile)
                .onCoreRightClick(player, hand);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getState(state) == CrystalType.TOWER_CORE ? TOWER_CORE_BOX : RITUAL_CRYSTAL_BOX;
    }

    /**
     * CHC-inspired visual language: small orbiting starlight on every node,
     * and a taller vertical beam from the tower core.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        CrystalType type = getState(state);
        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double radius = type == CrystalType.TOWER_CORE ? 0.45D : 0.32D;
        double orbitX = centerX + Math.cos(angle) * radius;
        double orbitZ = centerZ + Math.sin(angle) * radius;
        world.spawnParticle(EnumParticleTypes.END_ROD, orbitX,
                centerY + random.nextDouble() * 0.9D, orbitZ,
                -Math.cos(angle) * 0.008D, 0.015D, -Math.sin(angle) * 0.008D);

        if (type != CrystalType.TOWER_CORE || random.nextInt(3) != 0) return;
        for (int i = 0; i < 4; i++) {
            double height = 0.25D + i * 0.45D + random.nextDouble() * 0.15D;
            world.spawnParticle(EnumParticleTypes.END_ROD, centerX + (random.nextDouble() - 0.5D) * 0.12D,
                    pos.getY() + height, centerZ + (random.nextDouble() - 0.5D) * 0.12D,
                    0.0D, 0.035D, 0.0D);
        }
    }

    public enum CrystalType implements IStringSerializable {
        RITUAL_CRYSTAL("ritual_crystal"),
        TOWER_CORE("tower_core");

        private final String name;

        CrystalType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
