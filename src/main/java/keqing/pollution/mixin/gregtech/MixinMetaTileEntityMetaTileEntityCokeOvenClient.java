package keqing.pollution.mixin.gregtech;

import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.recipes.RecipeMap;
import gregtech.common.metatileentities.multi.MetaTileEntityCokeOven;
import keqing.pollution.POConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;


@Mixin(MetaTileEntityCokeOven.class)
public abstract class MixinMetaTileEntityMetaTileEntityCokeOvenClient extends RecipeMapPrimitiveMultiblockController {

    @Unique
    private final float pollution$pollutionMultiplier = POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;

    public MixinMetaTileEntityMetaTileEntityCokeOvenClient(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip1"));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip2", pollution$pollutionMultiplier));
    }
}
