package keqing.pollution.loaders.recipes.mods.TheBetweendLand;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.stack.ItemMaterialInfo;
import gregtech.api.unification.stack.MaterialStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

import static gregtech.api.GTValues.M;
import static gregtech.api.unification.material.Materials.Water;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.unification.ore.OrePrefix.dustSmall;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Limestone;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class StoneLine {
    public static void init() {
        //补充矿辞
        OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.CRAGROCK, 1));
        OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.PITSTONE, 1));
        OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.LIMESTONE, 1));
        OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.BETWEENSTONE, 1));
        OreDictionary.registerOre("sand", new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        OreDictionary.registerOre("stoneCobble", new ItemStack(BlockRegistry.CRAGROCK, 1));
        OreDictionary.registerOre("stoneCobble", new ItemStack(BlockRegistry.PITSTONE, 1));
        OreDictionary.registerOre("stoneCobble", new ItemStack(BlockRegistry.LIMESTONE, 1));
        OreDictionary.registerOre("stoneCobble", new ItemStack(BlockRegistry.BETWEENSTONE, 1));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, CragRock)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemMaterialInfo(new MaterialStack(CragRock, M)));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, PitStone)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemMaterialInfo(new MaterialStack(PitStone, M)));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, Limestone)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemMaterialInfo(new MaterialStack(Limestone, M)));

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.GENERIC_STONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .fluidInputs(Water.getFluid(1000))
                .outputs(new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, BetweenStone)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemMaterialInfo(new MaterialStack(BetweenStone, M)));

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_1.getMetadata()))
                .fluidInputs(Water.getFluid(1000))
                .outputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_1.getMetadata()), new ItemMaterialInfo(new MaterialStack(CragRock, M)));

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_2.getMetadata()))
                .fluidInputs(Water.getFluid(1000))
                .outputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_2.getMetadata()), new ItemMaterialInfo(new MaterialStack(CragRock, M)));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(ItemMisc.EnumItemMisc.BETWEENSTONE_PEBBLE.create(1))
                .output(dustSmall, BetweenStone)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        OreDictUnifier.registerOre(ItemMisc.EnumItemMisc.BETWEENSTONE_PEBBLE.create(1), new ItemMaterialInfo(new MaterialStack(CragRock, M / 4)));

        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemMaterialInfo(new MaterialStack(Mud, M)));
        OreDictUnifier.registerOre(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemMaterialInfo(new MaterialStack(Mud, M)));

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SLIMY_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .fluidInputs(Water.getFluid(1000))
                .output(Blocks.DIRT)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SLIMY_GRASS, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .fluidInputs(Water.getFluid(1000))
                .output(Blocks.GRASS)
                .duration(100)
                .EUt(16)
                .buildAndRegister();


        ////////////////////////////////////////////////
        //通用矿辞处理，补全对原版材料的获取
        ModHandler.addShapedRecipe(true, "PoDirt", new ItemStack(Items.CLAY_BALL),
                " h", " M",
                'M', new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        ModHandler.addShapedRecipe(true, "PoStone5", new ItemStack(Blocks.GRAVEL),
                " h", " M",
                'M', "cobblestone");

        ModHandler.addShapedRecipe(true, "PoSand", new ItemStack(Blocks.SAND),
                " h", " M",
                'M', Blocks.GRAVEL);

        ModHandler.addShapedRecipe(true, "PoFlint", new ItemStack(Items.FLINT),
                "MM", "MM",
                'M', ItemMisc.EnumItemMisc.BETWEENSTONE_PEBBLE.create(1));

        ModHandler.addShapedRecipe(true, "PoString", new ItemStack(Items.STRING),
                "   ", "MMM", "   ",
                'M', ItemMisc.EnumItemMisc.SWAMP_REED_ROPE.create(1));

        //刷石
        RecipeMaps.ROCK_BREAKER_RECIPES.recipeBuilder()
                .notConsumable(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .outputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .EUt(960)
                .duration(16)
                .buildAndRegister();
        RecipeMaps.ROCK_BREAKER_RECIPES.recipeBuilder()
                .notConsumable(new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .outputs(new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .EUt(960)
                .duration(16)
                .buildAndRegister();
        RecipeMaps.ROCK_BREAKER_RECIPES.recipeBuilder()
                .notConsumable(new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .outputs(new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .EUt(960)
                .duration(16)
                .buildAndRegister();
    }
}
