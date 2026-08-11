package meowmel.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import gregtech.api.items.toolitem.ToolClasses;
import meowmel.pollution.common.block.tile.TileEntityStarstreamObeliskCore;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/** Structural blocks and the physical storage core of the Starstream Nexus. */
@ParametersAreNonnullByDefault
public class POStarstreamObelisk extends VariantBlock<POStarstreamObelisk.ObeliskBlockType> {

    public POStarstreamObelisk() {
        super(Material.ROCK);
        setTranslationKey("starstream_obelisk");
        setHardness(12.0F);
        setResistance(80.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel(ToolClasses.PICKAXE, 4);
        setDefaultState(getState(ObeliskBlockType.STARSTREAM_CASING));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return getState(state) == ObeliskBlockType.OBELISK_CORE;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return getState(state) == ObeliskBlockType.OBELISK_CORE
                ? new TileEntityStarstreamObeliskCore() : null;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return getState(state) == ObeliskBlockType.OBELISK_CORE
                ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        ObeliskBlockType type = getState(state);
        if (type == ObeliskBlockType.OBELISK_CORE) return 15;
        if (type == ObeliskBlockType.CONSTELLATION_ANCHOR) return 11;
        if (type == ObeliskBlockType.STARSTREAM_RUNED_CASING) return 5;
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (getState(state) != ObeliskBlockType.OBELISK_CORE) return false;
        if (world.isRemote) return true;
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityStarstreamObeliskCore
                && ((TileEntityStarstreamObeliskCore) tile).onCoreRightClick(player);
    }

    public enum ObeliskBlockType implements IStringSerializable {
        STARSTREAM_CASING("starstream_casing"),
        STARSTREAM_RUNED_CASING("starstream_runed_casing"),
        CONSTELLATION_ANCHOR("constellation_anchor"),
        OBELISK_CORE("obelisk_core");

        private final String name;

        ObeliskBlockType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }
}
