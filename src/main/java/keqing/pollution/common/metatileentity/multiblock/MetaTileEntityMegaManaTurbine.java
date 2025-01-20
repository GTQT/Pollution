package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.MultiblockFuelRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.FuelMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.logic.OverclockingLogic;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.LivingWoodVariant;
import vazkii.botania.common.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

import static com.cleanroommc.modularui.utils.MathUtils.min;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;

//巨型魔力轮机，计划是跑MANA_To_EU的配方，提供少量某些材料可以增大输出上限，持续运作有并行加成，白板工作上限为UV，64A IV发电
//目前计划是可以提供的材料组合包括黑白漫宿（要求几把气线，上限拉到UHV 1A，对应64A LuV），液态超次元秘银和刻金（要求白垩和持续运作的锻炉，上限拉到UEV 2A，对应128A ZPM）
//液态约束，感知和生命源质（要求持续运作的狱火熔炉和仪式机，上限拉到UIV 4A，对应256A UV)，之后它就该下岗了?
//暂时不知道po的材料等级分配，目前看来是
//LV——TC6各种结晶，六矿（少量），神秘锭（少量），交错天然材料（少量，只能手挖和注魔），魔力谐振器，灵气谐振器（这俩有点用的太多了），紫水晶，猫眼石，琥珀等宝石，悖论物质
//MV——（少量）漫宿钢，六种杂鱼金属，魔力催化剂基底
//HV——聚灵阵，魔力钢，（大量）量产六矿，神秘锭
//EV——黑土贤者石，（少量）泰拉钢，源质钢，（大量）六种杂鱼金属，漫宿钢，魔力催化剂基底
//IV——（少量）黑白漫宿，超次元秘银刻金，感知锭，(大量）魔力钢，感知锭（少量）
//Luv——白垩贤者石，锻炉量产（黑白漫宿），约束锭（少量）
//ZPM——锻炉量产（超次元秘银刻金，交错天然材料，泰拉钢源质钢，考虑到产能供给，应该要zpm才能产的多），感知锭（大量）
//UV——赤成贤者石，约束锭（大量）
//UHV——
//UEV——黄金贤者石
//UIV——
//UMV——贤者之石？
//MAX——毕业物品，
//可能需要魔法侧的和贤者石头等级挂钩的部件
//生产机器的产能应该比纯科技侧机器高一个等级，毕竟做这玩意需要额外的成本
//想拉进来的已有材料：匠魂元金聚合矩阵暗影玛玉灵阿迪特蕴魔结晶飞龙神龙混沌，原版龙蛋龙头紫颂果末地烛，Biome的各种宝石，TC的灵宝炼化虚空原始珍珠
//龙研龙锭觉醒龙双足飞龙和各种核心，血魔法感知约束生命源质，植物魔法奥利哈钢暗影光明盖亚魂盖亚钢自然魔力龙石精灵尘魔源布蕴魔丝等（太多了）
//交错焦油心焦油侍从三种元素宝石和生命水晶鬼火各类掉落物和食物等，
//在lib里的就直接加，不在的就写个gt版本共享矿词
//计划机器（不知道能不能写得出来）
//产能机器：
//bot：彼方兰的营养均衡套餐，启命英新式核电，魔力轮机
//血魔法：苦难井和羽刀折磨王
//加工机器
//bot：大精灵门，精灵工坊，大魔力池，盖亚机
//血魔法：坠星位标，数字化血祭坛，意志生长机，大狱火熔炉，仪式机
//懒得写了，能不能写出来都不好说，结构就丢到群里下单，看看有没有群友想做建筑的
//优先级：写机器>写配方材料
public class MetaTileEntityMegaManaTurbine extends FuelMultiblockController {
    public final int tier;
    public final ICubeRenderer casingRenderer;
    public final boolean hasMufflerHatch;
    public final ICubeRenderer frontOverlay;
    public long CatalystAllowedMaxCapacity;
    public int parallel;
    private Integer CoilLevel;

    public MetaTileEntityMegaManaTurbine(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int tier, ICubeRenderer casingRenderer, boolean hasMufflerHatch, ICubeRenderer frontOverlay) {
        super(metaTileEntityId, recipeMap, tier);
        this.casingRenderer = casingRenderer;
        this.hasMufflerHatch = hasMufflerHatch;
        this.frontOverlay = frontOverlay;
        this.tier = tier;
        this.recipeMapWorkable = new MegaManaTurbineWorkable(this);
    }

