package keqing.pollution.integration.thaumcraft;

import gregtech.api.GTValues;
import gregtech.api.modules.GregTechModule;
import gregtech.api.util.GTUtility;
import gregtech.integration.IntegrationModule;
import gregtech.modules.GregTechModules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.Arrays;
import java.util.List;

@GregTechModule(
        moduleID = "tc_integration",
        containerID = GTValues.MODID,
        modDependencies = "thaumcraft",
        name = "GregTech Thaumcraft Integration",
        description = "Thaumcraft Integration Module"
)
public class ThaumcraftModule extends IntegrationModule {

    @Override
    public void construction(FMLConstructionEvent event) {
        TCAspects.AER.tcAspect = Aspect.AIR;
        TCAspects.TERRA.tcAspect = Aspect.EARTH;
        TCAspects.IGNIS.tcAspect = Aspect.FIRE;
        TCAspects.AQUA.tcAspect = Aspect.WATER;
        TCAspects.ORDO.tcAspect = Aspect.ORDER;
        TCAspects.PERDITIO.tcAspect = Aspect.ENTROPY;
        TCAspects.VACUOS.tcAspect = Aspect.VOID;
        TCAspects.LUX.tcAspect = Aspect.LIGHT;
        TCAspects.MOTUS.tcAspect = Aspect.MOTION;
        TCAspects.GELUM.tcAspect = Aspect.COLD;
        TCAspects.VITREUS.tcAspect = Aspect.CRYSTAL;
        TCAspects.METALLUM.tcAspect = Aspect.METAL;
        TCAspects.VICTUS.tcAspect = Aspect.LIFE;
        TCAspects.MORTUUS.tcAspect = Aspect.DEATH;
        TCAspects.POTENTIA.tcAspect = Aspect.ENERGY;
        TCAspects.PERMUTATIO.tcAspect = Aspect.EXCHANGE;
        TCAspects.PRAECANTATIO.tcAspect = Aspect.MAGIC;
        TCAspects.AURAM.tcAspect = Aspect.AURA;
        TCAspects.ALKIMIA.tcAspect = Aspect.ALCHEMY;
        TCAspects.VITIUM.tcAspect = Aspect.FLUX;
        TCAspects.TENEBRAE.tcAspect = Aspect.DARKNESS;
        TCAspects.ALIENIS.tcAspect = Aspect.ELDRITCH;
        TCAspects.VOLATUS.tcAspect = Aspect.FLIGHT;
        TCAspects.HERBA.tcAspect = Aspect.PLANT;
        TCAspects.INSTRUMENTUM.tcAspect = Aspect.TOOL;
        TCAspects.FABRICO.tcAspect = Aspect.CRAFT;
        TCAspects.MACHINA.tcAspect = Aspect.MECHANISM;
        TCAspects.VINCULUM.tcAspect = Aspect.TRAP;
        TCAspects.SPIRITUS.tcAspect = Aspect.SOUL;
        TCAspects.COGNITIO.tcAspect = Aspect.MIND;
        TCAspects.SENSUS.tcAspect = Aspect.SENSES;
        TCAspects.AVERSIO.tcAspect = Aspect.AVERSION;
        TCAspects.PRAEMUNIO.tcAspect = Aspect.PROTECT;
        TCAspects.DESIDERIUM.tcAspect = Aspect.DESIRE;
        TCAspects.EXANIMIS.tcAspect = Aspect.UNDEAD;
        TCAspects.BESTIA.tcAspect = Aspect.BEAST;
        TCAspects.HUMANUS.tcAspect = Aspect.MAN;

        // Thaumic Bases

        TCAspects.FAMES.tcAspect = Aspect.getAspect("fames");
        TCAspects.ITER.tcAspect = Aspect.getAspect("iter");
        TCAspects.SANO.tcAspect = Aspect.getAspect("sano");
        TCAspects.PANNUS.tcAspect = Aspect.getAspect("pannus");
        TCAspects.MESSIS.tcAspect = Aspect.getAspect("messis");


    }

    private static AspectList getAspectList(List<TCAspects.TCAspectStack> aspects) {
        AspectList aspectList = new AspectList();
        for (TCAspects.TCAspectStack aspect : aspects) {
            if (aspect.aspect.tcAspect != null) {
                aspectList.add((Aspect) aspect.aspect.tcAspect, (int) aspect.amount);
            }
        }
        return aspectList;
    }

    public static void addCrucibleRecipe( String name,  String research,  Object input,  ItemStack output,
                                          List<TCAspects.TCAspectStack> aspects) {
        if (output.isEmpty()) return;

        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(GTValues.MODID, name), new CrucibleRecipe(
                research,
                GTUtility.copy(output),
                (input instanceof ItemStack || input instanceof List<?>) ? input : input.toString(),
                getAspectList(aspects)));
    }

    public static void addInfusionRecipe( String name,  String research,  ItemStack output, int instability,
                                         List<TCAspects.TCAspectStack> aspects,  ItemStack mainInput, ItemStack... sideInputs) {
        if (output.isEmpty()) return;
        if (aspects.isEmpty()) return;

        Object[] inputs = Arrays.stream(sideInputs).map(Ingredient::fromStacks).toArray();
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(GTValues.MODID, name), new InfusionRecipe(
                research,
                GTUtility.copy(output),
                instability,
                getAspectList(aspects),
                mainInput,
                inputs));
    }

    //public static void addArcaneCraftingRecipe(@NotNull String name, @NotNull String research, ) {

    //    ThaumcraftApi.addArcaneCraftingRecipe(ForgeRegistries.RECIPES, new ShapedArcaneRecipe());
    //}

    public static void registerAspectsToItem( String oreDict,  List<TCAspects.TCAspectStack> aspects) {
        if (aspects.isEmpty()) return;
        ThaumcraftApi.registerObjectTag(oreDict, getAspectList(aspects));
    }

    public static void registerAspectsToItem( ItemStack stack,  List<TCAspects.TCAspectStack> aspects, boolean additive) {
        if (aspects.isEmpty()) return;
        if (additive) {
            ThaumcraftApi.registerComplexObjectTag(stack, getAspectList(aspects));
        } else {
            AspectList alreadyRegisteredAspects = ThaumcraftApi.internalMethods.getObjectAspects(stack);
            if (alreadyRegisteredAspects == null || alreadyRegisteredAspects.size() <= 0) {
                ThaumcraftApi.registerObjectTag(stack, getAspectList(aspects));
            }
        }
    }
}