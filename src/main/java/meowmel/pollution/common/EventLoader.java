package meowmel.pollution.common;

import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.registry.MTEManager;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.MaterialRegistryEvent;

import meowmel.pollution.POConfig;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static meowmel.pollution.api.utils.POTeleporter.portalIngredient;
import static meowmel.pollution.common.block.blocks.PollutionBlocksInit.BLOCK_TF_PORTAL;

@Mod.EventBusSubscriber(modid = "pollution")
public class EventLoader {

    private static final Random RANDOM = ThreadLocalRandom.current();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMTERegistry(MTEManager.MTERegistryEvent event) {
        GregTechAPI.mteManager.createRegistry(Pollution.MODID);
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void createMaterialRegistry(MaterialRegistryEvent event) {
        GregTechAPI.materialManager.createRegistry(Pollution.MODID);
    }


    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerMaterials(MaterialEvent event) {
        PollutionMaterials.register();
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;
        if (!world.isRemote && event.phase == TickEvent.Phase.END && player.ticksExisted % (POConfig.WorldSettingSwitch.checkPortalDestination ? 100 : 20) == 0) {
            checkForPortalCreation(player, world, 32.0F);
        }
    }

    private static void checkForPortalCreation(EntityPlayer player, World world, float rangeToCheck) {
        if (world.provider.getDimension() == POConfig.WorldSettingSwitch.originDimension
                || world.provider.getDimension() == POConfig.WorldSettingSwitch.BTNetherDimensionID
                || POConfig.WorldSettingSwitch.allowPortalsInOtherDimensions) {

            List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, player.getEntityBoundingBox().grow(rangeToCheck));

            for (EntityItem entityItem : itemList) {
                if (portalIngredient.apply(entityItem.getItem())) {
                    BlockPos pos = entityItem.getPosition();
                    IBlockState state = world.getBlockState(pos);
                    if (BLOCK_TF_PORTAL.canFormPortal(state)) {
                        Random rand = new Random();
                        for (int i = 0; i < 2; i++) {
                            double vx = rand.nextGaussian() * 0.02D;
                            double vy = rand.nextGaussian() * 0.02D;
                            double vz = rand.nextGaussian() * 0.02D;
                            world.spawnParticle(EnumParticleTypes.SPELL, entityItem.posX, entityItem.posY + 0.2, entityItem.posZ, vx, vy, vz);
                        }
                        BLOCK_TF_PORTAL.tryToCreatePortal(world, pos, entityItem, player);
                    }
                }
            }
        }
    }
}