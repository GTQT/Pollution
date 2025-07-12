package keqing.pollution.mixin.thaumcraft;

import keqing.pollution.Pollution;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

@Mixin(ThaumcraftCraftingManager.class)
public class ThaumcraftCraftingManagerMixin {

    @Inject(method = "generateTags(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;", at = @At("RETURN"), remap = false)
    private static void captureLateObjectTags(ItemStack is, CallbackInfoReturnable<AspectList> cir) {
        if (Pollution.lateObjectTags != null) {
            Pollution.lateObjectTags.put(CommonInternals.generateUniqueItemstackId(is), cir.getReturnValue());
            Pollution.LOGGER.debug("Captured late object tag for item: " + is.getItem().getRegistryName());
        }
    }

}