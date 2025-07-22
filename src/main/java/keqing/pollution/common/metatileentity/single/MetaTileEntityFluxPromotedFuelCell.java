package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.capability.impl.FuelRecipeLogic;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import gregtech.client.utils.PipelineUtil;
import keqing.pollution.POConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.Math.pow;


public class MetaTileEntityFluxPromotedFuelCell extends WorkableTieredMetaTileEntity {
    public boolean checkFluxCondition = false;
    public boolean isActive = false;
    public float efficiencyMultiplier;
    public boolean FluxThreshold;
    public float FluxCeiling = 0;
    float desiredFlux = 0;
    float currentFlux = 0;

    public MetaTileEntityFluxPromotedFuelCell(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer, int tier, Function<Integer, Integer> tankScalingFunction) {
        super(metaTileEntityId, recipeMap, renderer, tier, tankScalingFunction, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity GregTechTileEntity) {
        return new MetaTileEntityFluxPromotedFuelCell(metaTileEntityId, recipeMap, renderer, getTier(), getTankScalingFunction());
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            desiredFlux = (float) (POConfig.PollutionSystemSwitch.FluxPromotedGeneratorFluxPerTick * 4 + 0.05 * 4 * (getTier() - 1));
            currentFlux = AuraHandler.getFlux(getWorld(), getPos());
            FluxCeiling = (float) (60 + 5 * pow(4, getTier()));
            efficiencyMultiplier = 0;
            checkFluxCondition = desiredFlux <= currentFlux;
            FluxThreshold = 10 <= currentFlux;
            isActive = checkFluxCondition && FluxThreshold;
            if (this.isActive()) {

                this.workable.setWorkingEnabled(true);
                if (!checkFluxCondition || currentFlux < 10) {
                    efficiencyMultiplier = 0;
                } else if (currentFlux >= 10 && currentFlux < 50) {
                    efficiencyMultiplier = (5F - 256F / (1.6F * currentFlux + 48F));
                } else if (currentFlux >= 50 && currentFlux <= FluxCeiling) {
                    efficiencyMultiplier = 3F;
                } else {
                    doExplosion1(1);
                }
                AuraHelper.drainFlux(getWorld(), getPos(), desiredFlux, false);
                this.energyContainer.addEnergy((long) (efficiencyMultiplier * GTValues.V[getTier()]));
            } else {

                this.workable.setWorkingEnabled(false);


            }
        }
    }


    public void doExplosion1(float explosionPower) {
        this.setExploded();
        this.getWorld().setBlockToAir(this.getPos());
        for (int i = -3; i < 3; i++)
            for (int j = -3; j < 3; j++)
                for (int k = -3; k < 3; k++)
                    this.getWorld().createExplosion(null, (double) this.getPos().getX() + 0.5 + 5 * i, (double) this.getPos().getY() + 0.5 + 5 * k, (double) this.getPos().getZ() + 0.5 + 5 * j, explosionPower, true);
    }

    @Override
    public boolean isActive() {
        return this.workable.isActive() && this.workable.isWorkingEnabled() && isActive;
    }


    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    public long getMaxInputOutputAmperage() {
        return 4L;
    }

    public boolean isEnergyEmitter() {
        return true;
    }

    public long getEnergyCapacity() {
        return (long) (8192 * pow(4, getTier()));
    }

    protected void reinitializeEnergyContainer() {
        super.reinitializeEnergyContainer();
        long tierVoltage = GTValues.V[this.getTier()];
        super.energyContainer = EnergyContainerHandler.emitterContainer(this, getEnergyCapacity(), tierVoltage, this.getMaxInputOutputAmperage());

    }

    protected RecipeLogicEnergy createWorkable(RecipeMap<?> recipeMap) {
        return new FuelRecipeLogic(this, recipeMap, () -> {
            return this.energyContainer;
        });
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder createGuiTemplate = ModularUI.defaultBuilder();
        {
            RecipeMap<?> workableRecipeMap = this.workable.getRecipeMap();
            int yOffset = 0;
            if (workableRecipeMap.getMaxInputs() >= 6 || workableRecipeMap.getMaxFluidInputs() >= 6 || workableRecipeMap.getMaxOutputs() >= 6 || workableRecipeMap.getMaxFluidOutputs() >= 6) {
                yOffset = 9;
            }

            AbstractRecipeLogic var10001;
            ModularUI.Builder builder;
            if (this.handlesRecipeOutputs) {
                var10001 = this.workable;
                Objects.requireNonNull(var10001);
                builder = workableRecipeMap.createUITemplate(var10001::getProgressPercent, this.importItems, this.exportItems, this.importFluids, this.exportFluids, yOffset);
            } else {
                var10001 = this.workable;
                Objects.requireNonNull(var10001);
                builder = workableRecipeMap.createUITemplateNoOutputs(var10001::getProgressPercent, this.importItems, this.exportItems, this.importFluids, this.exportFluids, yOffset);
            }

            builder.widget(new LabelWidget(6, 6, this.getMetaFullName())).bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, yOffset);
            builder.dynamicLabel(7, 50, () -> {
                return "Flux Ceiling Durable: " + (60 + 5 * pow(4, getTier()));
            }, 2302755);
            builder.dynamicLabel(7, 60, () -> {
                return "Current Flux: " + (AuraHelper.getFlux(getWorld(), getPos()));
            }, 2302755);
            builder.dynamicLabel(7, 70, () -> {
                return "Flux per tick: " + POConfig.PollutionSystemSwitch.FluxPromotedGeneratorFluxPerTick * pow(4, getTier() - 1);
            }, 2302755);
            return builder.build(this.getHolder(), entityPlayer);
        }
    }

    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        String key = this.metaTileEntityId.getPath().split("\\.")[0];
        String mainKey = String.format("gregtech.machine.%s.tooltip", key);
        if (I18n.hasKey(mainKey)) {
            tooltip.add(1, I18n.format(mainKey));
        }
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", this.energyContainer.getEnergyCapacity()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
        getBaseRenderer().render(renderState, translation, colouredPipeline);
        getRenderer().renderSided(EnumFacing.UP, renderState, translation, PipelineUtil.color(pipeline, GTValues.VC[getTier()]));
        Textures.ENERGY_OUT.renderSided(this.getFrontFacing(), renderState, translation, PipelineUtil.color(pipeline, GTValues.VC[getTier()]));

    }

    @SideOnly(Side.CLIENT)
    private ICubeRenderer getRenderer() {
        return Textures.POWER_SUBSTATION_OVERLAY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return isActive ? Textures.MAGIC_ENERGY_ABSORBER_ACTIVE : Textures.MAGIC_ENERGY_ABSORBER;
    }

}