package keqing.pollution.common.metatileentity.multiblock.generator;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.MultiblockFuelRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.logic.OverclockingLogic;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.block.GTQTMetaBlocks;
import keqing.gtqtcore.common.block.blocks.BlockPCBFactoryCasing;
import keqing.pollution.POConfig;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.*;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;

public class MetaTileEntityMultiDanDeLifeOn extends FuelMultiblockController {
    @Override
    public boolean usesMui2() {
        return false;
    }
    private int XPos;
    private int YPos;
    private int ZPos;
    protected int[][] CellAge = new int[31][31];
    protected boolean[][] IsCellAlive = new boolean[31][31];
    private Integer CoilLevel;
    public long EnergyBuffer;
    private int modeIndex = 0;


    public MetaTileEntityMultiDanDeLifeOn(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.DAN_DE_LIFE_ON, 10);
        this.recipeMapWorkable = new MultiDanDeLifeOnWorkable(this);

    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMultiDanDeLifeOn(this.metaTileEntityId);
    }
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDA", " QQQQQQQQQQQQQQQQQQQQQQQQQQQI     IQQQQQQQQQQQQQQQQQQQQQQQQQQQ ", " Q                          Q     Q                          Q ", " M                          Q     Q                          M ", " N                          Q     Q                          N ", "                            Q     Q                            ", "                            QQ   QQ                            ", "                             QQQQQ                             " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTROCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVTROCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWVTROCB", "ADPPPPDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDPPPPDA", " Q    QQQQQQQQQQQQQQQQQQQQQQI     IQQQQQQQQQQQQQQQQQQQQQQ    Q ", "      M                     Q     Q                     M      ", "      N                     Q     Q                     N      ", "                            Q     Q                            ", "                            Q     Q                            ", "                            QQ   QQ                            ", "                             QQQQQ                             " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXWVTROCB", "ADPPPPDBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOZZZZYXWVTROCB", "ADPPPPDB     GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG     BDPPPPDA", " Q    Q      F                                   F      Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYOZZZZYXWVTROCB", "ADPPPPDB     G                                   G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCYOZZZZYXWVTROCB", "ADPPPPDB     G BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " I    I                                                 I    I ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q ", " QQ  QQ                                                 QQ  QQ ", "  QQQQ                                                   QQQQ  " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "GDPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDG", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", " Q    Q                                                 Q    Q " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "GDPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDG", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", " Q    Q                                                 Q    Q " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "GDPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDG", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", " Q    Q                                                 Q    Q " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " I    I                                                 I    I ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q ", " Q    Q                                                 Q    Q ", " QQ  QQ                                                 QQ  QQ ", "  QQQQ                                                   QQQQ  " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHCYOZZZZYXWVTROCB", "ADPPPPDB     G BPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGCYOZZZZYXWVTROCB", "ADPPPPDB     G BZPZPZPZPZPZPZPZPZPZPZPZPZPZPZPZB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCYOZZZZYXWVTROCB", "ADPPPPDB     G BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYOZZZZYXWVTROCB", "ADPPPPDB     G                                   G     BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOZZZZYXWVTROCB", "ADPPPPDB     GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG     BDPPPPDA", " Q    Q      F                                   F      Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYXWVTROCB", "ADPPPPDB                                               BDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXWVTROCB", "ADPPPPDBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBDPPPPDA", " Q    Q                                                 Q    Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWVTROCB", "ADPPPPDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDPPPPDA", " Q    QQQQQQQQQQQQQQQQQQQQQQI     IQQQQQQQQQQQQQQQQQQQQQQ    Q ", "      M                     Q     Q                     M      ", "      N                     Q     Q                     N      ", "                            Q     Q                            ", "                            Q     Q                            ", "                            QQ   QQ                            ", "                             QQQQQ                             " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVTROCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTROCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOCB", "ADPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPDA", " Q                                                           Q ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                            Q     Q                            ", "                            Q     Q                            " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDA", " QQQQQQQQQQQQQQQQQQQQQQQQQQQI     IQQQQQQQQQQQQQQQQQQQQQQQQQQQ ", " Q                          Q     Q                          Q ", " M                          Q     Q                          M ", " N                          Q     Q                          N ", "                            Q     Q                            ", "                            QQ   QQ                            ", "                             QQQQQ                             " )
                .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               ", "                                                               " )


                .where('A', states(getCasingStateLivingRock()))
                .where('B', states(getCasingFusionGlass()))
                .where('C', CP_COIL_CASING.get())
                .where('D', states(getCasingStateMossLivingRock()))
                .where('E', this.selfPredicate())
                .where('F', states(getCasingManaPylon()))
                .where('I', states(getCasingLivingRockSquare()))
                .where('M', states(getCasingLivingRockSlab()))
                .where('N', states(getCasingNaturalPylon()))
                .where('O', states(getCasingFusionCasing3()))
                .where('P', states(getCasingEnchantedSoil()))
                .where('Q', states(getCasingLivingRockWall()))
                .where('R', states(getCasingBioPcb()))
                .where('T', states(getCasingManaPlate5()))
                .where('V', states(getCasingBotBlock2()))
                .where('W', states(getCasingDragonStone()))
                .where('X', states(getCasingDreamWoodGlimmering()))
                .where('Y', states(getCasingLeaves()))
                .where('Z', states(getCasingAltGrass()))
                .where('G', states(getCasingManaPlate4())
                        .or(abilities(MultiblockAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1).setPreviewCount(0))
                        .or(abilities(MultiblockAbility.OUTPUT_LASER).setMaxGlobalLimited(1).setPreviewCount(1))
                        .or(autoAbilities(false,true,true,false,false,true,false)))
                .where('H', states(getCasingManaPlate3()))
                .build();
    }

    public IBlockState getCasingStateLivingRock() {
        return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.BRICK);
    }

    public IBlockState getCasingStateMossLivingRock() {
        return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.MOSSY_BRICK);
    }

    private static IBlockState getCasingLivingRockWall() {
        return ModFluffBlocks.livingrockWall.getDefaultState();
    }

    private static IBlockState getCasingLivingRockSquare() {
        return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.CHISELED_BRICK);
    }

    private static IBlockState getCasingLivingRockSlab() {
        return ModFluffBlocks.livingrockSlab.getDefaultState();
    }

    private static IBlockState getCasingNaturalPylon() {
        return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.NATURA);
    }

    private static IBlockState getCasingFusionCasing3() {
        return MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3);
    }

    private static IBlockState getCasingEnchantedSoil() {
        return ModBlocks.enchantedSoil.getDefaultState();
    }

    private static IBlockState getCasingBioPcb() {
        return GTQTMetaBlocks.blockPCBFactoryCasing.getState(BlockPCBFactoryCasing.PCBFactoryCasingType.BIOLOGICAL_STERILE_MACHINE_CASING);
    }

    private static IBlockState getCasingManaPlate5() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_5);
    }

    private static IBlockState getCasingBotBlock2() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_2_CASING);
    }

    private static IBlockState getCasingDreamWoodGlimmering() {
        return ModBlocks.dreamwood.getDefaultState().withProperty(BotaniaStateProps.LIVINGWOOD_VARIANT, LivingWoodVariant.GLIMMERING);
    }

    private static IBlockState getCasingLeaves() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3);
    }

    private static IBlockState getCasingFusionGlass() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
    }

    private static IBlockState getCasingAltGrass() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3);
    }

    private static IBlockState getCasingManaPlate4() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_4);
    }

    private static IBlockState getCasingManaPlate3() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3);
    }

    private static IBlockState getCasingManaPylon() {
        return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
    }
    private static IBlockState getCasingDragonStone() {
        return ModBlocks.storage.getDefaultState().withProperty(BotaniaStateProps.STORAGE_VARIANT, StorageVariant.DRAGONSTONE);
    }
    //切换输出模式
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (!this.getWorld().isRemote) {
            if (playerIn.isSneaking() && !this.isActive()) {
                if(modeIndex==0){
                    modeIndex=1;
                } else {
                    modeIndex=0;
                }
                playerIn.sendStatusMessage(new TextComponentTranslation("pollution.modeChanged.message",new Object[0]),true);
            }else if(playerIn.isSneaking() && this.isActive()){
                playerIn.sendStatusMessage(new TextComponentTranslation("gregtech.multiblock.multiple_recipemaps.switch_message", new Object[0]), true);
            }

        }
            return true;
    }
    //将模式数据存进nbt
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("Mode", this.modeIndex);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.modeIndex = data.getInteger("Mode");
    }
    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        this.outputFluidInventory = new FluidTankList(this.allowSameFluidFillForOutputs(), this.getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        List<IEnergyContainer> energyContainer = new ArrayList<>(this.getAbilities(MultiblockAbility.OUTPUT_ENERGY));
        energyContainer.addAll(this.getAbilities(MultiblockAbility.OUTPUT_LASER));
        this.energyContainer = new EnergyContainerList(energyContainer);
    }
    //GUI信息
    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockFuelRecipeLogic recipeLogic = (MultiblockFuelRecipeLogic)this.recipeMapWorkable;
        MultiblockDisplayText.builder(textList, this.isStructureFormed()).setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive()).addEnergyProductionLine(this.getMaxVoltage(), (long)recipeLogic.getRecipeEUt());
        textList.add(TextComponentUtil.stringWithColor(TextFormatting.AQUA, "花园内部EU缓存: " + EnergyBuffer));
        textList.add(TextComponentUtil.stringWithColor(TextFormatting.AQUA, "花园输出模式: " + modeText()));
        textList.add(TextComponentUtil.stringWithColor(TextFormatting.AQUA, "花园动力仓容量: " + this.energyContainer.getEnergyCapacity()));
        textList.add(TextComponentUtil.stringWithColor(TextFormatting.AQUA, "花园动力仓存储能量: " + this.energyContainer.getEnergyStored()));
    }

    private String modeText(){
        String modeText;
        if(modeIndex==0){
            modeText = I18n.format("pollution.machine.pollution_multi_dan_de_life_on.mode0");
        } else {modeText = I18n.format("pollution.machine.pollution_multi_dan_de_life_on.mode1");}
        return modeText;
    }
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object CoilLevel = context.get("COILTieredStats");
        this.CoilLevel = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) CoilLevel).getIntTier(),
                0);
        List<IEnergyContainer> energyContainer = new ArrayList<>(this.getAbilities(MultiblockAbility.OUTPUT_ENERGY));
        energyContainer.addAll(this.getAbilities(MultiblockAbility.OUTPUT_LASER));
        EnergyContainerList outEnergyContainer = new EnergyContainerList(energyContainer);

        // Initialize XPos, YPos, and ZPos here
        this.XPos = this.getPos().getX();
        this.YPos = this.getPos().getY();
        this.ZPos = this.getPos().getZ();
    }
    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();
        long residualEnergyCapacity = this.energyContainer.getEnergyCapacity() - this.energyContainer.getEnergyStored();
        if (EnergyBuffer != 0) {
            if(residualEnergyCapacity != 0 && modeIndex==0){
                if (EnergyBuffer >= residualEnergyCapacity) {
                this.energyContainer.addEnergy(residualEnergyCapacity);
                EnergyBuffer = EnergyBuffer - residualEnergyCapacity;
                } else {
                this.energyContainer.addEnergy(EnergyBuffer);
                EnergyBuffer = 0;
                }
            }
            if(modeIndex==1){
                EnergyBuffer+=this.energyContainer.getEnergyStored();
                this.energyContainer.addEnergy(-residualEnergyCapacity);
                FluidStack ManaOutput= GTQTMaterials.Richmagic.getFluid((int) (1.25*EnergyBuffer / POConfig.MachineSettingSwitch.EuPerMbRichMagicKq));
                this.outputFluidInventory.fill(ManaOutput,true);
                EnergyBuffer=0;

            }

        }
    }

    private boolean CellingLiveOrNot(int CellXPos, int CellYPos, int CellZPos) {
        int liveNeighbors = 0;
        boolean isItaLive;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // Skip the cell itself
                if (getWorld().getBlockState(new BlockPos(CellXPos + dx, CellYPos, CellZPos + dz)).getBlock().equals(ModBlocks.cellBlock)) {
                    liveNeighbors++;
                }
            }
        }
        boolean BlockItselfIsAlive = getWorld().getBlockState(new BlockPos(CellXPos, CellYPos, CellZPos)).getBlock().equals(ModBlocks.cellBlock);
        if (BlockItselfIsAlive) {
            isItaLive = liveNeighbors > 1 && liveNeighbors < 4;
        }
         else{
            isItaLive = liveNeighbors == 3;
            }
         return isItaLive;
        }


    @Override
    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return POTextures.MANA_4;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    protected class MultiDanDeLifeOnWorkable extends MultiblockFuelRecipeLogic {
        public MultiDanDeLifeOnWorkable(FuelMultiblockController tileEntity) {
            super(tileEntity);
            this.multiDandelifeon = (MetaTileEntityMultiDanDeLifeOn) tileEntity;
        }
        public long EnergyPerAgeForCellDied(int CellAge){
            return (long) (pow(this.multiDandelifeon.CoilLevel,1.5)*2560000*pow(CellAge,(1/3))+22937600*2/3);
        }
        private final MetaTileEntityMultiDanDeLifeOn multiDandelifeon;

        @Override
        protected void updateRecipeProgress() {
            if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true)) {
                this.drawEnergy(this.recipeEUt, false);
                if (++this.progressTime > this.maxProgressTime) {
                    this.completeRecipe();
                    EnumFacing facing = this.multiDandelifeon.getFrontFacing();
                    int startX = XPos;
                    int startZ = ZPos;
                    switch (facing) {
                        case NORTH:
                            startX = XPos - 15;
                            startZ = ZPos + 16;
                            break;
                        case SOUTH:
                            startX = XPos - 15;
                            startZ = ZPos - 46;
                            break;
                        case WEST:
                            startX = XPos + 16;
                            startZ = ZPos - 15;
                            break;
                        case EAST:
                            startX = XPos - 46;
                            startZ = ZPos - 15;
                            break;
                    }
                    for (int x = 0; x < 31; x++) {
                        for (int z = 0; z < 31; z++) {
                            int cellX = startX + x;
                            int cellZ = startZ + z;
                            IsCellAlive[x][z] = CellingLiveOrNot(cellX, YPos + 1, cellZ);
                        }
                    }
                    for (int x = 0; x < 31; x++) {
                        for (int z = 0; z < 31; z++) {
                            int cellX = startX + x;
                            int cellZ = startZ + z;
                            if (IsCellAlive[x][z]) {
                                if(!this.metaTileEntity.getPos().add(cellX - XPos, 1, cellZ - ZPos).equals(ModBlocks.cellBlock)){
                                this.metaTileEntity.getWorld().setBlockState(
                                        this.metaTileEntity.getPos().add(cellX - XPos, 1, cellZ - ZPos),
                                        ModBlocks.cellBlock.getDefaultState()
                                );
                                }
                                CellAge[x][z]++;
                            } else {
                                this.metaTileEntity.getWorld().setBlockState(
                                        this.metaTileEntity.getPos().add(cellX - XPos, 1, cellZ - ZPos),
                                        Blocks.AIR.getDefaultState()
                                );
                                if(CellAge[x][z]!=0){
                                EnergyBuffer +=  EnergyPerAgeForCellDied(CellAge[x][z]);
                                CellAge[x][z] = 0; // Reset cell age after it dies
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public long getMaxVoltage() {
            return super.getMaxVoltage();
        }

        @Override
        protected long getMaxParallelVoltage() {
            return this.getMaxVoltage();
        }

        public int parallelLimit() {
            return 1;
        }

        @Override
        public int getParallelLimit() {
            return parallelLimit();
        }

        @Override
        public long getMaximumOverclockVoltage() {
            return getMaxVoltage();
        }

        @Override
        public boolean isAllowOverclocking() {
            return false;
        }
    }
}