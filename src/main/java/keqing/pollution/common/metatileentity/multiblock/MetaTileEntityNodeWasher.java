package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.api.metatileentity.MetaTileEntityBaseWithControl;
import keqing.pollution.Pollution;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POTurbine;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.log;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static net.minecraft.util.math.MathHelper.ceil;

public class MetaTileEntityNodeWasher extends MetaTileEntityBaseWithControl {
    @Override
    public boolean usesMui2() {
        return false;
    }
    //随机数
    private final Random random = Pollution.RANDOM;
    //六种流体
    private final FluidStack INFUSED_AIR = PollutionMaterials.infused_air.getFluid(1);
    private final FluidStack INFUSED_FIRE = PollutionMaterials.infused_fire.getFluid(1);
    private final FluidStack INFUSED_EARTH = PollutionMaterials.infused_earth.getFluid(1);
    private final FluidStack INFUSED_WATER = PollutionMaterials.infused_water.getFluid(1);
    private final FluidStack INFUSED_ORDER = PollutionMaterials.infused_order.getFluid(1);
    private final FluidStack INFUSED_ENTROPY = PollutionMaterials.infused_entropy.getFluid(1);
    //计时器，洗点时间
    private final int timer = 0;
    //线圈等级
    private int coilLevel;
    //选择洗的属性
    private String infusedType;
    //最高洗点数值
    private int maxInfusedValue;
    private int timeCost;

    public MetaTileEntityNodeWasher(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.AAMINATED_GLASS);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.POLYTETRAFLUOROETHYLENE_PIPE);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityNodeWasher(this.metaTileEntityId);
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object coilLevel = context.get("COILTieredStats");
        this.coilLevel = POUtils.getOrDefault(() -> coilLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) coilLevel).getIntTier(),
                0);
    }

    private String decideType(FluidStack stack) {
        String type = "";
        return type;
    }

    protected void updateFormedValid() {
        if (!this.isActive()) {
            setActive(true);
        }
        //计算最高洗点数值
        int EUtTier = ceil(log((double) this.energyContainer.getInputVoltage() / 32) / log(4) + 1);
        maxInfusedValue = coilLevel * EUtTier * 25;
        //首先如果输入仓内部有流体，能源仓有电(EV)，则消耗掉输入总线里面的节点
        if (this.getInputInventory().getStackInSlot(0) == PollutionMetaItems.PACKAGED_AURA_NODE.getStackForm()
                && this.energyContainer.getEnergyStored() > 1920) {
        }

    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.node_washer.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.node_washer.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.node_washer.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.node_washer.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.node_washer.tooltip.5"));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("pollution.machine.node_washer.infusedtype", this.infusedType).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.node_washer.maxinfusedvalue", this.maxInfusedValue)).setStyle((new Style()).setColor(TextFormatting.RED)));
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXXXX", "XXXXXXX", "XXXXXXX", "##XXXXX")
                .aisle("XXXXXXX", "XAXCCCX", "XXXAAAX", "##XXXXX")
                .aisle("XXXXXXX", "XAXCCCX", "XXXAAAX", "##XXXXX")
                .aisle("XXXXXXX", "XSXDDDX", "XEXDDDX", "##XXXXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(30)
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setExactLimit(1).setPreviewCount(1)))
                .where('C', states(getCasingState2()))
                .where('D', states(getCasingState3()))
                .where('A', states(getCasingState4()))
                .where('E', CP_COIL_CASING.get())
                .where('#', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_HOT;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }
}
