package keqing.pollution.common.metatileentity.single;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ManaGeneratorTileEntity extends SimpleGeneratorMetaTileEntity {


    public ManaGeneratorTileEntity(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, new RecipeMap<>("mana_gen_recipes", 0, 0, 0, 0, new SimpleRecipeBuilder(), true), Textures.COMBUSTION_GENERATOR_OVERLAY, tier, (n) -> n + 1);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ManaGeneratorTileEntity(metaTileEntityId, getTier());
    }

    public int manaCostPerTier(int tier) {
        return tier * 10;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return POTextures.MAGIC_VOLTAGE_CASINGS[getTier()];
    }

    @Override
    protected void reinitializeEnergyContainer() {
        super.reinitializeEnergyContainer();
        ((EnergyContainerHandler) energyContainer).setSideOutputCondition((facing) -> facing == frontFacing);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        tooltip.add(1, I18n.format("花花草草的力量！"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", this.energyContainer.getEnergyCapacity()));
        if (this.recipeMap.getMaxFluidInputs() > 0 || this.recipeMap.getMaxFluidOutputs() > 0) {
            tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", this.getTankScalingFunction().apply(this.getTier())));
        }

    }

    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        tooltip.add(I18n.format("gregtech.tool_action.soft_mallet.reset"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    public void reciveMana(int mana) {
        if (energyContainer.getEnergyStored() <= energyContainer.getEnergyCapacity()) {
            energyContainer.addEnergy((long) mana * (getTier() + 1));
        }
    }
}