package keqing.pollution.loaders.recipes.mods.TheBetweendLand;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.api.unification.ore.GTQTOrePrefix;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.Pollution;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS;
import static gregtech.common.items.MetaItems.COMPRESSED_COKE_CLAY;
import static gregtech.common.items.MetaItems.WOODEN_FORM_BRICK;
import static keqing.pollution.api.recipes.PORecipeMaps.STOVE_RECIPES;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class VanillaRecipes {
    public static void init() {
        miscRecipes();
        initRecipes();
        removeRecipes();
    }
    public static ItemStack createCrystalEssenceWithAspects(String string) {
        // 创建ItemStack
        ItemStack crystalEssence = new ItemStack(ItemsTC.crystalEssence);

        // 创建NBTTagCompound来存储NBT数据
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        // 创建NBTTagList来存储Aspects
        NBTTagList aspectsTagList = new NBTTagList();

        // 创建一个NBTTagCompound来存储单个Aspect的数据
        NBTTagCompound aspectTagCompound = new NBTTagCompound();
        aspectTagCompound.setString("key",string);
        aspectTagCompound.setInteger("amount", 1);

        // 将Aspect的NBTTagCompound添加到NBTTagList中
        aspectsTagList.appendTag(aspectTagCompound);

        // 将NBTTagList添加到NBTTagCompound中
        nbtTagCompound.setTag("Aspects", aspectsTagList);

        // 将NBTTagCompound设置到ItemStack中
        crystalEssence.setTagCompound(nbtTagCompound);

        return crystalEssence;
    }
    private static void miscRecipes() {
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:swamp_talisman"));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(ItemRegistry.LIFE_CRYSTAL)
                .inputs(createCrystalEssenceWithAspects("aer"))
                .inputs(createCrystalEssenceWithAspects("terra"))
                .inputs(createCrystalEssenceWithAspects("ignis"))
                .inputs(createCrystalEssenceWithAspects("aqua"))
                .inputs(createCrystalEssenceWithAspects("ordo"))
                .output(ItemRegistry.SWAMP_TALISMAN)
                .EUt(30)
                .duration(300)
                .buildAndRegister();

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "swamp_talisman"), new InfusionRecipe(
                "",
                new ItemStack(ItemRegistry.SWAMP_TALISMAN),
                2,
                new AspectList(),
                new ItemStack(ItemRegistry.LIFE_CRYSTAL),
                createCrystalEssenceWithAspects("aer"),
                createCrystalEssenceWithAspects("terra"),
                createCrystalEssenceWithAspects("ignis"),
                createCrystalEssenceWithAspects("aqua"),
                createCrystalEssenceWithAspects("ordo")
        ));




        //空杀虫剂
        ModHandler.addShapedRecipe("pesticide_empty", new ItemStack(PollutionMetaItems.PESTICIDE_EMPTY.getMetaItem(), 1, 201),
                "hHf", "XRX", "XSX",
                'H', new UnificationEntry(ring, octine),
                'S', new UnificationEntry(plate, octine),
                'R', new UnificationEntry(springSmall, Iron),
                'X', new UnificationEntry(GTQTOrePrefix.plate_curved, Iron));
        //杀虫剂
        ModHandler.addShapedRecipe("pesticide", new ItemStack(PollutionMetaItems.PESTICIDE.getMetaItem(), 1, 202),
                "CAA", "BB ", "   ",
                'C', new ItemStack(PollutionMetaItems.PESTICIDE_EMPTY.getMetaItem(), 1, 201),
                'A', new UnificationEntry(dust, Sulfur),
                'B', new UnificationEntry(dust, infused_fire));
        //理智恢复药（1）
        ModHandler.addShapedRecipe("devay_pill_1", new ItemStack(PollutionMetaItems.DEVAY_PILL_1.getMetaItem(), 1, 204),
                "AB ", "CD ", "   ",
                'A', new ItemStack(ItemRegistry.SAP_BALL),
                'B', new ItemStack(ItemRegistry.NIBBLESTICK),
                'C', new ItemStack(ItemRegistry.BLACK_HAT_MUSHROOM_ITEM),
                'D', new ItemStack(ItemRegistry.ITEMS_MISC, 1, 18));
        //理智恢复药（空）
        ModHandler.addShapedRecipe("devay_pill_empty", new ItemStack(PollutionMetaItems.DEVAY_PILL_EMPTY.getMetaItem(), 1, 203),
                "XhX", "X X", "XfX",
                'X', new UnificationEntry(GTQTOrePrefix.plate_curved, Steel));
        //剩下三种
        ModHandler.addShapedRecipe("devay_pill_5", new ItemStack(PollutionMetaItems.DEVAY_PILL_5.getMetaItem(), 1, 205),
                "EAA", "AAB", "   ",
                'A', new ItemStack(ItemRegistry.SAP_BALL),
                'B', new ItemStack(ItemRegistry.ITEMS_MISC, 1, 18),
                'E', new ItemStack(PollutionMetaItems.DEVAY_PILL_EMPTY.getMetaItem(), 1, 203));
        ModHandler.addShapedRecipe("devay_pill_10", new ItemStack(PollutionMetaItems.DEVAY_PILL_10.getMetaItem(), 1, 206),
                "EAA", "AAB", "C  ",
                'A', new ItemStack(ItemRegistry.SAP_BALL),
                'B', new ItemStack(ItemRegistry.ITEMS_MISC, 1, 18),
                'C', new UnificationEntry(dust, infused_water),
                'E', new ItemStack(PollutionMetaItems.DEVAY_PILL_EMPTY.getMetaItem(), 1, 203));
        ModHandler.addShapedRecipe("devay_pill_20", new ItemStack(PollutionMetaItems.DEVAY_PILL_20.getMetaItem(), 1, 207),
                "EAA", "AAB", "CD ",
                'A', new ItemStack(ItemRegistry.SAP_BALL),
                'B', new ItemStack(ItemRegistry.ITEMS_MISC, 1, 18),
                'C', new UnificationEntry(dust, infused_water),
                'D', new ItemStack(ItemRegistry.PEARLED_PEAR),
                'E', new ItemStack(PollutionMetaItems.DEVAY_PILL_EMPTY.getMetaItem(), 1, 203));
    }

    public static void removeRecipes() {
        //对箱子等交错原版物品的配方魔改
        //杂草木工作台
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:weedwood_workbench"));
        ModHandler.addShapedRecipe("workbench", new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH),
                "AA", "BB",
                'B', "logWood",
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
                'B', new ItemStack(BlockRegistry.MUD_BRICKS, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'C', FluidUtil.getFilledBucket(Materials.Concrete.getFluid(1000)));

        ModHandler.addShapedRecipe("bucket_mud",
                FluidUtil.getFilledBucket(Mud.getFluid(1000)),
                " h ", " C ", " B ",
                'C', new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'B', Items.BUCKET);

        ModHandler.addShapedRecipe("po_bricks",
                new ItemStack(Blocks.BRICK_BLOCK),
                "GGG", "GCG", "GGG",
                'G', new ItemStack(Items.BRICK),
                'C', FluidUtil.getFilledBucket(Mud.getFluid(1000)));

        //熔炉追加配方
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(Blocks.SAND));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(Blocks.DIRT));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.PEAT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(Items.COAL));
        ModHandler.addSmeltingRecipe(new UnificationEntry(ingot, syrmorite), ItemMisc.EnumItemMisc.SYRMORITE_INGOT.create(1));
        ModHandler.addSmeltingRecipe(new UnificationEntry(ingot, octine), new ItemStack(ItemRegistry.OCTINE_INGOT));
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
        //纸
        ModHandler.addShapedRecipe("po_paper_dust", OreDictUnifier.get(OrePrefix.dust, Materials.Paper, 2), "SSS",
                " m ", 'S', ItemMisc.EnumItemMisc.DRIED_SWAMP_REED.create(1));

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
        //硫磺粉碎
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .input(ItemMisc.EnumItemMisc.SULFUR.create(1).getItem())
                .output(dust, Sulfur)
                .duration(20)
                .EUt(7)
                .buildAndRegister();
        //淤泥后续
        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs(Mud.getFluid(1000))
                .chancedOutput(new ItemStack(Items.CLAY_BALL), 2500, 250)
                .chancedOutput(new ItemStack(Blocks.SAND), 2500, 250)
                .chancedOutput(new ItemStack(Blocks.GRAVEL), 2500, 250)
                .chancedOutput(dust, GTQTMaterials.Limestone, 2500, 250)
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
                .fluidInputs(new FluidStack(FluidRegistry.SWAMP_WATER, 1000))
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

        for (ItemStack fuel : FuelStacks) {
            STOVE_RECIPES.recipeBuilder()
                    .inputs(fuel)
                    .inputs(new ItemStack(BlockRegistry.SWAMP_DIRT, 16, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                    .output(Blocks.DIRT, 16)
                    .fluidOutputs(Mud.getFluid(2000))
                    .duration(200)
                    .buildAndRegister();

            STOVE_RECIPES.recipeBuilder()
                    .inputs(fuel)
                    .inputs(new ItemStack(BlockRegistry.SILT, 16, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                    .output(Blocks.SAND, 16)
                    .fluidOutputs(Mud.getFluid(2000))
                    .duration(200)
                    .buildAndRegister();

            STOVE_RECIPES.recipeBuilder()
                    .inputs(fuel)
                    .inputs(new ItemStack(BlockRegistry.MUD, 16, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                    .output(Blocks.CLAY, 16)
                    .fluidOutputs(Mud.getFluid(2000))
                    .duration(200)
                    .buildAndRegister();


        }
    }
}

