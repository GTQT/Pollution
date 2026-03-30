package meowmel.pollution.common.entity.moster;


import meowmel.pollution.Pollution;
import meowmel.pollution.common.entity.shoot.EntityBlitzBolt;
import meowmel.pollution.dimension.biome.POBiomeHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static meowmel.pollution.Pollution.MODID;

public class EntityBlitz extends EntityElemental {


    static boolean restrictLightLevel = true;
    static ResourceLocation lootTable;
    static int spawnLightLevel = 8;
    static int spawnWeight = 10;
    static int spawnMin = 1;
    static int spawnMax = 4;
    public static boolean effect = true;
    public static void initialize(int id) {
        lootTable = LootTableList.register(new ResourceLocation("pollution", "entities/blitz"));
        Set<Biome> validBiomes = new HashSet<>();
        validBiomes.add(POBiomeHandler.UnderWorld_BIOME);
        EntityRegistry.addSpawn(EntityBlitz.class, spawnWeight, spawnMin, spawnMax, EnumCreatureType.MONSTER, validBiomes.toArray(new Biome[validBiomes.size()]));
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "ele_blitz"), EntityBlitz.class,"Blitz",id, Pollution.instance,64,3,true, 0xF0F8FF, 0xFFEFD5);
    }


    public EntityBlitz(World world) {
        super(world);
        ambientParticle = EnumParticleTypes.TOWN_AURA;

    }

    @Nullable
    protected ResourceLocation getLootTable() {

        return lootTable;
    }

    @Override
    protected void initEntityAI() {

        tasks.addTask(4, new AIblitzBoltAttack(this));
        tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        tasks.addTask(7, new EntityAIWander(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }
    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }
    @Override
    protected boolean restrictLightLevel() {

        return restrictLightLevel;
    }

    @Override
    protected int getSpawnLightLevel() {

        return spawnLightLevel;
    }

    /* ATTACK */
    static class AIblitzBoltAttack extends EntityAIBase {

        private final EntityBlitz blitz;
        private int attackStep;
        private int attackTime;

        public AIblitzBoltAttack(EntityBlitz entity) {

            blitz = entity;
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {

            EntityLivingBase target = blitz.getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void startExecuting() {

            this.attackStep = 0;
        }

        @Override
        public void resetTask() {

            this.blitz.setInAttackMode(false);
        }

        @Override
        public void updateTask() {

            --this.attackTime;
            EntityLivingBase target = this.blitz.getAttackTarget();
            double d0 = this.blitz.getDistanceSq(target);

            if (d0 < 4.0D) {
                if (attackTime <= 0) {
                    attackTime = 20;
                    blitz.attackEntityAsMob(target);
                }

                blitz.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
            } else if (d0 < getFollowDistance() * getFollowDistance()) {

                if (attackTime <= 0) {
                    ++attackStep;

                    if (attackStep == 1) {
                        attackTime = 60;
                        blitz.setInAttackMode(true);
                    } else if (attackStep <= 4) {
                        attackTime = 6;
                    } else {
                        attackTime = 100;
                        attackStep = 0;
                        blitz.setInAttackMode(false);
                    }

                    if (attackStep > 1) {
                        blitz.world.playEvent(null, 1009, new BlockPos((int) blitz.posX, (int) blitz.posY, (int) blitz.posZ), 0);

                        for (int i = 0; i < 1; ++i) {
                            EntityBlitzBolt bolt = new EntityBlitzBolt(blitz.world, blitz);
                            bolt.shoot(target.posX - blitz.posX, target.posY - blitz.posY, target.posZ - blitz.posZ, 1.5F, 1.0F);
                            bolt.posY = blitz.posY + blitz.height / 2.0F + 0.5D;
                            blitz.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 2.0F, (blitz.rand.nextFloat() - blitz.rand.nextFloat()) * 0.2F + 1.0F);
                            blitz.world.spawnEntity(bolt);
                        }
                    }
                }
                blitz.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
            } else {
                blitz.getNavigator().clearPath();
                blitz.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
            }
            super.updateTask();
        }

        private double getFollowDistance() {

            IAttributeInstance attribute = this.blitz.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return attribute == null ? 16.0D : attribute.getAttributeValue();
        }
    }

}