package keqing.pollution.mixin;

import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import keqing.pollution.POConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(SimpleGeneratorMetaTileEntity.class)
public abstract class MixinSimpleGeneratorMetaTileEntityClient extends WorkableTieredMetaTileEntity {

    @Unique
    private final float pollution$pollutionMultiplier = (float) (25 + (getTier() - 1) * 15) / 100 * POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;

    public MixinSimpleGeneratorMetaTileEntityClient(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer, int tier, Function<Integer, Integer> tankScalingFunction, boolean handlesRecipeOutputs) {
        super(metaTileEntityId, recipeMap, renderer, tier, tankScalingFunction, handlesRecipeOutputs);
    }

    @Inject(method = "addInformation", at = @At("TAIL"))
    private void injectTooltip(ItemStack stack, World worldIn, List<String> tooltip, boolean advanced, CallbackInfo ci) {
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip1"));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip2", pollution$pollutionMultiplier));
    }
}
