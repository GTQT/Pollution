package keqing.pollution.mixin.gtsteam;

import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import keqing.gtsteam.api.metatileentity.multiblock.NoEnergyMultiblockController;
import keqing.gtsteam.common.metatileentities.multi.primitive.MetaTileEntityIndustrialPrimitiveBlastFurnace;
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

@Mixin(MetaTileEntityIndustrialPrimitiveBlastFurnace.class)
public abstract class MixinMetaTileEntityIndustrialPrimitiveBlastFurnaceClient extends NoEnergyMultiblockController {

    @Unique
    private final float pollution$pollutionMultiplier = POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;


    public MixinMetaTileEntityIndustrialPrimitiveBlastFurnaceClient(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip1"));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip2", pollution$pollutionMultiplier));
    }
}
