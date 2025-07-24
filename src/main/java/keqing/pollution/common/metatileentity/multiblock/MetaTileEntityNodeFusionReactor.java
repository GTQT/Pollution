package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.logic.OCParams;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.recipes.properties.impl.FusionEUToStartProperty;
import gregtech.api.util.GTUtility;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityFusionReactor;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.gtqtcore.GTQTCoreConfig;
import keqing.pollution.POConfig;
import keqing.pollution.api.capability.ICleanVis;
import keqing.pollution.api.capability.ipml.POMultiblockCleanVisRecipeLogic;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.objmodels.ObjModels;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POHyper;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MetaTileEntityNodeFusionReactor extends MultiMapMultiblockController implements ICleanVis, IFastRenderMetaTileEntity {

    private final int tier;
    //漫宿类型
    private final FluidStack BLACK_MANSUS = PollutionMaterials.blackmansus.getFluid(1);
    private final FluidStack WHITE_MANSUS = PollutionMaterials.whitemansus.getFluid(1);
    private final FluidStack STARRY_MANSUS = PollutionMaterials.starrymansus.getFluid(1);
    @SideOnly(Side.CLIENT)
    //计时器
    private int timer;
    //机器节点因子相关
    private int overallParallelAmount = 1;
    private int overallProgressTimeAmount = 0;
    private int overallEnergyAmount = 0;
    private int overallBlackAmount = 0;
    private int overallWhiteAmount = 0;
    private int overallStarryAmount = 0;
    //机器属性
    private EnergyContainerList inputEnergyContainer;
    private long heat = 0L;
    private long energyInternal = 0L;
    //漫宿供应检测
    private boolean isMansusSupplied = false;

    public MetaTileEntityNodeFusionReactor(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.FUSION_RECIPES, PORecipeMaps.NODE_MAGIC_FUSION_RECIPES});
        this.tier = tier;
        this.recipeMapWorkable = new NodeFusionReactorRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_1);
    }

    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.inputEnergyContainer = new EnergyContainerList(energyContainer);
        long euCapacity = this.calculateEnergyStorageFactor(energyContainer.size());
        this.energyContainer = new EnergyContainerHandler(this, euCapacity, GTValues.V[this.tier], 0L, 0L, 0L) {
            public @NotNull String getName() {
                return "EnergyContainerInternal";
            }
        };
        //把nbt数据写入主方块缓存
        this.energyContainer.addEnergy(energyInternal);
    }

    private long calculateEnergyStorageFactor(int energyInputAmount) {
        return (long) energyInputAmount * (long) Math.pow(2.0, this.tier - 6) * 10000000L;
    }

    //区块超稳的基类方法复读
    @Override
    public boolean isCleanVis() {
        int aX = this.getPos().getX();
        int aY = this.getPos().getY();
        int aZ = this.getPos().getZ();
        BlockPos pos = new BlockPos(aX, aY, aZ);
        float fluxThisChunk = AuraHelper.getFlux(this.getWorld(), pos);
        float difference = AuraHelper.getVis(this.getWorld(), pos) - AuraHelper.getAuraBase(this.getWorld(), pos);
        return fluxThisChunk <= 4.2f && Math.abs(difference) <= 4.2f;
    }

    //聚变update逻辑：和普通聚变一样，但是额外检测节点属性
    @Override
    public void updateFormedValid() {
        //把能量吸入内部缓存
        if (this.inputEnergyContainer.getEnergyStored() > 0L) {
            long energyAdded = this.energyContainer.addEnergy(this.inputEnergyContainer.getEnergyStored());
            if (energyAdded > 0L) {
                this.inputEnergyContainer.removeEnergy(energyAdded);
            }
        }
        super.updateFormedValid();
        timer++;
        List<Integer> nodeInfo;
        //每秒检测一次nodeInfo，不然太卡了
        if (timer >= 20) {
            nodeInfo = readNodeInfo();
            overallParallelAmount = nodeInfo.get(0);
            overallEnergyAmount = nodeInfo.get(1);
            overallProgressTimeAmount = nodeInfo.get(2);
            overallBlackAmount = nodeInfo.get(3);
            overallWhiteAmount = nodeInfo.get(4);
            overallStarryAmount = nodeInfo.get(5);
            //漫宿也是每秒消耗一次
            if (this.getRecipeMap() == PORecipeMaps.NODE_MAGIC_FUSION_RECIPES) {
                if (overallBlackAmount != 0 || overallWhiteAmount != 0 || overallStarryAmount != 0) {
                    if (checkMansus()) {
                        //只有工作状态消耗漫宿
                        if (this.getRecipeLogic().isWorking()) {
                            consumeMansus();
                        }
                        //不管工作与否都把漫宿检测返回
                        isMansusSupplied = true;
                    } else {
                        isMansusSupplied = false;
                    }
                } else {
                    isMansusSupplied = true;
                }
            }
            timer = 0;
        }
    }

    //感谢kzp对本函数的大力支持！
    private void consumeMansus() {
        List<IFluidTank> fluidInputInventory = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        for (IFluidTank iFluidTank : fluidInputInventory) {
            if (BLACK_MANSUS.isFluidEqual(iFluidTank.getFluid())) {
                iFluidTank.drain(overallBlackAmount, true);
            }
            if (WHITE_MANSUS.isFluidEqual(iFluidTank.getFluid())) {
                iFluidTank.drain(overallWhiteAmount, true);
            }
            if (STARRY_MANSUS.isFluidEqual(iFluidTank.getFluid())) {
                iFluidTank.drain(overallStarryAmount, true);
            }
        }

    }

    //感谢kzp对本函数的大力支持！
    private boolean checkMansus() {
        List<IFluidTank> fluidInputInventory = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        long long1 = 0L;
        long long2 = 0L;
        long long3 = 0L;
        for (IFluidTank iFluidTank : fluidInputInventory) {
            if (BLACK_MANSUS.isFluidEqual(iFluidTank.getFluid())) {
                long1 += iFluidTank.getFluidAmount();
            }
            if (WHITE_MANSUS.isFluidEqual(iFluidTank.getFluid())) {
                long2 += iFluidTank.getFluidAmount();
            }
            if (STARRY_MANSUS.isFluidEqual(iFluidTank.getFluid())) {
                long3 += iFluidTank.getFluidAmount();
            }
        }
        return long1 > overallBlackAmount && long2 > overallWhiteAmount && long3 > overallStarryAmount;
    }

    //因子函数，不同case给不同因子
    private List<Integer> readNodeInfo() {

        int parallelAmount = 1;
        int energyAmount = 0;
        int progressTimeAmount = 0;
        int blackAmount = 0;
        int whiteAmount = 0;
        int starryAmount = 0;
        List<Integer> list = new ArrayList<>();

        for (var i = 0; i < this.getInputInventory().getSlots(); ++i) {
            if (!this.inputInventory.getStackInSlot(i).isEmpty()) {
                ItemStack stack = this.getInputInventory().getStackInSlot(i);
                if (stack.getItem() == PollutionMetaItems.PACKAGED_AURA_NODE.getMetaItem() && stack.getMetadata() == PollutionMetaItems.PACKAGED_AURA_NODE.metaValue) {
                    switch (Objects.requireNonNull(this.inputInventory.getStackInSlot(i).getTagCompound()).getString("NodeType")) {
                        case "Ominous":
                            parallelAmount += 1;
                            blackAmount += 1;
                            whiteAmount += 1;
                        case "Pure":
                            blackAmount -= 1;
                            whiteAmount -= 1;
                        case "Voracious":
                            parallelAmount += 2;
                            energyAmount += 1;
                            progressTimeAmount += 1;
                            starryAmount += 1;
                        case "Concussive":
                            energyAmount += 1;
                            progressTimeAmount += 1;
                            blackAmount += 1;
                            whiteAmount += 1;
                    }
                    switch (Objects.requireNonNull(this.inputInventory.getStackInSlot(i).getTagCompound()).getString("NodeTire")) {
                        case "Bright":
                            energyAmount += 1;
                            progressTimeAmount += 1;
                            blackAmount -= 1;
                            whiteAmount -= 1;
                        case "Withering":
                            blackAmount += 2;
                            whiteAmount += 2;
                            starryAmount += 1;
                        case "Pale":
                            blackAmount += 1;
                            whiteAmount += 1;
                    }
                    if (this.inputInventory.getStackInSlot(i).getTagCompound().getInteger("EssenceEntropy") >= 200) {
                        blackAmount += 1;
                        whiteAmount += 1;
                    }
                    if (this.inputInventory.getStackInSlot(i).getTagCompound().getInteger("EssenceOrder") >= 300) {
                        blackAmount -= 1;
                        whiteAmount -= 1;
                    }
                    if (this.inputInventory.getStackInSlot(i).getTagCompound().getInteger("EssenceFire") >= 300) {
                        energyAmount += 1;
                    }
                    if (this.inputInventory.getStackInSlot(i).getTagCompound().getInteger("EssenceOrder") >= 300 && this.inputInventory.getStackInSlot(i).getTagCompound().getInteger("EssenceFire") >= 300) {
                        parallelAmount += 1;
                    }
                    if (this.inputInventory.getStackInSlot(i).getTagCompound().getInteger("EssenceWater") >= 300) {
                        progressTimeAmount += 1;
                    }
                }
                if (blackAmount < 0) {
                    blackAmount = 0;
                }
                if (whiteAmount < 0) {
                    whiteAmount = 0;
                }
            }
        }
        list.add(parallelAmount);
        list.add(energyAmount);
        list.add(progressTimeAmount);
        list.add(blackAmount);
        list.add(whiteAmount);
        list.add(starryAmount);
        return list;
    }

    //感谢小伞对本函数的大力支持！
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ABA              ", "              BDB              ", "              ABA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "             AADAA             ", "             DDEDD             ", "             AADAA             ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "            AABEBAA            ", "            DDEDEDD            ", "            AABEBAA            ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "           AAB D BAA           ", "           DDED DEDD           ", "           AAB D BAA           ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "          AAB     BAA          ", "          DDED   DEDD          ", "          AAB     BAA          ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "         AAB       BAA         ", "         DDED     DEDD         ", "         AAB       BAA         ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "        AAB         BAA        ", "        DDED       DEDD        ", "        AAB         BAA        ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "       AAB           BAA       ", "       DDED         DEDD       ", "       AAB           BAA       ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "         H           H         ", "                               ", "                               ", "                               ", "      AAB             BAA      ", "      DDED           DEDD      ", "      AAB             BAA      ", "                               ", "                               ", "                               ", "         H           H         ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "         F           F         ", "        HEG         GEH        ", "         F           F         ", "                               ", "                               ", "     AAB               BAA     ", "     DDED             DEDD     ", "     AAB               BAA     ", "                               ", "                               ", "         F           F         ", "        HEG         GEH        ", "         F           F         ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "         G           G         ", "                               ", "                               ", "                               ", "    AAB                 BAA    ", "    DDED               DEDD    ", "    AAB                 BAA    ", "                               ", "                               ", "                               ", "         G           G         ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "   AAB                   BAA   ", "   DDED                 DEDD   ", "   AAB                   BAA   ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "  AAB                     BAA  ", "  DDED                   DEDD  ", "  AAB                     BAA  ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ")
                .aisle("                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", " AAB                       BAA ", " DDED                     DEDD ", " AAB                       BAA ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ")
                .aisle("              ABA              ", "             AADAA             ", "            AABEBAA            ", "           AAB D BAA           ", "          AAB     BAA          ", "         AAB       BAA         ", "        AAB         BAA        ", "       AAB           BAA       ", "      AAB             BAA      ", "     AAB               BAA     ", "    AAB                 BAA    ", "   AAB                   BAA   ", "  AAB                     BAA  ", " AAB                       BAA ", "AAB                         BAA", "BDED                       DEDB", "AAB                         BAA", " AAB                       BAA ", "  AAB                     BAA  ", "   AAB                   BAA   ", "    AAB                 BAA    ", "     AAB               BAA     ", "      AAB             BAA      ", "       AAB           BAA       ", "        AAB         BAA        ", "         AAB       BAA         ", "          AAB     BAA          ", "           AAB D BAA           ", "            AABEBAA            ", "             AADAA             ", "              ABA              ")
                .aisle("              BDB              ", "             DDEDD             ", "            DDEDEDD            ", "           DDED DEDD           ", "          DDED   DEDD          ", "         DDED     DEDD         ", "        DDED       DEDD        ", "       DDED         DEDD       ", "      DDED           DEDD      ", "     DDED             DEDD     ", "    DDED               DEDD    ", "   DDED                 DEDD   ", "  DDED                   DEDD  ", " DDED                     DEDD ", "BDED                       DEDB", "DED                         DED", "BDED                       DEDB", " DDED                     DEDD ", "  DDED                   DEDD  ", "   DDED                 DEDD   ", "    DDED               DEDD    ", "     DDED             DEDD     ", "      DDED           DEDD      ", "       DDED         DEDD       ", "        DDED       DEDD        ", "         DDED     DEDD         ", "          DDED   DEDD          ", "           DDED DEDD           ", "            DDEDEDD            ", "             DDEDD             ", "              BDB              ")
                .aisle("              ABA              ", "             AADAA             ", "            AABEBAA            ", "           AAB D BAA           ", "          AAB     BAA          ", "         AAB       BAA         ", "        AAB         BAA        ", "       AAB           BAA       ", "      AAB             BAA      ", "     AAB               BAA     ", "    AAB                 BAA    ", "   AAB                   BAA   ", "  AAB                     BAA  ", " AAB                       BAA ", "AAB                         BAA", "BDED                       DEDB", "AAB                         BAA", " AAB                       BAA ", "  AAB                     BAA  ", "   AAB                   BAA   ", "    AAB                 BAA    ", "     AAB               BAA     ", "      AAB             BAA      ", "       AAB           BAA       ", "        AAB         BAA        ", "         AAB       BAA         ", "          AAB     BAA          ", "           AAB D BAA           ", "            AABEBAA            ", "             AADAA             ", "              ABA              ")
                .aisle("                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", " AAB                       BAA ", " DDED                     DEDD ", " AAB                       BAA ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ")
                .aisle("                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "  AAB                     BAA  ", "  DDED                   DEDD  ", "  AAB                     BAA  ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "   AAB                   BAA   ", "   DDED                 DEDD   ", "   AAB                   BAA   ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "         G           G         ", "                               ", "                               ", "                               ", "    AAB                 BAA    ", "    DDED               DEDD    ", "    AAB                 BAA    ", "                               ", "                               ", "                               ", "         G           G         ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "         F           F         ", "        HEG         GEH        ", "         F           F         ", "                               ", "                               ", "     AAB               BAA     ", "     DDED             DEDD     ", "     AAB               BAA     ", "                               ", "                               ", "         F           F         ", "        HEG         GEH        ", "         F           F         ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "         H           H         ", "                               ", "                               ", "                               ", "      AAB             BAA      ", "      DDED           DEDD      ", "      AAB             BAA      ", "                               ", "                               ", "                               ", "         H           H         ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "                               ", "       AAB           BAA       ", "       DDED         DEDD       ", "       AAB           BAA       ", "                               ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "                               ", "        AAB         BAA        ", "        DDED       DEDD        ", "        AAB         BAA        ", "                               ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "                               ", "         AAB       BAA         ", "         DDED     DEDD         ", "         AAB       BAA         ", "                               ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "               D               ", "          AAB     BAA          ", "          DDED   DEDD          ", "          AAB     BAA          ", "               D               ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "              BEB              ", "           AAB D BAA           ", "           DDED DEDD           ", "           AAB D BAA           ", "              BEB              ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "              ADA              ", "            AABEBAA            ", "            DDEDEDD            ", "            AABEBAA            ", "              ADA              ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ADA              ", "             AADAA             ", "             DDEDD             ", "             AADAA             ", "              ADA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")
                .aisle("                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "              ABA              ", "              BSB              ", "              ABA              ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ", "                               ")

                .where('S', selfPredicate())
                .where(' ', any())
                .where('A', states(getCasingState()))
                .where('B', states(getCasingState2()))
                .where('D', states(getCasingState3()))
                .where('E', states(getCasingState4()))
                .where('F', states(getCasingState3())
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(5).setPreviewCount(5))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                )
                .where('G', states(getCasingState3())
                        .or(metaTileEntities(Arrays.stream(MetaTileEntities.ENERGY_INPUT_HATCH)
                                .filter((mte) -> mte != null && this.tier <= mte.getTier())
                                .toArray(MetaTileEntity[]::new)).setMinGlobalLimited(1).setPreviewCount(16)))
                .where('H', metaTileEntities(MetaTileEntities.ITEM_IMPORT_BUS[GTValues.ULV]))
                .build();
    }

    private IBlockState getCasingState3() {
        if (this.tier == 6) {
            return PollutionMetaBlocks.HYPER.getState(POHyper.HyperType.HYPER_1_CASING);
        } else
            return this.tier == 7 ? PollutionMetaBlocks.HYPER.getState(POHyper.HyperType.HYPER_2_CASING) : PollutionMetaBlocks.HYPER.getState(POHyper.HyperType.HYPER_3_CASING);
    }

    private IBlockState getCasingState4() {
        if (this.tier == 6) {
            return MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL);
        } else return MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
    }

    //无需能源仓
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (this.tier == 6) {
            return POTextures.HYPER_1;
        } else return this.tier == 7 ? POTextures.HYPER_2 : POTextures.HYPER_3;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.FUSION_REACTOR_OVERLAY;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setLong("heat", this.heat);
        this.energyInternal = this.energyContainer.getEnergyStored();
        data.setLong("energyInternal", this.energyInternal);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.heat = data.getLong("heat");
        this.energyInternal = data.getLong("energyInternal");
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom((textList, syncer) -> {
                    textList.add(KeyUtil.lang( "区块超稳: %s    漫宿供给: %s", syncer.syncBoolean(isCleanVis()), syncer.syncBoolean(isMansusSupplied)));
                    textList.add(KeyUtil.lang( "并行因子: %s    减耗因子: %s    加速因子: %s", syncer.syncInt(overallParallelAmount), syncer.syncInt(overallEnergyAmount), syncer.syncInt(overallProgressTimeAmount)));
                    textList.add(KeyUtil.lang( "玄牝因子: %s    素牡因子: %s    天璇因子: %s", syncer.syncInt(overallBlackAmount), syncer.syncInt(overallWhiteAmount), syncer.syncInt(overallStarryAmount)));
                    textList.add(KeyUtil.lang( "温度缓存: %s", syncer.syncLong(heat)));
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }
    //tooltip
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.5"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.6"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.7"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.8"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.9"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.10"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.11"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.12"));
        tooltip.add(I18n.format("pollution.machine.node.fusion.reactor.tooltip.13"));

    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityNodeFusionReactor(this.metaTileEntityId, this.tier);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        IFastRenderMetaTileEntity.super.renderMetaTileEntity(x, y, z, partialTicks);

        if (isStructureFormed() && GTQTCoreConfig.OBJRenderSwitch.EnableObj && POConfig.OBJRenderSwitch.EnableObjNodeFusionReactor) {
            final int xDir = this.getFrontFacing().getOpposite().getXOffset();
            final int zDir = this.getFrontFacing().getOpposite().getZOffset();
            //机器开启才会进行渲染
            //这是一些opengl的操作,GlStateManager是mc自身封装的一部分方法  前四条详情请看 https://turou.fun/minecraft/legacy-render-tutor/
            //opengl方法一般需要成对出现，实际上他是一个状态机，改装状态后要还原  一般情况按照我这些去写就OK
            GlStateManager.pushAttrib(); //保存变换前的位置和角度
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            FMLClientHandler.instance().getClient().getTextureManager().bindTexture(ObjModels.sun_pic); //自带的材质绑定 需要传递一个ResourceLocation
            GlStateManager.translate(x, y, z);//translate是移动方法 这个移动到xyz是默认的 不要动
            GlStateManager.translate(xDir * 15 + 0.5, 0.5, zDir * 15 + 0.5);//translate是移动方法 这个移动到xyz是默认的 不要动


            float angle = (System.currentTimeMillis() % 3600) / 10.0f; //我写的随时间变化旋转的角度
            //GlStateManager.rotate(90, 0F, 1F, 0F);//rotate是旋转模型的方法  DNA的初始位置不太对 我旋转了一下   四个参数为：旋转角度，xyz轴，可以控制模型围绕哪个轴旋转
            GlStateManager.rotate(angle, 0F, 1F, 0F);//我让dna围绕z轴旋转，角度是实时变化的


            GlStateManager.scale(0.1, 0.1, 0.1);
            // ObjModels.Tree_Model.renderAllWithMtl(); //这个是模型加载器的渲染方法  这是带MTL的加载方式
            ObjModels.sun.renderAll(); //这个是模型加载器的渲染方法  这是不带MTL的加载方式
            GlStateManager.popMatrix();//读取变换前的位置和角度(恢复原状) 下面都是还原状态机的语句
            GlStateManager.enableLighting();
            GlStateManager.popAttrib();
            GlStateManager.enableCull();
        }

    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        //这个影响模型的可视范围，正常方块都是 1 1 1，长宽高各为1，当这个方块离线玩家视线后，obj模型渲染会停止，所以可以适当放大这个大小能让模型有更多角度的可视
        return new AxisAlignedBB(getPos().add(-5, -5, -5), getPos().add(5, 5, 5));
    }


    //渲染模型的位置

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true, true);
    }

    //聚变逻辑：并行、耗电、聚变速度根据节点属性运算，额外附加超净检测
    protected class NodeFusionReactorRecipeLogic extends POMultiblockCleanVisRecipeLogic {
        public NodeFusionReactorRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        public @NotNull NBTTagCompound serializeNBT() {
            NBTTagCompound tag = super.serializeNBT();
            tag.setLong("Heat", MetaTileEntityNodeFusionReactor.this.heat);
            return tag;
        }

        //照抄ceu
        public void deserializeNBT(@NotNull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            MetaTileEntityNodeFusionReactor.this.heat = compound.getLong("Heat");
        }


        public long getMaxVoltage() {
            return Math.min(GTValues.V[MetaTileEntityNodeFusionReactor.this.tier], super.getMaxVoltage());
        }

        //照抄ceu
        @Override
        protected void modifyOverclockPre(@NotNull OCParams ocParams, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPre(ocParams, storage);

            // Limit the number of OCs to the difference in fusion reactor MK.
            // I.e., a MK2 reactor can overclock a MK1 recipe once, and a
            // MK3 reactor can overclock a MK2 recipe once, or a MK1 recipe twice.
            long euToStart = storage.get(FusionEUToStartProperty.getInstance(), 0L);
            int fusionTier = FusionEUToStartProperty.getFusionTier(euToStart);
            if (fusionTier != 0) fusionTier = MetaTileEntityNodeFusionReactor.this.tier - fusionTier;
            ocParams.setOcAmount(Math.min(fusionTier, ocParams.ocAmount()));
        }

        //没有照抄的
        @Override
        public int getParallelLimit() {
            if (this.getRecipeMap() == RecipeMaps.FUSION_RECIPES) {
                return overallParallelAmount * 2;
            } else if (this.getRecipeMap() == PORecipeMaps.NODE_MAGIC_FUSION_RECIPES) {
                return overallParallelAmount;
            } else return 1;
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            super.setMaxProgress((int) (maxProgress * (1 - 0.02 * overallProgressTimeAmount)));
        }

        @Override
        protected boolean drawEnergy(long recipeEUt, boolean simulate) {
            long finalRecipeEUt = (long) (recipeEUt * (1 - Math.min(0.5, 0.01 * overallEnergyAmount)));
            long resultEnergy = this.getEnergyStored() - finalRecipeEUt;
            if (resultEnergy >= 0L && resultEnergy <= this.getEnergyCapacity()) {
                if (!simulate) {
                    this.getEnergyContainer().changeEnergy(-finalRecipeEUt);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void updateRecipeProgress() {
            this.drawEnergy(this.recipeEUt, false);
            //不清洁无法工作
            if (!isCleanVis()) {
                return;
            }
            if (++this.progressTime > this.maxProgressTime) {
                this.completeRecipe();
            }
        }

        //照抄ceu的热能逻辑，分两种配方
        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            if (this.getRecipeMap() == PORecipeMaps.NODE_MAGIC_FUSION_RECIPES) {
                //检测超稳和漫宿供应
                if (isCleanVis() && isMansusSupplied) {
                    //和下面一样
                    if (recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) > MetaTileEntityNodeFusionReactor.this.energyContainer.getEnergyCapacity()) {
                        return false;
                    } else {
                        long heatDiff = recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) - MetaTileEntityNodeFusionReactor.this.heat;
                        if (heatDiff <= 0L) {
                            return true;
                        } else if (MetaTileEntityNodeFusionReactor.this.energyContainer.getEnergyStored() < heatDiff) {
                            return false;
                        } else {
                            MetaTileEntityNodeFusionReactor.this.energyContainer.removeEnergy(heatDiff);
                            MetaTileEntityNodeFusionReactor.this.heat = heatDiff;
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            } else if (this.getRecipeMap() == RecipeMaps.FUSION_RECIPES) {
                if (recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) > MetaTileEntityNodeFusionReactor.this.energyContainer.getEnergyCapacity()) {
                    return false;
                } else {
                    long heatDiff = recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) - MetaTileEntityNodeFusionReactor.this.heat;
                    if (heatDiff <= 0L) {
                        return true;
                    } else if (MetaTileEntityNodeFusionReactor.this.energyContainer.getEnergyStored() < heatDiff) {
                        return false;
                    } else {
                        MetaTileEntityNodeFusionReactor.this.energyContainer.removeEnergy(heatDiff);
                        MetaTileEntityNodeFusionReactor.this.heat = heatDiff;
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
    }
}