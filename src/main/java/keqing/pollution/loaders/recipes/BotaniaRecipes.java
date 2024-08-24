package keqing.pollution.loaders.recipes;

import gregtech.api.metatileentity.multiblock.CleanroomType;
import gregtech.api.unification.ore.OrePrefix;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.RecipeMaps.MACERATOR_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.FLUID_CANNER_RECIPES;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;
import static keqing.gtqtcore.common.items.GTQTMetaItems.CONTAMINATED_PETRI_DISH;
import static keqing.gtqtcore.common.items.GTQTMetaItems.SHEWANELLA_CULTURE;
import static net.minecraft.block.BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
import static net.minecraft.init.Blocks.RED_FLOWER;
import static vazkii.botania.common.block.ModBlocks.mushroom;
import static vazkii.botania.common.block.ModBlocks.specialFlower;
import static vazkii.botania.common.item.ModItems.*;
import static vazkii.botania.common.lib.LibBlockNames.FLOWER;
import static vazkii.botania.common.lib.LibBlockNames.MUSHROOM;

public class BotaniaRecipes {
    public static void init() {
        remove();
        flower();
    }

    //"puredaisy", "manastar", "endoflame", "hydroangeas", "thermalily", "arcanerose", "munchdew", "entropinnyum", "kekimurus", "gourmaryllis", "narslimmus", "spectrolus", "rafflowsia", "shulk_me_not", "dandelifeon", "jadedAmaranthus", "bellethorn", "dreadthorn", "heiseiDream", "tigerseye", "marimorphosis", "orechid", "orechidIgnem", "fallenKanade", "exoflame", "agricarnation", "hopperhock", "rannuncarpus", "tangleberrie", "jiyuulia", "hyacidus", "medumone", "pollidisiac", "clayconia", "loonium", "daffomill", "vinculotus", "spectranthemum", "bubbell", "solegnolia", "bergamute"
    private static void flower() {

        //白雏菊诱变改造
        BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 8))
                .input(dust,NetherStar)
                .fluidInputs(Enzymesac.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("puredaisy"),100,0)
                .cleanroom(CleanroomType.CLEANROOM)
                .rate(10)
                .buildAndRegister();

        //火红莲诱变改造
        BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 0))
                .input(dust,Blaze)
                .fluidInputs(Enzymesac.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("endoflame"),100,0)
                .cleanroom(CleanroomType.CLEANROOM)
                .rate(10)
                .buildAndRegister();

        //火红莲诱变改造
        BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 0))
                .input(OrePrefix.dust, PollutionMaterials.llp, 1)
                .fluidInputs(Enzymesac.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("hydroangeas"),100,0)
                .cleanroom(CleanroomType.CLEANROOM)
                .rate(10)
                .buildAndRegister();

        //瓶装末地空气
        FLUID_CANNER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .fluidInputs(LiquidEnderAir.getFluid(1000))
                .input(Items.GLASS_BOTTLE)
                .outputs(new ItemStack(manaResource, 1, 15))
                .cleanroom(CleanroomType.CLEANROOM)
                .buildAndRegister();

        //染料线
        int min;
        for(min = 0; min < 16; ++min) {
            BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(RED_FLOWER)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .rate(10)
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(Blocks.YELLOW_FLOWER)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .rate(10)
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(Blocks.BROWN_MUSHROOM)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .rate(10)
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(Blocks.RED_MUSHROOM)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .rate(10)
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(GTQTMetaItems.COMMON_ALGAE)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .rate(10)
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            MACERATOR_RECIPES.recipeBuilder().EUt(VA[LV]).duration(40)
                    .outputs(new ItemStack(dye, 2, min))
                    .inputs(new ItemStack(petal, 1, min))
                    .buildAndRegister();

            MACERATOR_RECIPES.recipeBuilder().EUt(VA[LV]).duration(40)
                    .outputs(new ItemStack(dye, 2, min))
                    .inputs(new ItemStack(mushroom, 1, min))
                    .buildAndRegister();
        }
    }

    private static void remove() {

    }
}
