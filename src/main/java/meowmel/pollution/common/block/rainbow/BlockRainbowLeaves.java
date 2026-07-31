package meowmel.pollution.common.block.rainbow;

import meowmel.pollution.Pollution;
import meowmel.pollution.common.CommonProxy;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Rainbow leaves rendered with a position-dependent tint.
 *
 * <p>The original ChromatiCraft leaf performs several biome-specific actions.
 * This clean-room implementation deliberately limits its environmental action
 * to Thaumcraft aura maintenance: slowly removing flux and restoring vis.</p>
 */
public class BlockRainbowLeaves extends BlockLeaves {

    public static final PropertyBool SMALL = PropertyBool.create("small");

    public BlockRainbowLeaves() {
        setRegistryName(Pollution.MODID, "rainbow_leaves");
        setTranslationKey(Pollution.MODID + ".rainbow_leaves");
        setCreativeTab(CommonProxy.Pollution_TAB);
        setHardness(0.2F);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState()
                .withProperty(CHECK_DECAY, false)
                .withProperty(DECAYABLE, false)
                .withProperty(SMALL, false));
    }

    public IBlockState generatedState(boolean small) {
        return getDefaultState()
                .withProperty(CHECK_DECAY, false)
                .withProperty(DECAYABLE, true)
                .withProperty(SMALL, small);
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.randomTick(world, pos, state, random);
        if (world.isRemote || world.getBlockState(pos).getBlock() != this || random.nextInt(32) != 0) {
            return;
        }

        // Spread the work across nearby aura chunks instead of repeatedly
        // modifying only the chunk containing the trunk.
        BlockPos target = pos.add(random.nextInt(33) - 16, random.nextInt(9) - 4,
                random.nextInt(33) - 16);
        if (!world.isBlockLoaded(target)) {
            return;
        }

        float flux = AuraHelper.getFlux(world, target);
        if (flux > 0.0F) {
            AuraHelper.drainFlux(world, target, Math.min(0.5F, flux), false);
        }

        float base = AuraHelper.getAuraBase(world, target);
        float vis = AuraHelper.getVis(world, target);
        float gentleCap = base * 1.1F;
        if (base > 0.0F && vis < gentleCap) {
            AuraHelper.addVis(world, target, Math.min(0.25F, gentleCap - vis));
        }
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(5) != 0) {
            return;
        }
        int color = rainbowColor(pos);
        double red = ((color >> 16) & 255) / 255.0D;
        double green = ((color >> 8) & 255) / 255.0D;
        double blue = (color & 255) / 255.0D;
        world.spawnParticle(EnumParticleTypes.REDSTONE,
                pos.getX() + random.nextDouble(),
                pos.getY() + random.nextDouble(),
                pos.getZ() + random.nextDouble(),
                red, green, blue);
    }

    public static int rainbowColor(@Nullable BlockPos pos) {
        if (pos == null) {
            long phase = System.currentTimeMillis() / 45L;
            return MathHelper.hsvToRGB((phase % 360L) / 360.0F, 0.72F, 1.0F);
        }
        double x = pos.getX();
        double y = pos.getY() * 3.0D;
        double z = pos.getZ() + pos.getX();
        int band = Math.floorMod((int) Math.floor(Math.sqrt(x * x + y * y + z * z)), 32);
        return MathHelper.hsvToRGB(band / 32.0F, 0.72F, 1.0F);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        Random random = world instanceof World ? ((World) world).rand : RANDOM;
        int dyeCount = 1 + random.nextInt(3) + (fortune > 0 && random.nextInt(3) < fortune ? 1 : 0);
        for (int i = 0; i < dyeCount; i++) {
            drops.add(new ItemStack(Items.DYE, 1, random.nextInt(16)));
        }

        boolean small = state.getValue(SMALL);
        int dropPenalty = small ? 2 : 1;
        int fortuneDivisor = Math.max(1, fortune + 1);
        addRareDrop(drops, random, new ItemStack(RainbowTreeRegistration.RAINBOW_SAPLING),
                Math.max(1, 8000 * dropPenalty / fortuneDivisor));
        addRareDrop(drops, random, new ItemStack(Items.APPLE),
                Math.max(1, 1000 * dropPenalty / fortuneDivisor));
        addRareDrop(drops, random, new ItemStack(Items.GOLDEN_APPLE),
                Math.max(1, 4000 * dropPenalty / fortuneDivisor));
        addRareDrop(drops, random, new ItemStack(Items.GOLDEN_APPLE, 1, 1),
                Math.max(1, 40000 * dropPenalty / fortuneDivisor));
    }

    private static void addRareDrop(NonNullList<ItemStack> drops, Random random,
                                    ItemStack stack, int chance) {
        if (random.nextInt(chance) == 0) {
            drops.add(stack);
        }
    }

    @Override
    @Nonnull
    public BlockPlanks.EnumType getWoodType(int meta) {
        return BlockPlanks.EnumType.OAK;
    }

    @Override
    @Nonnull
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world,
                                     BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world,
                                        BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(DECAYABLE, false)
                .withProperty(CHECK_DECAY, false).withProperty(SMALL, false), 2);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE, SMALL);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(DECAYABLE)) {
            meta |= 1;
        }
        if (state.getValue(CHECK_DECAY)) {
            meta |= 2;
        }
        if (state.getValue(SMALL)) {
            meta |= 4;
        }
        return meta;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(DECAYABLE, (meta & 1) != 0)
                .withProperty(CHECK_DECAY, (meta & 2) != 0)
                .withProperty(SMALL, (meta & 4) != 0);
    }
}
