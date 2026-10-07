package meowmel.pollution.common;

import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.registry.MTEManager;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.MaterialRegistryEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;

import meowmel.pollution.Pollution;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.unification.PollutionStoneTypes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "pollution")
public class EventLoader {

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerStoneTypes(PostMaterialEvent event) {
        PollutionStoneTypes.init();
    }
}
