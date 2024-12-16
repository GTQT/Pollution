package keqing.pollution.loaders.recipes;

import gregtech.api.metatileentity.multiblock.CleanroomType;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.item.ModItems;
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
        manahatch();
    }

    //"puredaisy", "manastar", "endoflame", "hydroangeas", "thermalily", "arcanerose", "munchdew", "entropinnyum", "kekimurus", "gourmaryllis", "narslimmus", "spectrolus", "rafflowsia", "shulk_me_not", "dandelifeon", "jadedAmaranthus", "bellethorn", "dreadthorn", "heiseiDream", "tigerseye", "marimorphosis", "orechid", "orechidIgnem", "fallenKanade", "exoflame", "agricarnation", "hopperhock", "rannuncarpus", "tangleberrie", "jiyuulia", "hyacidus", "medumone", "pollidisiac", "clayconia", "loonium", "daffomill", "vinculotus", "spectranthemum", "bubbell", "solegnolia", "bergamute"
    private static void flower() {

        //白雏菊诱变改造
        BIOLOGICAL_REACTION_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 8))
                .input(dust,NetherStar)
                .fluidInputs(Enzymesac.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("puredaisy"),1000,0)
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

    private static void manahatch(){
        //魔力仓
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[LV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.SENSOR_LV.getMetaItem(), 2, 232)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[1].getStackForm())
                .duration(100)
                .EUt(VA[LV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[MV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.SENSOR_MV.getMetaItem(), 2, 233)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[2].getStackForm())
                .duration(100)
                .EUt(VA[MV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[HV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.SENSOR_HV.getMetaItem(), 2, 234)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[3].getStackForm())
                .duration(100)
                .EUt(VA[HV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[EV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.SENSOR_EV.getMetaItem(), 2, 235)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[4].getStackForm())
                .duration(100)
                .EUt(VA[EV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[IV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.SENSOR_IV.getMetaItem(), 2, 236)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[5].getStackForm())
                .duration(100)
                .EUt(VA[IV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[LuV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_LuV.getMetaItem(), 2, 237)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[6].getStackForm())
                .duration(100)
                .EUt(VA[LuV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[ZPM].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_ZPM.getMetaItem(), 2, 238)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[7].getStackForm())
                .duration(100)
                .EUt(VA[ZPM])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_UV.getMetaItem(), 2, 239)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[8].getStackForm())
                .duration(100)
                .EUt(VA[UV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UHV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_UHV.getMetaItem(), 2, 240)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[9].getStackForm())
                .duration(100)
                .EUt(VA[UHV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UEV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_UEV.getMetaItem(), 2, 241)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[10].getStackForm())
                .duration(100)
                .EUt(VA[UEV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UIV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_UIV.getMetaItem(), 2, 242)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[11].getStackForm())
                .duration(100)
                .EUt(VA[UIV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UXV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_UXV.getMetaItem(), 2, 243)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[12].getStackForm())
                .duration(100)
                .EUt(VA[UXV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[OpV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_OpV.getMetaItem(), 2, 244)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_HATCH[13].getStackForm())
                .duration(100)
                .EUt(VA[OpV])
                .buildAndRegister();
        //魔力池仓
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[LV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.EMITTER_LV.getMetaItem(), 2, 217)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[1].getStackForm())
                .duration(100)
                .EUt(VA[LV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[MV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.EMITTER_MV.getMetaItem(), 2, 218)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[2].getStackForm())
                .duration(100)
                .EUt(VA[MV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[HV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.EMITTER_HV.getMetaItem(), 2, 219)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[3].getStackForm())
                .duration(100)
                .EUt(VA[HV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[EV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.EMITTER_EV.getMetaItem(), 2, 220)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[4].getStackForm())
                .duration(100)
                .EUt(VA[EV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[IV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.mansussteel, 2)
                .input(MetaItems.EMITTER_IV.getMetaItem(), 2, 221)
                .fluidInputs(Magic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[5].getStackForm())
                .duration(100)
                .EUt(VA[IV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[LuV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_LuV.getMetaItem(), 2, 222)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[6].getStackForm())
                .duration(100)
                .EUt(VA[LuV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[ZPM].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_ZPM.getMetaItem(), 2, 223)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[7].getStackForm())
                .duration(100)
                .EUt(VA[ZPM])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_UV.getMetaItem(), 2, 224)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[8].getStackForm())
                .duration(100)
                .EUt(VA[UV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UHV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_UHV.getMetaItem(), 2, 225)
                .fluidInputs(Richmagic.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[9].getStackForm())
                .duration(100)
                .EUt(VA[UHV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UEV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_UEV.getMetaItem(), 2, 226)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[10].getStackForm())
                .duration(100)
                .EUt(VA[UEV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UIV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_UIV.getMetaItem(), 2, 227)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[11].getStackForm())
                .duration(100)
                .EUt(VA[UIV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UXV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_UXV.getMetaItem(), 2, 228)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[12].getStackForm())
                .duration(100)
                .EUt(VA[UXV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[OpV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_OpV.getMetaItem(), 2, 229)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_HATCH[13].getStackForm())
                .duration(100)
                .EUt(VA[OpV])
                .buildAndRegister();
    }
}
