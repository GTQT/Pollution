package keqing.pollution.loaders.recipes.mods.TheBetweendLand;

import gregtech.api.recipes.RecipeMaps;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

import static gregtech.api.unification.material.Materials.*;
import static thebetweenlands.common.registries.BlockRegistry.*;

public class BiologyLine {
    public static void init() {
        List<Block> Crop = new ArrayList<>();
        Crop.add(PITCHER_PLANT);
        Crop.add(WEEPING_BLUE);
        Crop.add(SUNDEW);
        Crop.add(BLACK_HAT_MUSHROOM);
        Crop.add(BULB_CAPPED_MUSHROOM);
        Crop.add(FLAT_HEAD_MUSHROOM);
        Crop.add(VENUS_FLY_TRAP);
        Crop.add(VOLARPAD);
        Crop.add(SWAMP_PLANT);
        Crop.add(SWAMP_KELP);
        Crop.add(MIRE_CORAL);
        Crop.add(DEEP_WATER_CORAL);
        Crop.add(WATER_WEEDS);
        Crop.add(ALGAE);
        Crop.add(POISON_IVY);
        Crop.add(ROOT);
        Crop.add(GIANT_ROOT);
        Crop.add(ARROW_ARUM);
        Crop.add(BLUE_EYED_GRASS);
        Crop.add(BLUE_IRIS);
        Crop.add(BONESET);
        Crop.add(BOTTLE_BRUSH_GRASS);
        Crop.add(BROOMSEDGE);
        Crop.add(BUTTON_BUSH);
        Crop.add(CARDINAL_FLOWER);
        Crop.add(CATTAIL);
        Crop.add(CAVE_GRASS);
        Crop.add(COPPER_IRIS);
        Crop.add(MARSH_HIBISCUS);
        Crop.add(MARSH_MALLOW);
        Crop.add(BLADDERWORT_FLOWER);
        Crop.add(BLADDERWORT_STALK);
        Crop.add(BOG_BEAN_FLOWER);
        Crop.add(BOG_BEAN_STALK);
        Crop.add(GOLDEN_CLUB_FLOWER);
        Crop.add(GOLDEN_CLUB_STALK);
        Crop.add(MARSH_MARIGOLD_FLOWER);
        Crop.add(MARSH_MARIGOLD_STALK);
        Crop.add(SWAMP_DOUBLE_TALLGRASS);
        Crop.add(MILKWEED);
        Crop.add(NETTLE);
        Crop.add(NETTLE_FLOWERED);
        Crop.add(PICKEREL_WEED);
        Crop.add(PHRAGMITES);
        Crop.add(SHOOTS);
        Crop.add(SLUDGECREEP);
        Crop.add(TALL_SLUDGECREEP);
        Crop.add(SOFT_RUSH);
        Crop.add(SWAMP_REED);
        Crop.add(SWAMP_REED_UNDERWATER);
        Crop.add(THORNS);
        Crop.add(TALL_CATTAIL);
        Crop.add(SWAMP_TALLGRASS);
        Crop.add(DEAD_WEEDWOOD_BUSH);
        Crop.add(WEEDWOOD_BUSH);
        Crop.add(NESTING_BLOCK_STICKS);
        Crop.add(NESTING_BLOCK_BONES);
        Crop.add(HOLLOW_LOG);
        Crop.add(CAVE_MOSS);
        Crop.add(CRYPTWEED);
        Crop.add(STRING_ROOTS);
        Crop.add(PALE_GRASS);
        Crop.add(ROTBULB);
        Crop.add(MOSS);
        Crop.add(DEAD_MOSS);
        Crop.add(LICHEN);
        Crop.add(DEAD_LICHEN);
        Crop.add(HANGER);

        for (Block block : Crop){
            RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                    .input(block)
                    .fluidOutputs(SeedOil.getFluid(10))
                    .EUt(2)
                    .duration(32)
                    .buildAndRegister();

            RecipeMaps.BREWING_RECIPES.recipeBuilder()
                    .input(block)
                    .fluidInputs(Water.getFluid(20))
                    .fluidOutputs(Biomass.getFluid(20))
                    .EUt(3)
                    .duration(160)
                    .buildAndRegister();
        }
    }
}
