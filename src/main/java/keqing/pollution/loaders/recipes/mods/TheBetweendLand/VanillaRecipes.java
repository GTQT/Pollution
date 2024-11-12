package keqing.pollution.loaders.recipes.mods.TheBetweendLand;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.CommonProxy;
import keqing.gtqtcore.common.block.GTQTMetaBlocks;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS;
import static gregtech.common.items.MetaItems.COMPRESSED_COKE_CLAY;
import static gregtech.common.items.MetaItems.WOODEN_FORM_BRICK;
import static keqing.gtqtcore.api.unification.ore.GTQTOrePrefix.motor_stick;
import static keqing.gtqtcore.common.items.GTQTMetaItems.*;
import static keqing.gtqtcore.common.items.GTQTMetaItems.ZHU_TAN;
import static keqing.pollution.api.recipes.PORecipeMaps.STOVE_RECIPES;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class VanillaRecipes {
    public static void init() {
        initRecipes();
        removeRecipes();
    }
    public static void removeRecipes() {
        //对箱子等交错原版物品的配方魔改
        //杂草木工作台
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:weedwood_workbench"));
        ModHandler.addShapedRecipe("workbench", new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH),
                "AA", "BB",
                'B',"logWood",
                'A', new ItemStack(Items.FLINT));

        //赛摩铜剪刀
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:syrmorite_shears"));
        ModHandler.addShapedRecipe("po_shears", new ItemStack(ItemRegistry.SYRMORITE_SHEARS),
                "PSP", "hRf", "XsX",
                'P', new UnificationEntry(plate, syrmorite),
                'S', new UnificationEntry(screw, syrmorite),
                'R', new UnificationEntry(ring, syrmorite),
                'X', new UnificationEntry(stick, syrmorite));

        //门
        //赛摩铜
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:syrmorite_door_item"));
        ModHandler.addShapedRecipe("syrmorite_door", new ItemStack(ItemRegistry.SYRMORITE_DOOR_ITEM),
                "PTh", "PRS", "PPd",
                'P', new UnificationEntry(OrePrefix.plate, syrmorite),
                'T', new ItemStack(Blocks.IRON_BARS),
                'R', new UnificationEntry(OrePrefix.ring, syrmorite),
                'S', new UnificationEntry(OrePrefix.screw, syrmorite));

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, syrmorite, 4)
                .inputs(new ItemStack(Blocks.IRON_BARS))
                .fluidInputs(syrmorite.getFluid(L / 9))
                .outputs(new ItemStack(ItemRegistry.SYRMORITE_DOOR_ITEM))
                .duration(400).EUt(VA[ULV]).buildAndRegister();

        //硫磺熔炉
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:sulfur_furnace"));
        ModHandler.addShapedRecipe("sulfur_furnace", new ItemStack(BlockRegistry.SULFUR_FURNACE),
                "XXX", "CGC", "XXX",
                'X', "cobblestone",
                'C', new UnificationEntry(dust, Sulfur),
                'G', new UnificationEntry(OrePrefix.gearSmall, PollutionMaterials.syrmorite));

        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:sulfur_furnace_dual"));
        ModHandler.addShapedRecipe("sulfur_furnace_dual", new ItemStack(BlockRegistry.SULFUR_FURNACE_DUAL),
                "XXX", "CGC", "XXX",
                'X', "cobblestone",
                'C', new ItemStack(BlockRegistry.SULFUR_FURNACE),
                'G', new UnificationEntry(OrePrefix.gearSmall, PollutionMaterials.syrmorite));

        //杂草木箱子
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:weedwood_chest"));
        ModHandler.addShapedRecipe("weedwood_chest1", new ItemStack(BlockRegistry.WEEDWOOD_CHEST), "LPL", "PFP", "LPL",
                'L', new ItemStack(BlockRegistry.WEEDWOOD),
                'P', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS),
                'F', new ItemStack(Items.FLINT));

        ModHandler.addShapedRecipe("weedwood_chest2", new ItemStack(BlockRegistry.WEEDWOOD_CHEST), "LPL", "PFP", "LPL",
                'L', new ItemStack(BlockRegistry.LOG_WEEDWOOD),
                'P', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS),
                'F', new ItemStack(Items.FLINT));

        //赛摩铜漏斗
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:syrmorite_hopper"));
        ModHandler.addShapedRecipe("hopper", new ItemStack(BlockRegistry.SYRMORITE_HOPPER, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                "XCX", "XGX", "wXh",
                'X', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite),
                'C', "chestWood",
                'G', new UnificationEntry(OrePrefix.gearSmall, PollutionMaterials.syrmorite));

        //赛摩铜桶
        ModHandler.addShapedRecipe("syrmorite_anvil", new ItemStack(Blocks.ANVIL), "BBB", "SBS", "PBP",
                'B', new UnificationEntry(OrePrefix.block, PollutionMaterials.syrmorite),
                'S', new UnificationEntry(OrePrefix.screw, PollutionMaterials.syrmorite),
                'P', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite));

        //赛摩铜活版门
        ModHandler.addShapedRecipe("syrmorite_iron_trapdoor", new ItemStack(Blocks.IRON_TRAPDOOR), "SPS", "PTP", "sPd",
                'S', new UnificationEntry(OrePrefix.screw, PollutionMaterials.syrmorite),
                'P', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite),
                'T', new ItemStack(Blocks.TRAPDOOR));

        //耐火砖新配方
        ModHandler.addShapedRecipe("casing_primitive_bricks",
                MetaBlocks.METAL_CASING.getItemVariant(PRIMITIVE_BRICKS),
                "BGB", "BCB", "BGB",
                'G', ItemMisc.EnumItemMisc.MUD_BRICK.create(1),
                'B',  new ItemStack(BlockRegistry.MUD_BRICKS, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'C', FluidUtil.getFilledBucket(Materials.Concrete.getFluid(1000)));

        ModHandler.addShapedRecipe("bucket_mud",
                FluidUtil.getFilledBucket(Mud.getFluid(1000)),
                " h ", " C ", " B ",
                'C',  new ItemStack(BlockRegistry.MUD,1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'B', Items.BUCKET);

        ModHandler.addShapedRecipe("po_bricks",
                new ItemStack(Blocks.BRICK_BLOCK),
                "GGG", "GCG", "GGG",
                'G',  new ItemStack(Items.BRICK),
                'C', FluidUtil.getFilledBucket(Mud.getFluid(1000)));

        //熔炉追加配方
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),new ItemStack(Blocks.SAND));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),new ItemStack(Blocks.DIRT));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.PEAT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),new ItemStack(Items.COAL));
        ModHandler.addSmeltingRecipe(new UnificationEntry(ingot,syrmorite), ItemMisc.EnumItemMisc.SYRMORITE_INGOT.create(1));
        ModHandler.addSmeltingRecipe(new UnificationEntry(ingot,octine), new ItemStack(ItemRegistry.OCTINE_INGOT));
        ModHandler.addSmeltingRecipe(FluidUtil.getFilledBucket(Mud.getFluid(1000)), FluidUtil.getFilledBucket(Water.getFluid(1000)));
        ModHandler.addSmeltingRecipe(ItemMisc.EnumItemMisc.LURKER_SKIN.create(1), new ItemStack(Items.LEATHER));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(Items.CLAY_BALL));
        //泥砖以及他的后续处理
        ModHandler.removeFurnaceSmelting(new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        ModHandler.addSmeltingRecipe(ItemMisc.EnumItemMisc.MUD_BRICK.create(1), new ItemStack(Items.BRICK));
        ModHandler.addShapedRecipe("mud_brick_from_clayAndMud", ItemMisc.EnumItemMisc.MUD_BRICK.create(8),
                "XXX", "SYS", "SSS",
                'Y', WOODEN_FORM_BRICK.getStackForm(),
                'X', "sand",
                'S', new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

    }
    public static void initRecipes() {
        //补充矿辞
        OreDictionary.registerOre("cobblestone",new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("cobblestone",new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("cobblestone",new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("cobblestone",new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("sand",new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        OreDictionary.registerOre("stoneCobble",new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("stoneCobble",new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("stoneCobble",new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));
        OreDictionary.registerOre("stoneCobble",new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        //神秘的原石变沙砾 沙砾变沙子
        /*
        ModHandler.addShapedRecipe(true, "PoStone1", new ItemStack(Blocks.GRAVEL),
                " h", " M",
                'M',new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, GTQTMaterials.Phyllite)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        ModHandler.addShapedRecipe(true, "PoStone2", new ItemStack(Blocks.GRAVEL),
                " h", " M",
                'M',new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.PITSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, GTQTMaterials.Gneiss)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        ModHandler.addShapedRecipe(true, "PoStone3", new ItemStack(Blocks.GRAVEL),
                " h", " M",
                'M',new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.LIMESTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, GTQTMaterials.Limestone)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        ModHandler.addShapedRecipe(true, "PoStone4", new ItemStack(Blocks.GRAVEL),
                " h", " M",
                'M',new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.BETWEENSTONE, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, Materials.Stone)
                .duration(100)
                .EUt(16)
                .buildAndRegister();
        */

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
                "   ","MMM", "   ",
                'M', ItemMisc.EnumItemMisc.SWAMP_REED_ROPE.create(1));

        //纸
        ModHandler.addShapedRecipe("po_paper_dust", OreDictUnifier.get(OrePrefix.dust, Materials.Paper, 2), "SSS",
                " m ", 'S',   ItemMisc.EnumItemMisc.DRIED_SWAMP_REED.create(1));

        ModHandler.addShapedRecipe("mud_paper", new ItemStack(Items.PAPER, 2),
                " r ", "SSS", " B ",
                'S', OreDictUnifier.get(OrePrefix.dust, Materials.Paper),
                'B', new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        //
        ModHandler.addShapedRecipe("mud_bu_dirt1", new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                " r ", "SSS", " B ",
                'S', new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'B', new ItemStack(Items.BUCKET));

        ModHandler.addShapedRecipe("mud_bu_dirt2", new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                " r ", "SSS", " B ",
                'S', new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'B', new ItemStack(Items.BUCKET));

        ModHandler.addShapedRecipe("mud_bu_dirt3", new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                " r ", "SSS", " B ",
                'S', new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'B', new ItemStack(Items.BUCKET));
        //粘土砖
        ModHandler.addShapedRecipe("compressed_coke_clay", COMPRESSED_COKE_CLAY.getStackForm(3), "XXX", "SYS", "SSS",
                'Y', WOODEN_FORM_BRICK.getStackForm(), 'X', new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), 'S', new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()));

        //原始化反添加沙子变黏土的配方
        GTQTcoreRecipeMaps.PR_MIX.recipeBuilder()
                .input(Blocks.SAND)
                .fluidInputs(Water.getFluid(1000))
                .output(Blocks.CLAY)
                .duration(100)
                .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(dust, Materials.QuartzSand)
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        //淤泥玻璃净化
        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SILT_GLASS, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.GLASS)
                .fluidInputs(Water.getFluid(1000))
                .fluidOutputs(Mud.getFluid(1000))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.DIRT)
                .fluidInputs(Water.getFluid(1000))
                .fluidOutputs(Mud.getFluid(1000))
                .duration(100)
                .EUt(16)
                .buildAndRegister();
        //淤泥后续
        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(Mud.getFluid(1000))
                .chancedOutput(new ItemStack(Items.CLAY_BALL), 2500, 250)
                .chancedOutput(new ItemStack(Blocks.SAND), 2500, 250)
                .chancedOutput(new ItemStack(Blocks.GRAVEL), 2500, 250)
                .chancedOutput(dust,GTQTMaterials.Limestone, 2500, 250)
                .fluidOutputs(Water.getFluid(1000))
                .duration(20)
                .EUt(30)
                .buildAndRegister();
        //离心配方
        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.SAND)
                .fluidOutputs(Mud.getFluid(1000))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.DIRT)
                .fluidOutputs(Mud.getFluid(1000))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs( new FluidStack(FluidRegistry.SWAMP_WATER, 1000))
                .fluidOutputs(Water.getFluid(1000))
                .fluidOutputs(Mud.getFluid(1000))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        //原始窑炉配方
        List<ItemStack> FuelStacks = new ArrayList<>();
        FuelStacks.add(OreDictUnifier.get(new UnificationEntry(OrePrefix.gem, Materials.Coal)));
        FuelStacks.add(OreDictUnifier.get(new UnificationEntry(OrePrefix.gem, Materials.Charcoal)));
        FuelStacks.add(OreDictUnifier.get(new UnificationEntry(OrePrefix.gem, Materials.Coke)));
        FuelStacks.add(GTQTMetaItems.JIAO_XIAN_REN_ZHANG.getStackForm());
        FuelStacks.add(GTQTMetaItems.NONG_SUO_XIAN_REN_ZHANG.getStackForm());
        FuelStacks.add(GTQTMetaItems.JIAO_TANG_JIAO.getStackForm());
        FuelStacks.add(GTQTMetaItems.NONG_SUO_TANG_JIAO.getStackForm());
        FuelStacks.add(GTQTMetaItems.ZHU_TAN.getStackForm());

        for(ItemStack fuel:FuelStacks) {
            STOVE_RECIPES.recipeBuilder()
                    .inputs(fuel)
                    .inputs(new ItemStack(BlockRegistry.SWAMP_DIRT, 16, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                    .output(Blocks.DIRT,16)
                    .fluidOutputs(Mud.getFluid(2000))
                    .duration(200)
                    .buildAndRegister();

            STOVE_RECIPES.recipeBuilder()
                    .inputs(fuel)
                    .inputs(new ItemStack(BlockRegistry.SILT, 16, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                    .output(Blocks.SAND,16)
                    .fluidOutputs(Mud.getFluid(2000))
                    .duration(200)
                    .buildAndRegister();

            STOVE_RECIPES.recipeBuilder()
                    .inputs(fuel)
                    .inputs(new ItemStack(BlockRegistry.MUD, 16, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                    .output(Blocks.CLAY,16)
                    .fluidOutputs(Mud.getFluid(2000))
                    .duration(200)
                    .buildAndRegister();
        }

    }
}