    private static IBlockState getInnerFilling() {
        return ModBlocks.bifrostPerm.getDefaultState();
    }

    private static IBlockState getCasingFrontFacing() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_5);
    }

    private static IBlockState getCasingBackFacing() {
        return MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2);
    }

    private static IBlockState getCasingInner() {
        return ModBlocks.dreamwood.getDefaultState().withProperty(BotaniaStateProps.LIVINGWOOD_VARIANT, LivingWoodVariant.GLIMMERING);
    }

    private static IBlockState getCasingFusionCoil() {
        return MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
    }

    //创建方块实体
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMegaManaTurbine(this.metaTileEntityId, this.recipeMap, this.tier, this.casingRenderer, this.hasMufflerHatch, this.frontOverlay);

    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("AAAAAAA", "ABBBBBA", "ABCCCBA", "ABCJCBA", "ABCCCBA", "ABBBBBA", "AAAAAAA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("ABBBBBA", "BEEEEEB", "BEFGFEB", "BEGDGEB", "BEFGFEB", "BEEEEEB", "ABBBBBA")
                .aisle("AAAAAAA", "ABBBBBA", "ABHHHBA", "ABHIHBA", "ABHHHBA", "ABBBBBA", "AAAAAAA")
                .where('A', states(getCasingState_Frame()))
                .where('B', states(getOuterFilling()))
                .where('C', states(getCasingBackFacing()))
                .where('D', states(getCasingFusionCoil()))
                .where('E', states(getInnerFilling()))
                .where('F', states(getCasingInner()))
                .where('G', CP_COIL_CASING.get())
                .where('H', states(getCasingFrontFacing())
                        .or(this.autoAbilities(false, true, false, false, true, true, false)))
                .where('I', selfPredicate())
                .where('J', states(getCasingFusionCoil())
                        .or(abilities(MultiblockAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.OUTPUT_LASER).setMaxGlobalLimited(1).setPreviewCount(1)))
                .build();
    }

    public IBlockState getOuterFilling() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
    }

    public IBlockState getCasingState_Frame() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(TextComponentUtil.stringWithColor(TextFormatting.AQUA, "最大输出功率:" + CatalystAllowedMaxCapacity));
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object CoilLevel = context.get("COILTieredStats");
        this.CoilLevel = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) CoilLevel).getIntTier(),
                0);
    }

    //客户端渲染
    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return POTextures.MANA_5;
    }

    @SideOnly(Side.CLIENT)
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return this.frontOverlay;
    }

    //确定机器等级
    public int getTier() {
        return this.tier;
    }

    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();

    }

    //控制是否销毁输出
    public boolean canVoidRecipeItemOutputs() {
        return true;
    }

    public boolean canVoidRecipeFluidOutputs() {
        return true;
    }

    protected boolean shouldShowVoidingModeButton() {
        return true;
    }

    //控制最大输出电压

    protected void initializeAbilities() {
        super.initializeAbilities();
        List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.OUTPUT_ENERGY));
        energyContainer.addAll(this.getAbilities(MultiblockAbility.OUTPUT_LASER));
        this.energyContainer = new EnergyContainerList(energyContainer);
    }

    protected class MegaManaTurbineWorkable extends MultiblockFuelRecipeLogic {
        public static final FluidStack BlackManSusStack = PollutionMaterials.blackmansus.getFluid(32);
        public static final FluidStack WhiteManSusStack = PollutionMaterials.whitemansus.getFluid(32);
        public static final FluidStack KeQinGoldStack = PollutionMaterials.keqinggold.getFluid(32);
        public static final FluidStack ChaoCiYuanYinStack = PollutionMaterials.hyperdimensional_silver.getFluid(32);
        public static final FluidStack GanZhiStack = PollutionMaterials.sentient_metal.getFluid(32);
        public static final FluidStack YueShuStack = PollutionMaterials.binding_metal.getFluid(32);
        private final MetaTileEntityMegaManaTurbine megaManaTurbine;

        public MegaManaTurbineWorkable(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
            this.megaManaTurbine = (MetaTileEntityMegaManaTurbine) tileEntity;
        }

        //判断催化剂等级，优先用高级催化剂
        protected int CatalystLevel() {
            IMultipleTankHandler inputTank = this.megaManaTurbine.getInputFluidInventory();
            boolean BlackManSusStackIsIn = inputTank.drain(BlackManSusStack, false) != null && BlackManSusStack.isFluidStackIdentical(inputTank.drain(BlackManSusStack, false));
            boolean WhiteManSusStackIsIn = inputTank.drain(WhiteManSusStack, false) != null && WhiteManSusStack.isFluidStackIdentical(inputTank.drain(WhiteManSusStack, false));
            boolean KeQinGoldStackIsIn = inputTank.drain(KeQinGoldStack, false) != null && KeQinGoldStack.isFluidStackIdentical(inputTank.drain(KeQinGoldStack, false));
            boolean ChaoCiYuanYinStackIsIn = inputTank.drain(ChaoCiYuanYinStack, false) != null && ChaoCiYuanYinStack.isFluidStackIdentical(inputTank.drain(ChaoCiYuanYinStack, false));
            boolean GanZhiStackIsIn = inputTank.drain(GanZhiStack, false) != null && GanZhiStack.isFluidStackIdentical(inputTank.drain(GanZhiStack, false));
            boolean YueShuStackIsIn = inputTank.drain(YueShuStack, false) != null && YueShuStack.isFluidStackIdentical(inputTank.drain(YueShuStack, false));

            if (GanZhiStackIsIn && YueShuStackIsIn) {
                return 3;
            } else if (KeQinGoldStackIsIn && ChaoCiYuanYinStackIsIn) {
                return 2;
            } else if (BlackManSusStackIsIn && WhiteManSusStackIsIn) {
                return 1;
            } else {
                return 0;
            }
        }

        //催化剂对应最大电压水平
        public long CatalystAllowedMaxCapacity() {

            switch (CatalystLevel()) {
                case 0 -> CatalystAllowedMaxCapacity = GTValues.V[8];
                case 1 -> CatalystAllowedMaxCapacity = GTValues.V[9];
                case 2 -> CatalystAllowedMaxCapacity = 2 * GTValues.V[10];
                case 3 -> CatalystAllowedMaxCapacity = GTValues.V[12];
                default -> CatalystAllowedMaxCapacity = 0;
            }
            return CatalystAllowedMaxCapacity;
        }

        //消耗催化剂
        protected void drainCatalyst() {
            if (this.totalContinuousRunningTime % 100L == 0L) {
                if (CatalystLevel() == 1)
                    this.megaManaTurbine.getInputFluidInventory().drain(BlackManSusStack, true);
                this.megaManaTurbine.getInputFluidInventory().drain(WhiteManSusStack, true);
            } else if (CatalystLevel() == 2) {
                this.megaManaTurbine.getInputFluidInventory().drain(KeQinGoldStack, true);
                this.megaManaTurbine.getInputFluidInventory().drain(ChaoCiYuanYinStack, true);
            } else if (CatalystLevel() == 3) {
                this.megaManaTurbine.getInputFluidInventory().drain(GanZhiStack, true);
                this.megaManaTurbine.getInputFluidInventory().drain(WhiteManSusStack, true);
            }
        }

        @Override
        protected void updateRecipeProgress() {
            if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true)) {
                this.drainCatalyst();
                this.drawEnergy(this.recipeEUt, false);
                if (++this.progressTime > this.maxProgressTime) {
                    this.completeRecipe();
                }
            }
        }

        @Override
        public long getMaxVoltage() {
            return min((int) CatalystAllowedMaxCapacity(), (int) super.getMaxVoltage());
        }

        @Override
        protected long getMaxParallelVoltage() {
            return this.getMaxVoltage();
        }

        //持续工作一段时间后达到输出最大值，所需时间取决于线圈等级
        public int parallelLimit() {

            int runningTime = (int) this.totalContinuousRunningTime;
            if (runningTime <= 60000 / (float) this.megaManaTurbine.CoilLevel) {
                parallel = 1 + Math.round((runningTime / (60000 / (float) this.megaManaTurbine.CoilLevel) * (32768 - 1)));
            } else {
                parallel = 32768;
            }
            return parallel;
        }

        @Override
        public int getParallelLimit() {
            return parallelLimit();
        }

        public long getMaximumOverclockVoltage() {
            return getMaxVoltage();
        }

        public boolean isAllowOverclocking() {
            return false;
        }

        protected int[] runOverclockingLogic(IRecipePropertyStorage propertyStorage, int recipeEUt, long maxVoltage, int duration, int amountOC) {
            return OverclockingLogic.standardOverclockingLogic(Math.abs(recipeEUt), maxVoltage, duration, amountOC, OverclockingLogic.PERFECT_OVERCLOCK_DURATION_DIVISOR, 4);
        }
    }


}