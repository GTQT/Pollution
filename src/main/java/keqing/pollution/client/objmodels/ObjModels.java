package keqing.pollution.client.objmodels;


import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

public class ObjModels {
    public static final IModelCustom sun = AdvancedModelLoader.loadModel(new ResourceLocation("pollution", "models/obj/sun.obj"));
    public static final ResourceLocation sun_pic = new ResourceLocation("pollution", "models/obj/sun.png");

}
