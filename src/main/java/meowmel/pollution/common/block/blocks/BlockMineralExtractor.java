package meowmel.pollution.common.block.blocks;

import meowmel.pollution.Pollution;
import meowmel.pollution.common.ModGuiHandler;
import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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

    /**
     * 右键打开矿物提取器 GUI（服务端打开容器，客户端渲染界面）。
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMineralExtractor) {
                player.openGui(Pollution.instance, ModGuiHandler.GUI_MINERAL_EXTRACTOR,
                        world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
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