package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.POConfig;
import meowmel.pollution.common.entity.dragon.net.DragonNetwork;
import meowmel.pollution.common.entity.dragon.net.PacketDragonWobble;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/** A single entity which grows from the egg stage into an adult ender dragon. */
public class EntityPollutionDragon extends EntityCreature {
    private static final DataParameter<Integer> STAGE = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> STAGE_TICKS = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> STAGE_DURATION = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> APPEARANCE = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOBBLE_TICKS = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WOBBLE_AXIS = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CRACK_LEVEL = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Float> BASE_SCALE = EntityDataManager.createKey(EntityPollutionDragon.class, DataSerializers.FLOAT);

    private int ticksInStage;
    private float lastAppliedSize = -1.0F;
    private boolean lastAppliedEggStage;
    private int wobbleStartedAt;
    private int clientWobbleAxis;
    private final Random dragonRandom = new Random();

    public EntityPollutionDragon(World world) {
        super(world);
        setSize(0.5F, 0.5F);
        setStage(DragonLifeStage.EGG);
        setAppearance(world.rand.nextInt(3));
        setBaseScale((float) POConfig.DragonSettings.dragonScale);
        enablePersistence();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(STAGE, DragonLifeStage.EGG.ordinal());
        dataManager.register(STAGE_TICKS, 0);
        dataManager.register(STAGE_DURATION, 24000);
        dataManager.register(APPEARANCE, 0);
        dataManager.register(WOBBLE_TICKS, 0);
        dataManager.register(WOBBLE_AXIS, 0);
        dataManager.register(CRACK_LEVEL, 0);
        dataManager.register(BASE_SCALE, 1.0F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(5, new EntityAIWander(this, 0.7D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public void onLivingUpdate() {
        if (isEgg()) {
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
            return;
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return isEgg() || super.canBreatheUnderwater();
    }

    @Override
    public boolean isPushedByWater() {
        return !isEgg() && super.isPushedByWater();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        applyStageSize();
        if (isEgg()) {
            getNavigator().clearPath();
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
        }

        if (world.isRemote) {
            if (getLifeStage() != DragonLifeStage.ADULT && ticksInStage < getStageDuration()) {
                ticksInStage++;
            }
            return;
        }
        if (isEgg()) {
            tickIncubation();
        } else if (getLifeStage() != DragonLifeStage.ADULT) {
            ticksInStage++;
            if (ticksInStage % 20 == 0) {
                dataManager.set(STAGE_TICKS, ticksInStage);
            }
            if (ticksInStage >= getStageDuration()) {
                advanceStage();
            }
        }
    }

    private void tickIncubation() {
        if (world.provider.getDimension() == 1 && !POConfig.DragonSettings.allowHatchingInEnd) {
            return;
        }
        ticksInStage++;
        if (ticksInStage % 20 == 0) {
            dataManager.set(STAGE_TICKS, ticksInStage);
        }
        int minimum = getStageDuration();
        float progress = minimum <= 0 ? 1.0F : Math.min(1.0F, (float) ticksInStage / minimum);
        if (progress >= 0.9F && getCrackLevel() < 2) {
            setCrackLevel(2);
            startWobble(20, dragonRandom.nextInt(3));
        } else if (progress >= 0.75F && getCrackLevel() < 1) {
            setCrackLevel(1);
        }
        if (ticksInStage >= minimum) {
            if (dragonRandom.nextInt(1200) == 0) {
                hatch();
            } else if (dragonRandom.nextInt(1000) == 0) {
                startWobble(24, dragonRandom.nextInt(3));
            }
        } else if (dragonRandom.nextInt(2400) == 0) {
            startWobble(18, dragonRandom.nextInt(3));
        }
    }

    private void hatch() {
        setCrackLevel(3);
        world.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, getSoundCategory(), 0.8F, 0.7F + rand.nextFloat() * 0.3F);
        for (int i = 0; i < 12; i++) {
            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextDouble() - 0.5D) * 0.8D,
                    posY + rand.nextDouble() * 0.8D, posZ + (rand.nextDouble() - 0.5D) * 0.8D,
                    0.0D, 0.08D, 0.0D, net.minecraft.block.Block.getStateId(net.minecraft.init.Blocks.DRAGON_EGG.getDefaultState()));
        }
        setStage(DragonLifeStage.HATCHLING);
        world.playSound(null, getPosition(), SoundEvents.ENTITY_ENDERDRAGON_GROWL, getSoundCategory(), 0.6F, 1.3F);
    }

    private void advanceStage() {
        DragonLifeStage next = getLifeStage().next();
        if (next == getLifeStage()) {
            return;
        }
        setStage(next);
        world.playSound(null, getPosition(), SoundEvents.ENTITY_ENDERDRAGON_GROWL, getSoundCategory(), 0.5F, 1.0F + next.ordinal() * 0.08F);
    }

    public void startWobble(int ticks, int axis) {
        if (world.isRemote) {
            return;
        }
        dataManager.set(WOBBLE_AXIS, axis);
        dataManager.set(WOBBLE_TICKS, Math.max(1, ticks));
        wobbleStartedAt = ticksExisted;
        clientWobbleAxis = axis;
        DragonNetwork.sendToAllAround(this, new PacketDragonWobble(getEntityId(), ticks, axis, getCrackLevel()));
    }

    public DragonLifeStage getLifeStage() {
        return DragonLifeStage.fromId(dataManager.get(STAGE));
    }

    @Override
    public String getName() {
        if (hasCustomName()) {
            return getCustomNameTag();
        }
        return I18n.translateToLocalFormatted("entity.pollution.ender_dragon.stage_name",
                super.getName(), I18n.translateToLocal(getLifeStage().getTranslationKey()));
    }

    public void setStage(DragonLifeStage stage) {
        ticksInStage = 0;
        dataManager.set(STAGE, stage.ordinal());
        dataManager.set(STAGE_TICKS, 0);
        dataManager.set(STAGE_DURATION, stage.getDurationTicks());
        setNoAI(stage == DragonLifeStage.EGG);
        applyStageSize();
    }

    public int getTicksInStage() {
        return Math.max(ticksInStage, dataManager.get(STAGE_TICKS));
    }

    public int getStageDuration() {
        return dataManager.get(STAGE_DURATION);
    }

    public float getGrowthScale(float partialTicks) {
        DragonLifeStage stage = getLifeStage();
        if (stage == DragonLifeStage.ADULT) {
            return stage.getScale();
        }
        int duration = Math.max(1, getStageDuration());
        float progress = Math.min(1.0F, (getTicksInStage() + partialTicks) / duration);
        return stage.getScale(progress);
    }

    public float getRenderScale() {
        return getRenderScale(0.0F);
    }

    public float getRenderScale(float partialTicks) {
        return Math.max(0.04F, getGrowthScale(partialTicks) * dataManager.get(BASE_SCALE));
    }

    public void setBaseScale(float baseScale) {
        dataManager.set(BASE_SCALE, Math.max(0.1F, Math.min(2.0F, baseScale)));
        lastAppliedSize = -1.0F;
        applyStageSize();
    }

    public float getBaseScale() {
        return dataManager.get(BASE_SCALE);
    }

    private void applyStageSize() {
        float scale = Math.max(0.04F, getRenderScale());
        boolean eggStage = isEgg();
        // Keep the collision box close to the continuously interpolated render scale,
        // especially during the very small hatchling stage.
        if (eggStage == lastAppliedEggStage && Math.abs(scale - lastAppliedSize) < 0.005F) {
            return;
        }
        lastAppliedSize = scale;
        lastAppliedEggStage = eggStage;
        float width = 3.5F * scale;
        float height = 2.5F * scale;
        if (eggStage) {
            width = 0.75F;
            height = 0.75F;
        }
        setSize(width, height);
        stepHeight = Math.max(0.1F, scale);
    }

    @Override
    public float getEyeHeight() {
        return isEgg() ? 0.5F : 1.7F * getRenderScale();
    }

    public boolean isEgg() {
        return getLifeStage() == DragonLifeStage.EGG;
    }

    public boolean isSaddled() {
        return false;
    }

    public int getAppearance() {
        return dataManager.get(APPEARANCE);
    }

    public void setAppearance(int appearance) {
        dataManager.set(APPEARANCE, Math.max(0, Math.min(2, appearance)));
    }

    public int getWobbleTicks() {
        return Math.max(0, dataManager.get(WOBBLE_TICKS) - Math.max(0, ticksExisted - wobbleStartedAt));
    }

    public int getWobbleAxis() {
        return clientWobbleAxis;
    }

    public void applyClientWobble(int ticks, int axis, int crackLevel) {
        if (!world.isRemote) {
            return;
        }
        dataManager.set(WOBBLE_TICKS, Math.max(1, ticks));
        dataManager.set(WOBBLE_AXIS, Math.max(0, Math.min(2, axis)));
        clientWobbleAxis = Math.max(0, Math.min(2, axis));
        wobbleStartedAt = ticksExisted;
        setCrackLevel(crackLevel);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (WOBBLE_TICKS.equals(key)) {
            wobbleStartedAt = ticksExisted;
        } else if (WOBBLE_AXIS.equals(key)) {
            clientWobbleAxis = dataManager.get(WOBBLE_AXIS);
        } else if (STAGE.equals(key)) {
            ticksInStage = 0;
        }
    }

    public int getCrackLevel() {
        return dataManager.get(CRACK_LEVEL);
    }

    public void setCrackLevel(int level) {
        dataManager.set(CRACK_LEVEL, Math.max(0, Math.min(3, level)));
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isEgg() ? SoundEvents.BLOCK_STONE_HIT : SoundEvents.ENTITY_ENDERDRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isEgg() ? SoundEvents.BLOCK_STONE_HIT : SoundEvents.ENTITY_ENDERDRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isEgg() ? SoundEvents.BLOCK_GLASS_BREAK : SoundEvents.ENTITY_ENDERDRAGON_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("DragonStage", getLifeStage().ordinal());
        compound.setString("DragonStageName", getLifeStage().name());
        compound.setInteger("DragonDataVersion", 1);
        compound.setInteger("DragonStageTicks", getTicksInStage());
        compound.setInteger("DragonStageDuration", getStageDuration());
        compound.setInteger("DragonAppearance", getAppearance());
        compound.setFloat("DragonBaseScale", getBaseScale());
        compound.setInteger("DragonCrackLevel", getCrackLevel());
        compound.setInteger("DragonWobbleTicks", getWobbleTicks());
        compound.setInteger("DragonWobbleAxis", getWobbleAxis());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        DragonLifeStage stage = DragonLifeStage.fromId(compound.getInteger("DragonStage"));
        if (compound.hasKey("DragonStageName", 8)) {
            try {
                stage = DragonLifeStage.valueOf(compound.getString("DragonStageName"));
            } catch (IllegalArgumentException ignored) {
                stage = DragonLifeStage.EGG;
            }
        }
        ticksInStage = Math.max(0, compound.getInteger("DragonStageTicks"));
        dataManager.set(STAGE, stage.ordinal());
        dataManager.set(STAGE_TICKS, ticksInStage);
        int minimumStageDuration = stage == DragonLifeStage.ADULT ? 0 : 1;
        dataManager.set(STAGE_DURATION, compound.hasKey("DragonStageDuration")
                ? Math.max(minimumStageDuration, compound.getInteger("DragonStageDuration")) : stage.getDurationTicks());
        setAppearance(compound.getInteger("DragonAppearance"));
        setBaseScale(compound.hasKey("DragonBaseScale") ? compound.getFloat("DragonBaseScale")
                : (float) POConfig.DragonSettings.dragonScale);
        setCrackLevel(compound.getInteger("DragonCrackLevel"));
        dataManager.set(WOBBLE_TICKS, Math.max(0, compound.getInteger("DragonWobbleTicks")));
        dataManager.set(WOBBLE_AXIS, Math.max(0, Math.min(2, compound.getInteger("DragonWobbleAxis"))));
        wobbleStartedAt = ticksExisted;
        clientWobbleAxis = dataManager.get(WOBBLE_AXIS);
        setNoAI(stage == DragonLifeStage.EGG);
        lastAppliedSize = -1.0F;
        lastAppliedEggStage = false;
        applyStageSize();
    }
}
