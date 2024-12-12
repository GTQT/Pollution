package keqing.pollution.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.client.gui.GuiFirefly;

@Mixin(GuiFirefly.class)
public class MixinGuiFirefly {

    @Inject(method = "drawFireFly",at = @At("HEAD"))
    public void drawFireFly(Minecraft minecraft, float partialTicks, CallbackInfo ci)
    {
        return;
    }
    @Inject(method = "drawTexturedModalRectWithColor",at = @At("HEAD"))
    public void drawTexturedModalRectWithColor(int x, int y, int textureX, int textureY, int width, int height, int color, CallbackInfo ci)
    {
        return;
    }
}
