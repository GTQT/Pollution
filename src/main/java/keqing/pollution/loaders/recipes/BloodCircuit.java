package keqing.pollution.loaders.recipes;


import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import gregtech.api.metatileentity.multiblock.CleanroomType;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.RecipeMaps.MACERATOR_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.FLUID_CANNER_RECIPES;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;

public class BloodCircuit {
    public static void init() {
        BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_MUSHROOM_BLOCK, 1, 8))
                .input(dust,NetherStar)
                .fluidInputs(Enzymesac.getFluid(100))
                .output(ItemBlockSpecialFlower.ofType("puredaisy").getItem(), 1)
                .rate(10)
                .buildAndRegister();


    }

    public static void addBloodAltarEx(ItemStack material1, ItemStack material2, int tier)
    {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(Ingredient.fromStacks(material1),material2,tier,(int) Math.pow(2,tier+2)*500,(int) Math.pow(2,tier+2),(int) Math.pow(2,tier+3));
    }
}
