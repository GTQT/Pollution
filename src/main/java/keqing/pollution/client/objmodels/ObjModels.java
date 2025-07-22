package keqing.pollution.client.objmodels;

import keqing.gtqtcore.api.obj.AdvancedModelLoader;
import keqing.gtqtcore.api.obj.IModelCustom;
import net.minecraft.util.ResourceLocation;

public class ObjModels {
    public static final IModelCustom sun = AdvancedModelLoader.loadModel(new ResourceLocation("pollution", "models/obj/sun.obj"));
    public static final ResourceLocation sun_pic = new ResourceLocation("pollution", "models/obj/sun.png");

}
