/*
 ** 2011 December 10
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package meowmel.pollution.client.entity.dragon.model;

import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/** DragonMounts ender-dragon geometry with Pollution's growth-aware animation input. */
public class DragonModel extends ModelBase {
    // Renderers reuse models; this animator is scratch state and is fully refreshed per render.
    private final DragonAnimator animator = new DragonAnimator();

    public final HeadPart head;
    public final ISegmentedPart neck;
    public final ISegmentedPart tail;
    public final LegPart foreLeg;
    public final LegPart hindLeg;
    public final BodyPart body;
    public final WingPart wing;
    public final ModelRenderer chest;
    public final ModelRenderer saddle;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float pitch;
    public float partialTicks;

    public DragonModel(IModelFactory factory) {
        factory.defineTextures(this, this::setTextureOffset);
        head = factory.makeHead(this);
        neck = factory.makeNeck(this);
        body = factory.makeBody(this);
        body.addChild(chest = factory.makeChest(this));
        chest.showModel = false;
        body.addChild(saddle = factory.makeSaddle(this));
        saddle.showModel = false;
        wing = factory.makeWing(this);
        tail = factory.makeTail(this);
        foreLeg = factory.makeForeLeg(this);
        hindLeg = factory.makeHindLeg(this);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        if (!(entity instanceof EntityPollutionDragon)) {
            return;
        }
        animator.setMovement(limbSwing, limbSwingAmount);
        animator.setLook(netHeadYaw, headPitch);
        animator.animate((EntityPollutionDragon) entity, this);
        head.setupAnim(animator);
        neck.setupSegments(animator.neckSegments);
        tail.setupSegments(animator.tailSegments);
        body.setupAnim(animator);
        wing.setupAnim(animator);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ticksExisted,
                       float headYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ticksExisted, headYaw, headPitch, scale, entity);
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX, offsetY, offsetZ);
        if (pitch != 0.0F) {
            GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.92F, 0.92F, 0.92F);
        head.render(scale);
        GlStateManager.popMatrix();
        neck.renderSegments(scale);
        body.render(scale);
        tail.renderSegments(scale);
        renderLegs(scale);
        renderWings(scale);
        GlStateManager.popMatrix();
    }

    private void renderWings(float scale) {
        scale *= 1.1F;
        GlStateManager.pushMatrix();
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        wing.render(scale);
        GlStateManager.scale(-1.0F, 1.0F, 1.0F);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        wing.render(scale);
        GlStateManager.disableCull();
        GlStateManager.popMatrix();
    }

    private void renderLegs(float scale) {
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        foreLeg.left.apply(foreLeg);
        foreLeg.render(scale);
        hindLeg.left.apply(hindLeg);
        hindLeg.render(scale);
        GlStateManager.scale(-1.0F, 1.0F, 1.0F);
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        foreLeg.right.apply(foreLeg);
        foreLeg.render(scale);
        hindLeg.right.apply(hindLeg);
        hindLeg.render(scale);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
    }
}
