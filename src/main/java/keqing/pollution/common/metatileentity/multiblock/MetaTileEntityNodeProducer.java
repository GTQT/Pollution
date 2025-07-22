package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.RelativeDirection;
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
import keqing.pollution.common.block.metablocks.*;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static net.minecraft.util.math.MathHelper.abs;
import static net.minecraft.util.math.MathHelper.ceil;

public class MetaTileEntityNodeProducer extends MetaTileEntityBaseWithControl {
    @Override
    public boolean usesMui2() {
        return false;
    }
    //随机数
    private final Random random = Pollution.RANDOM;
    //最低输入功率，默认为1920
    private final int EUt = 1920;
    //计时器和计数器
    private int timer = 0;
    private int tickCount = 0;
    //电压等级
    private int EUtTier;
    //生产时长 默认30s一次有损超频
    private int Duration;
    //每tick源质消耗
    private int infusedCost;
    //线圈等级
    private int coilLevel = 0;

    public MetaTileEntityNodeProducer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.FUSION_REACTOR.getState(POFusionReactor.FusionBlockType.FRAME_II);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.VOID_PRISM);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_0);
    }

    private static IBlockState getCasingState5() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_1);
    }

    private static IBlockState getCasingState6() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM);
    }

    private static IBlockState getCasingState7() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX);
    }

    private static IBlockState getCasingState8() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState9() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
    }

    private static IBlockState getCasingState10() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_3);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityNodeProducer(this.metaTileEntityId);
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

    public ItemStack getRandomAuraNode() {
        ItemStack auraNode = new ItemStack(PollutionMetaItems.PACKAGED_AURA_NODE.getMetaItem(), 1, 100);
        NBTTagCompound nodeTagCompound = new NBTTagCompound();
        double tire_probability = random.nextDouble() * 100; // 生成0到100之间的随机数
        double type_probability = random.nextDouble() * 100;
        int air_probability = min(abs(ceil(random.nextGaussian() * 100)), 1000);
        int fire_probability = min(abs(ceil(random.nextGaussian() * 100 * (0.9 + 0.1 * coilLevel))), 1000);
        int water_probability = min(abs(ceil(random.nextGaussian() * 100)), 1000);
        int earth_probability = min(abs(ceil(random.nextGaussian() * 100)), 1000);
        int order_probability = min(abs(ceil(random.nextGaussian() * 100 * (0.6 + 0.1 * EUtTier))), 1000);
        int entropy_probability = min(abs(ceil(random.nextGaussian() * 100)), 1000);
        String nodeTire;
        if (tire_probability < 60) {
            nodeTire = "Normal"; // 60%概率生成正常节点
        } else if (tire_probability < 80) {
            nodeTire = "Withering"; // 20%概率生成凋零节点
        } else if (tire_probability < 85) {
            nodeTire = "Bright"; // 5%概率生成明亮节点
        } else {
            nodeTire = "Pale"; // 剩余15%概率生成苍白节点
        }
        nodeTagCompound.setString("NodeTire", nodeTire);
        String nodeType;
        if (type_probability < 60) {
            nodeType = "Standard";
        } else if (type_probability < 70) {
            nodeType = "Ominous";
        } else if (type_probability < 80) {
            nodeType = "Pure";
        } else if (type_probability < 95) {
            nodeType = "Concussive";
        } else {
            nodeType = "Voracious";
        }
        nodeTagCompound.setString("NodeType", nodeType);
        nodeTagCompound.setInteger("EssenceAir", air_probability);
        nodeTagCompound.setInteger("EssenceFire", fire_probability);
        nodeTagCompound.setInteger("EssenceWater", water_probability);
        nodeTagCompound.setInteger("EssenceEarth", earth_probability);
        nodeTagCompound.setInteger("EssenceOrder", order_probability);
        nodeTagCompound.setInteger("EssenceEntropy", entropy_probability);
        auraNode.setTagCompound(nodeTagCompound); // 将NBTTagCompound附加到auraNode上
        return auraNode;
    }

    private boolean isOutputFull() {
        return !GTTransferUtils.addItemsToItemHandler(this.outputInventory, true, Collections.singletonList(PollutionMetaItems.PACKAGED_AURA_NODE.getStackForm()));
    }

    protected void updateFormedValid() {
        if (!this.isActive()) {
            setActive(true);
        }
        EUtTier = ceil(log((double) this.energyContainer.getInputVoltage() / 32) / log(4) + 1);
        Duration = ceil((float) 30 / (EUtTier - 3));
        infusedCost = (int) (144 * pow(2, EUtTier - 4));
        FluidStack INFUSED_ENERGY = PollutionMaterials.infused_energy.getFluid(infusedCost);
        List<IFluidTank> fluidInputInventory = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        //检测是否可以工作，输入仓是不是所需流体
        //每个tick都消耗一份流体，如果中途不足，立刻停止配方，计数器也随之恢复
        if (this.isWorkingEnabled() && INFUSED_ENERGY.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_ENERGY, false))) {
            if (this.energyContainer.getInputVoltage() > EUt && this.energyContainer.getEnergyStored() >= this.energyContainer.getInputVoltage()) {
                tickCount++;
                if (fluidInputInventory.get(0).getFluidAmount() >= infusedCost && !isOutputFull()) {
                    this.energyContainer.removeEnergy(this.energyContainer.getInputVoltage());
                    this.inputFluidInventory.drain(INFUSED_ENERGY, true);
                } else {
                    timer = 0; //重置进度
                }
                if (tickCount % 20 == 0) { // 每20个tick（1秒）执行一次
                    timer++;
                    if (timer >= Duration) { // 每duration秒执行一次
                        timer = 0; // 重置计时器
                        GTTransferUtils.insertItem(this.outputInventory, getRandomAuraNode(), false);
                    }
                }
            }
        }
    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.node_producer.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.node_producer.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.node_producer.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.node_producer.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.node_producer.tooltip.5"));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("pollution.machine.node_producer_duration", this.Duration).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.node_producer_infusedcost", this.infusedCost)).setStyle((new Style()).setColor(TextFormatting.RED)));
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle("                    ", "                    ", "                    ", "                    ", "                    ", "                    ", "                    ", "      A     A       ", "      B     B       ", "      B     B       ", "      B     B       ", "      A     A       ", "                    ", "                    ", "                    ", "                    ")
                .aisle("    CCCCCCCCCCC     ", "    BBBBBBBBBBB     ", "    BBBBBBBBBBB     ", "    CCCCCCCCCCC     ", "                    ", "      B     B       ", "      D     E       ", "     AAA   AAA      ", "     B B   B B      ", "     B B   B B      ", "     B B   B B      ", "     AAA   AAA      ", "      A     A       ", "      D     E       ", "                    ", "                    ")
                .aisle("   CFFFFFFFFFFFCGFCF", "   CGGGGGGGGGGGC  B ", "   C           C  B ", "   C           C  B ", "   C  B     B  C  B ", "   C           C  B ", "   C           C  B ", "   C  A     A  C GAG", "      B     B     A ", "      B     B       ", "      B     B       ", "      A     A       ", "                    ", "                    ", "                    ", "                    ")
                .aisle("  CFAAAAAAAAAAACGCCC", "   AGAAAAAAAAAGA BHB", "   A           A BHB", "   A  B     B  A BHB", "   A           A BHB", "   A           A BHB", "   A           A BHB", "  GA           AGAAA", "                 AAA", "                  A ", "                  H ", "                    ", "                    ", "                    ", "                    ", "                    ")
                .aisle(" CFAAAAAAAAAAAACGFCF", "  GFGAIFFIFFIAGA  B ", "  GF  B     B  B  B ", "  GF           B  B ", "  GA           B  B ", "  GA           B  B ", "  GA           A  B ", " GAFA         AFAGAG", "   B           B  A ", "   B           B    ", "   B           B    ", "   B           B    ", "   B           B    ", "   A           A    ", "                    ", "                    ")
                .aisle("XFAAAAAAAAAAAAACG F ", "  BGGAFIEIEIFAGA    ", "  F            B    ", "  F            B    ", "  A            B    ", "  A            B    ", "  A            A    ", " AFHFA       AFHFAG ", "  B B         B B   ", "  B B         B B   ", "  B B         B B   ", "  B B         B B   ", "  B B         B B   ", "  AFA         AAA   ", "   A           A    ", "   H           H    ")
                .aisle("XFAAAAAAAAAAAAACGFCF", " FBGGAFEIIIEFAGA  B ", " BB      A     B  B ", "  H            B  B ", "  A            B  B ", "  H            B  B ", "  B            A  B ", " BAFA         AFAGAG", "   B           B  A ", "   B           B    ", "   B           B    ", "   B           B    ", "   B           B    ", "   A           A    ", "                    ", "                    ")
                .aisle("XFAAAAAAAAAAAAACGCCC", " SBGGAIIIIIIIAGA BHB", " FB     AOA    B BHB", "  A     B B    B BHB", "  H    B   B   B BHB", "  A   B     B  B BHB", "  B  B       B A BHB", " BBAB         BAGAAA", "                 AAA", "                  A ", "                  H ", "                    ", "                    ", "                    ", "                    ", "                    ")
                .aisle("XFAAAAAAAAAAAAACGFCF", " FBGGAFEIIIEFAGA  B ", " BB      A     B  B ", "  H            B  B ", "  A            B  B ", "  H            B  B ", "  B            A  B ", " BAFA         AFAGAG", "   B           B  A ", "   B           B    ", "   B           B    ", "   B           B    ", "   B           B    ", "   A           A    ", "                    ", "                    ")
                .aisle("XFAAAAAAAAAAAAACG F ", "  BGGAFIEIEIFAGA    ", "  F            B    ", "  F            B    ", "  A            B    ", "  A            B    ", "  A            A    ", " AFHFA       AFHFAG ", "  B B         B B   ", "  B B         B B   ", "  B B         B B   ", "  B B         B B   ", "  B B         B     ", "  AFA         AAA   ", "   A           A    ", "   H           H    ")
                .aisle(" CFAAAAAAAAAAAACGFCF", "  GFGAIFFIFFIAGA  B ", "  GF  B     B  B  B ", "  GF           B  B ", "  GA           B  B ", "  GA           B  B ", "  GA           A  B ", " GAFA         AFAGAG", "   B           B  A ", "   B           B    ", "   B           B    ", "   B           B    ", "   B           B    ", "   A           A    ", "                    ", "                    ")
                .aisle("  CFAAAAAAAAAAACGCCC", "   AGAAAAAAAAAGA BHB", "   A           A BHB", "   A  B     B  A BHB", "   A           A BHB", "   A           A BHB", "   A           A BHB", "  GA           AGAAA", "                 AAA", "                  A ", "                  H ", "                    ", "                    ", "                    ", "                    ", "                    ")
                .aisle("   CFFFFFFFFFFFCGFCF", "   CGGGGGGGGGGGC  B ", "   C           C  B ", "   C           C  B ", "   C  B     B  C  B ", "   C           C  B ", "   C           C  B ", "   C  A     A  C GAG", "      B     B     A ", "      B     B       ", "      B     B       ", "      A     A       ", "                    ", "                    ", "                    ", "                    ")
                .aisle("    CCCCCCCCCCC     ", "    BBBBBBBBBBB     ", "    BBBBBBBBBBB     ", "    CCCCCCCCCCC     ", "                    ", "      B     B       ", "      K     L       ", "     AAA   AAA      ", "     B B   B B      ", "     B B   B B      ", "     B B   B B      ", "     AAA   AAA      ", "      A     A       ", "      K     L       ", "                    ", "                    ")
                .aisle("                    ", "                    ", "                    ", "                    ", "                    ", "                    ", "                    ", "      A     A       ", "      B     B       ", "      B     B       ", "      B     B       ", "      A     A       ", "                    ", "                    ", "                    ", "                    ")
                .where('S', selfPredicate())
                .where(' ', any())
                .where('A', states(getCasingState()))
                .where('X', states(getCasingState())
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1).setPreviewCount(1)))
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS).setExactLimit(1).setPreviewCount(1))
                .where('B', states(getCasingState2()))
                .where('C', states(getCasingState3()))
                .where('D', states(getCasingState4()))
                .where('E', states(getCasingState5()))
                .where('F', states(getCasingState6()))
                .where('G', states(getCasingState7()))
                .where('H', states(getCasingState8()))
                .where('I', CP_COIL_CASING.get())
                .where('K', states(getCasingState9()))
                .where('L', states(getCasingState10()))
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.FRAME_I;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

}

