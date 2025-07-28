package keqing.pollution.common.entity;

import keqing.pollution.Pollution;
import keqing.pollution.client.entity.*;
import keqing.pollution.common.entity.moster.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static keqing.pollution.Pollution.MODID;

public class PoEntitiesRegistry {
    public static void init() {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "tc_slime_aer"), EntitySlimeAer.class,"Aer Slime",1, Pollution.instance,64,3,true);
        EntityRegistry.registerEgg(new ResourceLocation(MODID, "tc_slime_aer"),0x48e06e, 0x199038);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "tc_slime_ignis"), EntitySlimeAer.class,"Ignis Slime",2, Pollution.instance,64,3,true);
        EntityRegistry.registerEgg(new ResourceLocation(MODID, "tc_slime_ignis"),0x48e06e, 0x199038);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "tc_slime_aqua"), EntitySlimeAer.class,"Aqua Slime",3, Pollution.instance,64,3,true);
        EntityRegistry.registerEgg(new ResourceLocation(MODID, "tc_slime_aqua"),0x48e06e, 0x199038);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "tc_slime_terra"), EntitySlimeAer.class,"Terra Slime",4, Pollution.instance,64,3,true);
        EntityRegistry.registerEgg(new ResourceLocation(MODID, "tc_slime_terra"),0x48e06e, 0x199038);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "tc_slime_ordo"), EntitySlimeAer.class,"Ordo Slime",5, Pollution.instance,64,3,true);
        EntityRegistry.registerEgg(new ResourceLocation(MODID, "tc_slime_ordo"),0x48e06e, 0x199038);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "tc_slime_perditio"), EntitySlimeAer.class,"Perditio Slime",6, Pollution.instance,64,3,true);
        EntityRegistry.registerEgg(new ResourceLocation(MODID, "tc_slime_perditio"),0x48e06e, 0x199038);
    }
    @SideOnly(Side.CLIENT)
    public static void initRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimeAer.class,new EntityRenderFactory<>(RenderTcSlimeAer.class));
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimeignis.class,new EntityRenderFactory<>(RenderTcSlimeIgnis.class));
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimeAqua.class,new EntityRenderFactory<>(RenderTcSlimeAqua.class));
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimeTerra.class,new EntityRenderFactory<>(RenderTcSlimeTerra.class));
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimeOrdo.class,new EntityRenderFactory<>(RenderTcSlimeOrdo.class));
        RenderingRegistry.registerEntityRenderingHandler(EntitySlimePerditio.class,new EntityRenderFactory<>(RenderTcSlimePerditio.class));
    }
}
