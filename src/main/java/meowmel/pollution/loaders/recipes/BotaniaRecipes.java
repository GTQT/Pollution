package meowmel.pollution.loaders.recipes;

import gregtech.api.metatileentity.multiblock.CleanroomType;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;
import meowmel.gtqtcore.common.items.GTQTMetaItems;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.RecipeMaps.CANNER_RECIPES;
import static gregtech.api.recipes.RecipeMaps.MACERATOR_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static meowmel.gtqtcore.api.recipes.GTQTRecipeMaps.BACTERIAL_VAT_RECIPES;
import static meowmel.gtqtcore.api.unification.material.GTQTMaterials.VoidMetal;
import static net.minecraft.init.Blocks.RED_FLOWER;
import static vazkii.botania.common.block.ModBlocks.mushroom;
import static vazkii.botania.common.item.ModItems.*;

public class BotaniaRecipes {
    public static void init() {
        remove();
        flower();
        rune();
        manahatch();

    }
    private static void rune() {
        //三种符文
        MagicRecipeProperties.manaPerTick(PORecipeMaps.MANA_RUNE_ALTAR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(ModItems.rune, 1, 1))
                .inputs(new ItemStack(ModItems.rune, 1, 5))
                .inputs(new ItemStack(ModItems.rune, 1, 9))
                .inputs(new ItemStack(ModItems.rune, 1, 10))
                .input(OrePrefix.block, PollutionMaterials.aetheric_dark_steel)
                .input(OrePrefix.block, PollutionMaterials.hyperdimensional_silver)
                .input(PollutionMetaItems.CORE_OF_IDEA)
                .input(OrePrefix.frameGt, PollutionMaterials.Terrasteel)
                .output(PollutionMetaItems.WHITE_RUNE)
                .EUt(7680)
                .duration(100), 1000L)
                .buildAndRegister();
        MagicRecipeProperties.manaPerTick(PORecipeMaps.MANA_RUNE_ALTAR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(ModItems.rune, 1, 2))
                .inputs(new ItemStack(ModItems.rune, 1, 6))
                .inputs(new ItemStack(ModItems.rune, 1, 9))
                .inputs(new ItemStack(ModItems.rune, 1, 10))
                .input(OrePrefix.block, PollutionMaterials.blood_of_avernus)
                .input(OrePrefix.block, VoidMetal)
                .input(PollutionMetaItems.CORE_OF_IDEA)
                .input(OrePrefix.frameGt, PollutionMaterials.Terrasteel)
                .output(PollutionMetaItems.BLACK_RUNE)
                .EUt(7680)
                .duration(100), 1000L)
                .buildAndRegister();
        MagicRecipeProperties.manaPerTick(PORecipeMaps.MANA_RUNE_ALTAR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(ModItems.rune, 1, 4))
                .inputs(new ItemStack(ModItems.rune, 1, 8))
                .inputs(new ItemStack(ModItems.rune, 1, 9))
                .inputs(new ItemStack(ModItems.rune, 1, 10))
                .input(OrePrefix.block, PollutionMaterials.iizunamaru_electrum)
                .input(OrePrefix.block, PollutionMaterials.KQGold)
                .input(PollutionMetaItems.CORE_OF_IDEA)
                .input(OrePrefix.frameGt, PollutionMaterials.Terrasteel)
                .output(PollutionMetaItems.STARRY_RUNE)
                .EUt(7680)
                .duration(100), 1000L)
                .buildAndRegister();
    }
    //"puredaisy", "manastar", "endoflame", "hydroangeas", "thermalily", "arcanerose", "munchdew", "entropinnyum", "kekimurus", "gourmaryllis", "narslimmus", "spectrolus", "rafflowsia", "shulk_me_not", "dandelifeon", "jadedAmaranthus", "bellethorn", "dreadthorn", "heiseiDream", "tigerseye", "marimorphosis", "orechid", "orechidIgnem", "fallenKanade", "exoflame", "agricarnation", "hopperhock", "rannuncarpus", "tangleberrie", "jiyuulia", "hyacidus", "medumone", "pollidisiac", "clayconia", "loonium", "daffomill", "vinculotus", "spectranthemum", "bubbell", "solegnolia", "bergamute"
    private static void flower() {

        //白雏菊诱变改造
        BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 8))
                .input(dust, NetherStar)
                .fluidInputs(SterileGrowthMedium.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("puredaisy"), 5000, 0)
                .cleanroom(CleanroomType.CLEANROOM)
                .buildAndRegister();

        //火红莲诱变改造
        BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 0))
                .input(dust, Blaze)
                .fluidInputs(SterileGrowthMedium.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("endoflame"), 5000, 0)
                .cleanroom(CleanroomType.CLEANROOM)
                .buildAndRegister();

        //火红莲诱变改造
        BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 0))
                .input(OrePrefix.dust, PollutionMaterials.llp, 1)
                .fluidInputs(SterileGrowthMedium.getFluid(100))
                .chancedOutput(ItemBlockSpecialFlower.ofType("hydroangeas"), 5000, 0)
                .cleanroom(CleanroomType.CLEANROOM)
                .buildAndRegister();

        //瓶装末地空气
        CANNER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .fluidInputs(LiquidEnderAir.getFluid(1000))
                .input(Items.GLASS_BOTTLE)
                .outputs(new ItemStack(manaResource, 1, 15))
                .cleanroom(CleanroomType.CLEANROOM)
                .buildAndRegister();

        //染料线
        int min;
        for (min = 0; min < 16; ++min) {
            BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(RED_FLOWER)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(Blocks.YELLOW_FLOWER)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(Blocks.BROWN_MUSHROOM)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(Blocks.RED_MUSHROOM)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
                    .cleanroom(CleanroomType.CLEANROOM)
                    .buildAndRegister();

            BACTERIAL_VAT_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                    .fluidInputs(Water.getFluid(500))
                    .input(GTQTMetaItems.ORDINARY_ALGAE)
                    .inputs(new ItemStack(dye, 1, min))
                    .outputs(new ItemStack(petal, 4, min))
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

    private static void manahatch() {
        //魔力仓
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[LV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.SENSOR_LV.getMetaItem(), 2, 232)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[0].getStackForm())
                .duration(100)
                .EUt(VA[LV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[MV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.SENSOR_MV.getMetaItem(), 2, 233)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[1].getStackForm())
                .duration(100)
                .EUt(VA[MV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[HV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.SENSOR_HV.getMetaItem(), 2, 234)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[2].getStackForm())
                .duration(100)
                .EUt(VA[HV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[EV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.SENSOR_EV.getMetaItem(), 2, 235)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[3].getStackForm())
                .duration(100)
                .EUt(VA[EV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[IV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.SENSOR_IV.getMetaItem(), 2, 236)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[4].getStackForm())
                .duration(100)
                .EUt(VA[IV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[LuV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_LuV.getMetaItem(), 2, 237)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[5].getStackForm())
                .duration(100)
                .EUt(VA[LuV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[ZPM].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_ZPM.getMetaItem(), 2, 238)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[6].getStackForm())
                .duration(100)
                .EUt(VA[ZPM])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_UV.getMetaItem(), 2, 239)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[7].getStackForm())
                .duration(100)
                .EUt(VA[UV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UHV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.SENSOR_UHV.getMetaItem(), 2, 240)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[8].getStackForm())
                .duration(100)
                .EUt(VA[UHV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UEV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_UEV.getMetaItem(), 2, 241)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[9].getStackForm())
                .duration(100)
                .EUt(VA[UEV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UIV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_UIV.getMetaItem(), 2, 242)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[10].getStackForm())
                .duration(100)
                .EUt(VA[UIV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[UXV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_UXV.getMetaItem(), 2, 243)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[11].getStackForm())
                .duration(100)
                .EUt(VA[UXV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_INPUT_HATCH[OpV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.SENSOR_OpV.getMetaItem(), 2, 244)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_INPUT_HATCH_1A[12].getStackForm())
                .duration(100)
                .EUt(VA[OpV])
                .buildAndRegister();
        //魔力池仓
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[LV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.EMITTER_LV.getMetaItem(), 2, 217)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[0].getStackForm())
                .duration(100)
                .EUt(VA[LV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[MV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.EMITTER_MV.getMetaItem(), 2, 218)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[1].getStackForm())
                .duration(100)
                .EUt(VA[MV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[HV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.EMITTER_HV.getMetaItem(), 2, 219)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[2].getStackForm())
                .duration(100)
                .EUt(VA[HV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[EV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.EMITTER_EV.getMetaItem(), 2, 220)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[3].getStackForm())
                .duration(100)
                .EUt(VA[EV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[IV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Mansussteel, 2)
                .input(MetaItems.EMITTER_IV.getMetaItem(), 2, 221)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[4].getStackForm())
                .duration(100)
                .EUt(VA[IV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[LuV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_LuV.getMetaItem(), 2, 222)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[5].getStackForm())
                .duration(100)
                .EUt(VA[LuV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[ZPM].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_ZPM.getMetaItem(), 2, 223)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[6].getStackForm())
                .duration(100)
                .EUt(VA[ZPM])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_UV.getMetaItem(), 2, 224)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[7].getStackForm())
                .duration(100)
                .EUt(VA[UV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UHV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.Terrasteel, 2)
                .input(MetaItems.EMITTER_UHV.getMetaItem(), 2, 225)
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[8].getStackForm())
                .duration(100)
                .EUt(VA[UHV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UEV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_UEV.getMetaItem(), 2, 226)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[9].getStackForm())
                .duration(100)
                .EUt(VA[UEV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UIV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_UIV.getMetaItem(), 2, 227)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[10].getStackForm())
                .duration(100)
                .EUt(VA[UIV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[UXV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_UXV.getMetaItem(), 2, 228)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[11].getStackForm())
                .duration(100)
                .EUt(VA[UXV])
                .buildAndRegister();
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.ENERGY_OUTPUT_HATCH[OpV].getStackForm())
                .input(rune, 1, 8)
                .input(OrePrefix.gear, PollutionMaterials.ElvenElementium, 2)
                .input(MetaItems.EMITTER_OpV.getMetaItem(), 2, 229)
                .fluidInputs(PollutionMaterials.whitemansus.getFluid(1000))
                .outputs(PollutionMetaTileEntities.MANA_POOL_OUTPUT_HATCH[12].getStackForm())
                .duration(100)
                .EUt(VA[OpV])
                .buildAndRegister();
    }

}
