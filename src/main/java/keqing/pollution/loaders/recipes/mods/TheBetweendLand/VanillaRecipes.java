package keqing.pollution.loaders.recipes.mods.TheBetweendLand;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.block;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS;
import static keqing.gtqtcore.api.unification.ore.GTQTOrePrefix.motor_stick;
import static keqing.gtqtcore.common.items.GTQTMetaItems.MOLD_MOTOR;
import static keqing.pollution.api.unification.PollutionMaterials.Mud;

public class VanillaRecipes {
    public static void init() {
        initRecipes();
        removeRecipes();
    }
    public static void removeRecipes() {
        //对箱子等交错原版物品的配方魔改
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:syrmorite_hopper"));
        ModHandler.addShapedRecipe("hopper", new ItemStack(BlockRegistry.SYRMORITE_HOPPER, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                "XCX", "XGX", "wXh",
                'X', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite),
                'C', "chestWood",
                'G', new UnificationEntry(OrePrefix.gearSmall, PollutionMaterials.syrmorite));

        /*
        ModHandler.removeRecipeByName(new ResourceLocation("thebetweenlands:syrmorite_door_item"));
        ModHandler.addShapedRecipe("syrmorite_door", new ItemStack(BlockRegistry.SYRMORITE_DOOR, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                "PTh", "PRS", "PPd",
                'P', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite),
                'T', new ItemStack(Blocks.IRON_BARS),
                'R', new UnificationEntry(OrePrefix.ring, PollutionMaterials.syrmorite),
                'S', new UnificationEntry(OrePrefix.screw, PollutionMaterials.syrmorite));
        */

        ModHandler.addShapedRecipe("syrmorite_anvil", new ItemStack(Blocks.ANVIL), "BBB", "SBS", "PBP",
                'B', new UnificationEntry(OrePrefix.block, PollutionMaterials.syrmorite),
                'S', new UnificationEntry(OrePrefix.screw, PollutionMaterials.syrmorite),
                'P', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite));

        ModHandler.addShapedRecipe("syrmorite_iron_trapdoor", new ItemStack(Blocks.IRON_TRAPDOOR), "SPS", "PTP", "sPd",
                'S', new UnificationEntry(OrePrefix.screw, PollutionMaterials.syrmorite),
                'P', new UnificationEntry(OrePrefix.plate, PollutionMaterials.syrmorite),
                'T', new ItemStack(Blocks.TRAPDOOR));

        ModHandler.addShapedRecipe("casing_primitive_bricks",
                MetaBlocks.METAL_CASING.getItemVariant(PRIMITIVE_BRICKS),
                "BGB", "BCB", "BGB",
                'B', MetaItems.FIRECLAY_BRICK.getStackForm(),
                'G',  new ItemStack(BlockRegistry.MUD_BRICKS, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),
                'C', FluidUtil.getFilledBucket(Mud.getFluid(1000)));

        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),new ItemStack(Blocks.SAND));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),new ItemStack(Blocks.DIRT));
        ModHandler.addSmeltingRecipe(new ItemStack(BlockRegistry.PEAT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()),new ItemStack(Items.COAL));

    }
    public static void initRecipes() {
        //神秘的原石变沙砾 沙砾变沙子
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

        ModHandler.addShapedRecipe(true, "PoStone5", new ItemStack(Blocks.GRAVEL),
                " h", " M",
                'M', "cobblestone");

        ModHandler.addShapedRecipe(true, "PoSand", new ItemStack(Blocks.SAND),
                " h", " M",
                'M', Blocks.GRAVEL);

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
                .fluidOutputs(Mud.getFluid(100))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.MUD, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.DIRT)
                .fluidInputs(Water.getFluid(1000))
                .fluidOutputs(Mud.getFluid(100))
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
                .duration(20)
                .EUt(30)
                .buildAndRegister();
        //离心配方
        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SILT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.SAND)
                .fluidOutputs(Mud.getFluid(100))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlockRegistry.SWAMP_DIRT, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()))
                .output(Blocks.DIRT)
                .fluidOutputs(Mud.getFluid(100))
                .duration(100)
                .EUt(16)
                .buildAndRegister();

        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .fluidInputs( new FluidStack(FluidRegistry.SWAMP_WATER, 1000))
                .fluidOutputs(Water.getFluid(1000))
                .fluidOutputs(Mud.getFluid(100))
                .duration(100)
                .EUt(16)
                .buildAndRegister();



    }
}

