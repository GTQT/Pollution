package meowmel.pollution.common.entity.shoot;


import meowmel.pollution.common.entity.moster.EntityBasalz;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBlizzBolt extends EntityThrowable {

    public static PotionEffect basalzEffect = new PotionEffectBasalz(5 * 20, 2);

    public EntityBlizzBolt(World world) {

        super(world);
    }

    public EntityBlizzBolt(World world, EntityLivingBase thrower) {

        super(world, thrower);
    }

    public EntityBlizzBolt(World world, double x, double y, double z) {

        super(world, x, y, z);
    }

    @Override
    protected float getGravityVelocity() {

        return 0.005F;
    }

    @Override
    protected void onImpact(RayTraceResult traceResult) {

        if (!getEntityWorld().isRemote) {
            if (traceResult.entityHit != null) {
                if (traceResult.entityHit instanceof EntityBasalz) {
                    traceResult.entityHit.attackEntityFrom(DamageSourceBasalz.causeDamage(this, getThrower()), 0);
                } else {
                    if (traceResult.entityHit.attackEntityFrom(DamageSourceBasalz.causeDamage(this, getThrower()), 5F) && traceResult.entityHit instanceof EntityLivingBase) {
                        EntityLivingBase living = (EntityLivingBase) traceResult.entityHit;

                        if (EntityBasalz.effect) {
                            living.addPotionEffect(new PotionEffect(EntityBlizzBolt.basalzEffect));
                        }
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX, posY, posZ, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble());
            }
            setDead();
        }
    }

    @Override
    @SideOnly (Side.CLIENT)
    public int getBrightnessForRender() {

        return 0xF000F0;
    }

    /* DAMAGE SOURCE */
    protected static class DamageSourceBasalz extends EntityDamageSource {

        public DamageSourceBasalz() {

            this(null);
        }

        public DamageSourceBasalz(Entity source) {

            super("blizz", source);
        }

        public static DamageSource causeDamage(EntityBlizzBolt entityProj, Entity entitySource) {

            return (new EntityDamageSourceIndirect("blizz", entityProj, entitySource == null ? entityProj : entitySource)).setProjectile();
        }
    }

    protected static class PotionEffectBasalz extends PotionEffect {

        public PotionEffectBasalz(Potion potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticlesIn) {

            super(potionIn, durationIn, amplifierIn, ambientIn, showParticlesIn);
            getCurativeItems().clear();
        }

        public PotionEffectBasalz(int duration, int amplifier) {

            this(MobEffects.WEAKNESS, duration, amplifier, false, true);
        }

    }

}
