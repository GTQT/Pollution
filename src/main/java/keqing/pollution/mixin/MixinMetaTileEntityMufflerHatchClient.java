package keqing.pollution.mixin;

import gregtech.client.utils.TooltipHelper;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.POConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(MetaTileEntityMufflerHatch.class)
public abstract class MixinMetaTileEntityMufflerHatchClient extends MetaTileEntityMultiblockPart {

    @Unique
    private final float pollution$pollutionMultiplier = (float) ((100 - (getTier() - 1) * 12.5) / 100 * POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier);

    @Final
    @Shadow
    private int recoveryChance;

    public MixinMetaTileEntityMufflerHatchClient(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.muffler_hatch.tooltip1"));
        tooltip.add(I18n.format("gregtech.muffler.recovery_tooltip", recoveryChance));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip1"));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip2", pollution$pollutionMultiplier));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("gregtech.machine.muffler_hatch.tooltip2"));
    }

}
