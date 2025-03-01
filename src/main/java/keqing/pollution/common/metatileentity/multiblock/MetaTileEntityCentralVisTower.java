package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTTransferUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.api.metaileentity.MetaTileEntityBaseWithControl;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POManaPlate;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aura.AuraHelper;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_FRAME;

public class MetaTileEntityCentralVisTower extends MetaTileEntityBaseWithControl {
    private final int cleaningPeriod = 400;
    MetaTileEntityManaHatch manaHatch;
    private int timer = 0;
    private final boolean canWork = true;
    private int frameLevel;

    private int manaConsumptionSpeed;

    private float fluxCleaned = 0;
    private float auraSupplemented = 0;
    private int goodChunkAmount = 0;

    public MetaTileEntityCentralVisTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.hyperdimensional_silver).getBlock(PollutionMaterials.hyperdimensional_silver);
    }

    private static IBlockState getCasingState3() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
    }

    private static IBlockState getCasingState4() {
        return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
    }

    private static IBlockState getCasingState5() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_1);
    }

    private static IBlockState getCasingState6() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_1);
    }

    private static IBlockState getCasingState7() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
    }

    private static IBlockState getCasingState8() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState9() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object frameLevel = context.get("FRAMETieredStats");
        this.frameLevel = POUtils.getOrDefault(() -> frameLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) frameLevel).getIntTier(),
                0);
        for (Map.Entry<String, Object> str : context.entrySet()) {
            if (str.getKey().startsWith("Multi")) {
                HashSet set = (HashSet) str.getValue();
                for (var s : set
                ) {
                    if (s instanceof MetaTileEntityManaHatch) {
                        this.manaHatch = (MetaTileEntityManaHatch) s;
                    }
                }
            }
        }
    }

    private boolean consumeMana() {
        return this.manaHatch.consumeMana(manaConsumptionSpeed,false);
    }

    private void produceMansus(float fluxCleaned, float auraSupplemented, int goodChunkAmount) {
        FluidStack BLACK_MANSUS = PollutionMaterials.blackmansus.getFluid((int) (fluxCleaned * manaConsumptionSpeed * 10));
        FluidStack STARRY_MANSUS = PollutionMaterials.starrymansus.getFluid(goodChunkAmount * manaConsumptionSpeed);
        GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(BLACK_MANSUS));
        //可生产漫宿的区块等于区块数量，即可生成
        if (goodChunkAmount == Math.pow(2 * frameLevel - 1, 2)) {
            GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(STARRY_MANSUS));
        } else if (frameLevel == 1 && goodChunkAmount == 9) {
            GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(STARRY_MANSUS));
        }
        if (auraSupplemented <= 0) {
            FluidStack WHITE_MANSUS = PollutionMaterials.whitemansus.getFluid((int) (manaConsumptionSpeed * auraSupplemented * -10));
            GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(WHITE_MANSUS));
        }
    }

    @Override
    protected void updateFormedValid() {
        if (!this.isActive()) {
            setActive(true);
        }
        // 获取设备的位置坐标
        int aX = this.getPos().getX();
        int aY = this.getPos().getY();
        int aZ = this.getPos().getZ();

        manaConsumptionSpeed = (int) (4 * Math.pow(2, (Math.max(0, this.manaHatch.getTier() - 2))));

        if (canWork) {
            timer++;
            consumeMana();
        }

        if (timer >= cleaningPeriod) {
            fluxCleaned = auraSupplemented = goodChunkAmount = 0;
            int tier = Math.max(1, frameLevel - 1);
            for (int x = -tier; x <= tier; x++) {
                for (int z = -tier; z <= tier; z++) {
                    BlockPos pos = new BlockPos(aX + x * 16, aY, aZ + z * 16);
                    float visThisChunk = AuraHelper.getVis(this.getWorld(), pos);
                    float fluxThisChunk = AuraHelper.getFlux(this.getWorld(), pos);
                    float difference = visThisChunk - AuraHelper.getAuraBase(this.getWorld(), pos);
                    //当清除咒波和供给灵气在这个区块内没有超过阈值，这个区块被视为可生产星辰漫宿的
                    float overAllAmount = fluxThisChunk + difference;
                    if (overAllAmount <= 4.2f) {
                        goodChunkAmount += 1;
                    }
                    if (consumeMana()) {
                        //清除每个区块80%污染，补充灵气，同时计算清除量和补充量
                        fluxCleaned += 0.8f * fluxThisChunk;
                        AuraHelper.drainFlux(this.getWorld(), pos, 0.8f * fluxThisChunk, false);
                        auraSupplemented += -0.8f * difference;
                        //灵气差大于零就降灵气，反之增灵气
                        if (difference > 0) {
                            AuraHelper.drainVis(this.getWorld(), pos, 0.8f * difference, false);
                        } else if (difference < 0) {
                            AuraHelper.addVis(this.getWorld(), pos, -0.8f * difference);
                        }
                    }
                }
            }
            produceMansus(fluxCleaned, auraSupplemented, goodChunkAmount);
            timer = 0;
        }
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("AB           BA", "C             C", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "       D       ", "       E       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("BAB         BAB", " C           C ", " C           C ", " C           C ", "               ", "               ", "               ", "               ", "               ", "       A       ", "    DDDFDDD    ", "       G       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle(" BAB       BAB ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  CA       AC  ", "   CAAACAAAC   ", "   DFFFDFFFD   ", "    GGGCGGG    ", "     H H H     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "       D       ", "       E       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("  BABBBBBBBAB  ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "  ACA     ACA  ", "  CAC     CAC  ", "  DFDDDCDDDFD  ", "   G   G   G   ", "   H   C   H   ", "     H H H     ", "      AAA      ", "      HHH      ", "      AAA      ", "       H       ", "       H       ", "       A       ", "     DDJDD     ", "       G       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("   BACCFCCAB   ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "   AA     AA   ", "  ACC     CCA  ", " DFD       DFD ", "  G         G  ", "               ", "    H     H    ", "    HAHHHAH    ", "     HBBBH     ", "     AHHHA     ", "     H   H     ", "     H   H     ", "     AACAA     ", "    DJJDJJD    ", "     GGCGG     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "       D       ", "       E       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("   BCBBKBBCB   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  A         A  ", " DFD       DFD ", "  G         G  ", "  H         H  ", "   H       H   ", "    AH   HA    ", "    HBHHHBH    ", "    AH   HA    ", "    H     H    ", "    H     H    ", "    A  A  A    ", "   DJDDCDDJD   ", "    G  H  G    ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       A       ", "      DFD      ", "       G       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("   BCBKKKBCB   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  A         A  ", " DFD       DFD ", "  G         G  ", "               ", "               ", "   AH     HA   ", "   HBH   HBH   ", "   AH     HA   ", "               ", "               ", "    A     A    ", "   DJD J DJD   ", "    G     G    ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "      ACA      ", "     DFDFD     ", "      GHG      ", "       H       ", "       H       ", "       C       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("   BFKKKKKFB   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", " AC         CA ", "DFDC       CDFD", "EGCG       GCGE", "  HC       CH  ", "   H       H   ", "   AH     HA   ", "   HBH   HBH   ", "   AH     HA   ", "   H       H   ", "   H       H   ", "   ACA   ACA   ", "  DJDCJBJCDJD  ", "  EGCH B HCGE  ", "     H B H     ", "     H B H     ", "     H B H     ", "     H B H     ", "     H B H     ", "     H B H     ", "     H B H     ", "     ACBCA     ", "    DFDBDFD    ", "    EGHBHGE    ", "      HBH      ", "      HBH      ", "      C C      ", "       C       ", "       C       ", "       C       ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       J       ")
                .aisle("   BCBKKKBCB   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  A         A  ", " DFD       DFD ", "  G         G  ", "               ", "               ", "   AH     HA   ", "   HBH   HBH   ", "   AH     HA   ", "               ", "               ", "    A     A    ", "   DJD J DJD   ", "    G     G    ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "      ACA      ", "     DFDFD     ", "      GHG      ", "       H       ", "       H       ", "       C       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("   BCBBKBBCB   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  A         A  ", " DFD       DFD ", "  G         G  ", "  H         H  ", "   H       H   ", "    AH   HA    ", "    HBHHHBH    ", "    AH   HA    ", "    H     H    ", "    H     H    ", "    A  A  A    ", "   DJDDCDDJD   ", "    G  H  G    ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       H       ", "       A       ", "      DFD      ", "       G       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("   BACCFCCAB   ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "    A     A    ", "   AA     AA   ", "  ACC     CCA  ", " DFD       DFD ", "  G         G  ", "               ", "    H     H    ", "    HAHHHAH    ", "     HBBBH     ", "     AHHHA     ", "     H   H     ", "     H   H     ", "     AACAA     ", "    DJJDJJD    ", "     GGCGG     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "       D       ", "       E       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("  BABBBBBBBAB  ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "   A       A   ", "  ACA     ACA  ", "  CAC     CAC  ", "  DFDDDCDDDFD  ", "   G   G   G   ", "   H   C   H   ", "     H H H     ", "      AAA      ", "      HSH      ", "      AAA      ", "       H       ", "       H       ", "       A       ", "     DDJDD     ", "       G       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle(" BAB       BAB ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  C         C  ", "  CA       AC  ", "   CAAACAAAC   ", "   DFFFDFFFD   ", "    GGGCGGG    ", "     H H H     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "       D       ", "       E       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("BAB         BAB", " C           C ", " C           C ", " C           C ", "               ", "               ", "               ", "               ", "               ", "               ", "    DDDFDDD    ", "       G       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .aisle("AB           BA", "C             C", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "       D       ", "       E       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                .where('S', selfPredicate())
                .where('A', states(getCasingState()))
                .where('B', CP_FRAME.get())
                .where('C', states(getCasingState2()))
                .where('D', states(getCasingState3()))
                .where('E', states(getCasingState4()))
                .where('F', states(getCasingState5()))
                .where('G', states(getCasingState6()))
                .where('H', states(getCasingState7()))
                .where('J', states(getCasingState8()))
                .where('K', states(getCasingState9()).setMinGlobalLimited(5)
                        .or(abilities(POMultiblockAbility.MANA_HATCH).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(3).setPreviewCount(3)))
                .where(' ', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MANA_BASIC;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCentralVisTower(this.metaTileEntityId);
    }

    //tooltip
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.5"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.6"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.7"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.8"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.9"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.10"));
        tooltip.add(I18n.format("pollution.machine.central_vis_tower.tooltip.11"));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("pollution.machine.central_vis_tower_canwork", this.canWork).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.central_vis_tower_framelevel", this.frameLevel)).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.central_vis_tower_timer", this.timer)).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.central_vis_tower_manatier", this.manaHatch.getTier())).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.central_vis_tower_fluxcleaned", this.fluxCleaned)).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.central_vis_tower_aurasupplemented", this.auraSupplemented)).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.central_vis_tower_goodchunkamount", this.goodChunkAmount)).setStyle((new Style()).setColor(TextFormatting.RED)));
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }
}
