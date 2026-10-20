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
import meowmel.pollution.client.entity.dragon.model.util.MathX;
import meowmel.pollution.client.entity.dragon.model.util.Segment;
import net.minecraft.util.math.MathHelper;

import static meowmel.pollution.client.entity.dragon.model.DragonModelContracts.NECK_SEGMENTS;
import static meowmel.pollution.client.entity.dragon.model.DragonModelContracts.TAIL_SEGMENTS;
import static meowmel.pollution.client.entity.dragon.model.DragonModelContracts.WING_FINGERS;

/** Compact ground-animation controller for the migrated DragonMounts model. */
public final class DragonAnimator {
    private static final float[] WING_FINGER_ANGLES = {0.15F, 0.72F, 1.38F, 2.15F};

    public final Segment head = new Segment();
    public final Segment[] neckSegments = createSegments(NECK_SEGMENTS);
    public final Segment[] tailSegments = createSegments(TAIL_SEGMENTS);

    private float movement;
    private float movementSpeed;
    private float lookYaw;
    private float lookPitch;
    private float wingArmRotateAngleX;
    private float wingArmRotateAngleY;
    private float wingArmRotateAngleZ;
    private float wingForearmRotateAngleX;
    private float wingForearmRotateAngleY;
    private float wingForearmRotateAngleZ;
    private final float[] wingFingerRotateY = new float[WING_FINGERS];
    private float jawRotateAngleX;

    public DragonAnimator() {
    }

    private static Segment[] createSegments(int length) {
        Segment[] segments = new Segment[length];
        for (int i = 0; i < length; i++) {
            segments[i] = new Segment();
        }
        return segments;
    }

    public void setMovement(float limbSwing, float limbSwingAmount) {
        movement = limbSwing;
        movementSpeed = Math.min(1.0F, limbSwingAmount);
    }

    public void setLook(float yawDegrees, float pitchDegrees) {
        lookYaw = MathHelper.clamp(yawDegrees, -45.0F, 45.0F) * MathX.DEGREES_TO_RADIANS;
        lookPitch = MathHelper.clamp(pitchDegrees, -30.0F, 30.0F) * MathX.DEGREES_TO_RADIANS;
    }

    public void animate(EntityPollutionDragon dragon, DragonModel model) {
        float time = dragon.ticksExisted + model.partialTicks;
        float idle = MathHelper.sin(time * 0.08F);
        float gait = MathHelper.sin(movement * 0.75F) * movementSpeed;
        float scale = dragon.getRenderScale();
        jawRotateAngleX = 0.08F + Math.max(0.0F,
                MathHelper.sin((dragon.ticksExisted + dragon.getLifeStage().ordinal() * 17) * 0.035F)) * 0.12F;

        head.posX = 0.0F;
        head.posY = -1.0F;
        head.posZ = -10.0F;
        head.rotX = lookPitch + idle * 0.025F;
        head.rotY = lookYaw + idle * 0.035F;
        head.rotZ = 0.0F;

        for (int i = 0; i < neckSegments.length; i++) {
            Segment segment = neckSegments[i];
            float fraction = (i + 1.0F) / neckSegments.length;
            segment.posX = MathHelper.sin(time * 0.055F - i * 0.3F) * 0.35F * fraction;
            segment.posY = -1.0F + MathHelper.sin(time * 0.04F - i * 0.25F) * 0.18F;
            segment.posZ = -3.0F + i * 7.0F;
            segment.rotX = lookPitch * (1.0F - fraction) + MathHelper.sin(time * 0.055F - i * 0.3F) * 0.025F;
            segment.rotY = lookYaw * (1.0F - fraction) + MathHelper.sin(time * 0.05F - i * 0.4F) * 0.035F;
            segment.rotZ = 0.0F;
        }

        for (int i = 0; i < tailSegments.length; i++) {
            Segment segment = tailSegments[i];
            float fraction = (i + 1.0F) / tailSegments.length;
            segment.posX = MathHelper.sin(time * 0.045F - i * 0.18F) * (0.6F + fraction * 1.4F);
            segment.posY = 4.0F + MathHelper.sin(time * 0.06F - i * 0.22F) * fraction * 0.35F;
            segment.posZ = 28.0F + i * 7.0F;
            segment.rotX = MathHelper.sin(time * 0.06F - i * 0.22F) * 0.025F;
            segment.rotY = MathHelper.sin(time * 0.045F - i * 0.18F) * (0.025F + fraction * 0.06F);
            segment.rotZ = 0.0F;
        }

        float flap = MathHelper.sin(time * 0.14F) * 0.08F;
        wingArmRotateAngleX = 0.02F + flap;
        wingArmRotateAngleY = 0.15F;
        wingArmRotateAngleZ = -0.28F + flap * 0.3F;
        wingForearmRotateAngleX = -0.15F - flap * 0.4F;
        wingForearmRotateAngleY = 0.0F;
        wingForearmRotateAngleZ = -0.12F;
        System.arraycopy(WING_FINGER_ANGLES, 0, wingFingerRotateY, 0, WING_FINGERS);

        model.foreLeg.left.shankRotX = -0.4F + gait * 0.35F;
        model.foreLeg.left.footRotX = 0.7F - gait * 0.35F;
        model.foreLeg.left.toeRotX = 0.0F;
        model.foreLeg.right.shankRotX = -0.4F - gait * 0.35F;
        model.foreLeg.right.footRotX = 0.7F + gait * 0.35F;
        model.foreLeg.right.toeRotX = 0.0F;
        model.hindLeg.left.shankRotX = 0.15F - gait * 0.4F;
        model.hindLeg.left.footRotX = -0.1F + gait * 0.3F;
        model.hindLeg.left.toeRotX = 0.0F;
        model.hindLeg.right.shankRotX = 0.15F + gait * 0.4F;
        model.hindLeg.right.footRotX = -0.1F - gait * 0.3F;
        model.hindLeg.right.toeRotX = 0.0F;
        model.foreLeg.left.rotationPointX = model.foreLeg.right.rotationPointX = -11.0F;
        model.foreLeg.left.rotationPointY = model.foreLeg.right.rotationPointY = 18.0F;
        model.foreLeg.left.rotationPointZ = model.foreLeg.right.rotationPointZ = 4.0F;
        model.hindLeg.left.rotationPointX = model.hindLeg.right.rotationPointX = -11.0F;
        model.hindLeg.left.rotationPointY = model.hindLeg.right.rotationPointY = 13.0F;
        model.hindLeg.left.rotationPointZ = model.hindLeg.right.rotationPointZ = 46.0F;

        model.offsetX = 0.0F;
        model.offsetY = -1.5F + idle * 0.04F * scale;
        model.offsetZ = -1.5F;
        model.pitch = 0.0F;
    }

    public float getJawRotateAngleX() {
        return jawRotateAngleX;
    }

    public float getWingArmRotateAngleX() { return wingArmRotateAngleX; }
    public float getWingArmRotateAngleY() { return wingArmRotateAngleY; }
    public float getWingArmRotateAngleZ() { return wingArmRotateAngleZ; }
    public float getWingForearmRotateAngleX() { return wingForearmRotateAngleX; }
    public float getWingForearmRotateAngleY() { return wingForearmRotateAngleY; }
    public float getWingForearmRotateAngleZ() { return wingForearmRotateAngleZ; }
    public float getWingFingerRotateY(int index) { return wingFingerRotateY[index]; }
}
