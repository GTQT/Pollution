package meowmel.pollution.common.block.blocks;

import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static meowmel.pollution.common.CommonProxy.Pollution_TAB;

/**
 * Display-only shell for the mineral extractor.
 *
 * <p>The block itself is invisible; its placed appearance is supplied by
 * {@code TesrMineralExtractor}. It deliberately contains no mining, inventory,
 * energy, GUI, or world-scanning behavior.</p>
 */
public class BlockMineralExtractor extends Block {

    public BlockMineralExtractor() {
        super(Material.IRON);
        setRegistryName("pollution", "mineral_extractor");
        setTranslationKey("pollution.mineral_extractor");
        setCreativeTab(Pollution_TAB);
        setHardness(4.0F);
        setResistance(12.0F);
        setLightLevel(0.625F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityMineralExtractor();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
