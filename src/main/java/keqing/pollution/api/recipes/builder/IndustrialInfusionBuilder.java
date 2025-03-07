package keqing.pollution.api.recipes.builder;

import keqing.pollution.api.utils.POAspectToGtFluidList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.Iterator;

import static keqing.pollution.api.recipes.PORecipeMaps.INDUSTRIAL_INFUSION_RECIPES;

public class IndustrialInfusionBuilder {
    public static void init()
    {
        Iterator var3 = ThaumcraftApi.getCraftingRecipes().values().iterator();
        Object recipe;
        while (var3.hasNext())
        {
            recipe = var3.next();
            if((recipe != null && recipe instanceof InfusionRecipe) )
            {
                InfusionRecipe read = (InfusionRecipe)recipe;
                if(read.recipeOutput instanceof ItemStack)
                {
                    var list = read.getRecipeInput().getMatchingStacks();
                    var listin = read.getComponents();
                    var out = ((ItemStack) read.recipeOutput).copy();
                    var aspectList = read.getAspects();
                    var aspect = read.getAspects().getAspects();
                    FluidStack[] fluids = new FluidStack[aspect.length];
                    int during = 0;
                    for (int i=0; i<aspect.length;i++)
                    {
                        during+=aspectList.getAmount(aspect[i]);
                        if(POAspectToGtFluidList.aspectToGtFluidList.get(aspect[i])!=null)
                            fluids[i] = POAspectToGtFluidList.aspectToGtFluidList.get(aspect[i]).getFluid(aspectList.getAmount(aspect[i])*144);
                    }
                    var brec = INDUSTRIAL_INFUSION_RECIPES.recipeBuilder()
                            .inputs(list)
                            .outputs(out)
                            .fluidInputs(fluids)
                            .duration(during)
                            .EUt(120);
                    for (var iii:listin)
                    {
                        brec.inputs(iii.getMatchingStacks());
                    }
                    brec.buildAndRegister();
                }

            }
        }
    }
}
