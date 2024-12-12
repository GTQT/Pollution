package keqing.pollution.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thebetweenlands.client.gui.SwarmOverlayRenderer;

@Mixin(SwarmOverlayRenderer.class)
public abstract class MixinSwarmOverlayRenderer {

    @Overwrite
    public void update() {

    }

    @Overwrite
    public void render(float partialTicks) {
    }
}
