package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTTransferUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import meowmel.pollution.api.capability.ipml.ManaHandlerList;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.utils.POUtils;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POBotBlock;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POManaPlate;
import meowmel.gtqtcore.api.blocks.impl.WrappedIntTired;
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

import java.util.*;

import static meowmel.pollution.api.predicate.TiredTraceabilityPredicate.CP_BEAM_CORE;

public class MetaTileEntityBotGasCollector extends MetaTileEntityBaseWithControl {

    //配方执行次数
    int times = 0;
    //核心等级
    int beamLevel;
    //流体检测
    boolean mansusCheck = false;
    boolean essenceCheck = false;
    //列表对应的大气材料
    Material result;
    //最终收集速率
    int finalCollectionSpeed;
    //要素消耗
    int essenceConsumptionSpeed;
    //魔力消耗速度
    int manaConsumptionSpeed;
    
    int tier;
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("tier", this.tier);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.tier = data.getInteger("tier");
    }
    //液态源质对应的气体列表
    HashMap<gregtech.api.unification.material.Material, gregtech.api.unification.material.Material> gasMap = new HashMap<>() {
        {
            put(PollutionMaterials.InfusedAir, Materials.Air);
            put(PollutionMaterials.InfusedFire, Materials.NetherAir);
            put(PollutionMaterials.InfusedAlien, Materials.EnderAir);
            //put(PollutionMaterials.infused_magic, GTQTMaterials.MarsAir);
            //put(PollutionMaterials.infused_taint, GTQTMaterials.UnderAir);
        }
    };

    public MetaTileEntityBotGasCollector(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_5_CASING);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.BAMINATED_GLASS);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_2);
    }

    private static IBlockState getCasingState4() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.KQGold).getBlock(PollutionMaterials.KQGold);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("pollution.machine.bot_gas_collector_beamLevel", this.beamLevel).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.bot_gas_collector_essenceConsumptionSpeed", this.essenceConsumptionSpeed)).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.bot_gas_collector_manaConsumptionSpeed", this.manaConsumptionSpeed)).setStyle((new Style()).setColor(TextFormatting.RED)));
        textList.add((new TextComponentTranslation("pollution.machine.bot_gas_collector_finalCollectionSpeed", this.finalCollectionSpeed)).setStyle((new Style()).setColor(TextFormatting.RED)));
    }

    //tooltip
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.5"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.6"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.7"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.8"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.9"));
        tooltip.add(I18n.format("pollution.machine.bot_gas_collector.tooltip.10"));
    }

    ManaHandlerList manaHandler;

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        manaHandler = new ManaHandlerList(getAbilities(POMultiblockAbility.MANA_INPUT_POOL));
    }

    @Override
    protected void resetTileAbilities() {
        super.resetTileAbilities();
        manaHandler = new ManaHandlerList(new ArrayList<>());
    }


    private boolean consumeMana(long amount,boolean simulate) {
        return this.manaHandler.consumeMana(amount,simulate);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object beamLevel = context.get("BEAMTieredStats");
        this.beamLevel = POUtils.getOrDefault(() -> beamLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) beamLevel).getIntTier(),
                0);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityBotGasCollector(this.metaTileEntityId);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.TERRA_5_CASING;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    protected void updateFormedValid() {
        if (!this.isActive()) {
            setActive(true);
        }
        essenceConsumptionSpeed = (int) ((14 - beamLevel) * 0.5 * (1 - 0.05 * tier));
        manaConsumptionSpeed = (int) (4 * Math.pow(2, (Math.max(0, tier - 2))));
        finalCollectionSpeed = manaConsumptionSpeed * 100;
        List<IFluidTank> fluidInputInventory = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        FluidStack WHITE_MANSUS = PollutionMaterials.whitemansus.getFluid(essenceConsumptionSpeed * 10);

        if (this.isWorkingEnabled() && !this.getWorld().isRemote) {
            if (WHITE_MANSUS.isFluidStackIdentical(this.inputFluidInventory.drain(WHITE_MANSUS, false))) {
                mansusCheck = true;
                if (times == 100) {
                    this.inputFluidInventory.drain(WHITE_MANSUS, true);
                }
            } else {
                mansusCheck = false;
            }

            ArrayList<Material> temp = new ArrayList<>(this.gasMap.keySet());
            for (IFluidTank iFluidTank : fluidInputInventory) {
                for (int j = 0; j < gasMap.size(); j++) {
                    if (temp.get(j).getFluid(essenceConsumptionSpeed).equals(iFluidTank.getFluid())) {
                        result = gasMap.get(temp.get(j));
                        if (mansusCheck) {
                            this.inputFluidInventory.drain(temp.get(j).getFluid(essenceConsumptionSpeed), true);
                            essenceCheck = true;
                            break;
                        }
                    }
                }
                if (essenceCheck) {
                    break;
                }
            }

            if (essenceCheck && mansusCheck) {
                if (this.manaHandler.consumeMana(manaConsumptionSpeed,true)) {
                    this.manaHandler.consumeMana(manaConsumptionSpeed,false);
                    FluidStack fluid = new FluidStack(result.getFluid(), finalCollectionSpeed);
                    GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(fluid));
                    times++;
                    essenceCheck = false;
                }
                if (times > 100) {
                    times = 0;
                }
            }
        }
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("E E", "E E", "DDD", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA")
                .aisle("   ", "   ", "DDD", "AAA", "ABA", "ABA", "ABA", "AAA", "AAA")
                .aisle("E E", "E E", "DDD", "ASA", "ACA", "ACA", "ACA", "AAA", "AAA")
                .where('A', states(getCasingState()).setMinGlobalLimited(15)
                        .or(abilities(POMultiblockAbility.MANA_INPUT_POOL).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(2).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1)))
                .where('B', CP_BEAM_CORE.get())
                .where('C', states(getCasingState2()))
                .where('D', states(getCasingState3()))
                .where('E', states(getCasingState4()))
                .where(' ', any())
                .where('S', selfPredicate())
                .build();
    }
}
