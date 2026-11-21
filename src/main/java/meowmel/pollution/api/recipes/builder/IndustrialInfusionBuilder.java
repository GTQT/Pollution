package meowmel.pollution.api.recipes.builder;

import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import gregtech.api.unification.material.Material;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.api.utils.POAspectToGtFluidList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeSet;

import static gregtech.api.GTValues.HV;
import static gregtech.api.GTValues.VA;
import static meowmel.pollution.api.recipes.PORecipeMaps.INDUSTRIAL_INFUSION_RECIPES;

public class IndustrialInfusionBuilder {
    private static final int MAX_ITEM_AMOUNT = 64;
    private static final int MILLIBUCKETS_PER_ASPECT = 144;
    private static final int TICKS_PER_ASPECT = 5;
    private static final Comparator<ItemStack> ALTERNATIVE_ORDER = Comparator
            .comparing((ItemStack stack) -> String.valueOf(stack.getItem().getRegistryName()))
            .thenComparingInt(ItemStack::getMetadata)
            .thenComparing(stack -> nbtSortKey(stack.getTagCompound()));

    public static void init() {
        ThaumcraftApi.getCraftingRecipes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.comparing(ResourceLocation::toString))))
                .forEachOrdered(entry -> {
                    if (entry.getValue() instanceof InfusionRecipe infusion) {
                        ResourceLocation id = entry.getKey();
                        if (id == null) {
                            Pollution.LOGGER.warn("Skipping industrial infusion: missing recipe ID");
                        } else if (infusion.recipeOutput instanceof ItemStack output && !output.isEmpty()) {
                            register(id, infusion, output);
                        } else {
                            Pollution.LOGGER.warn("Skipping industrial infusion {}: no fixed item output", id);
                        }
                    }
                });
    }

    private static String nbtSortKey(NBTBase tag) {
        if (tag instanceof NBTTagCompound compound) {
            StringJoiner entries = new StringJoiner(",", "{", "}");
            for (String key : new TreeSet<>(compound.getKeySet())) {
                entries.add(NBTTagString.quoteAndEscape(key) + ":" + nbtSortKey(compound.getTag(key)));
            }
            return entries.toString();
        }
        if (tag instanceof NBTTagList list) {
            StringJoiner entries = new StringJoiner(",", "[", "]");
            for (int i = 0; i < list.tagCount(); i++) {
                entries.add(nbtSortKey(list.get(i)));
            }
            return entries.toString();
        }
        return tag == null ? "" : tag.toString();
    }

    private static void register(ResourceLocation id, InfusionRecipe infusion, ItemStack output) {
        if (output.getCount() > MAX_ITEM_AMOUNT) {
            Pollution.LOGGER.error("Skipping industrial infusion {}: output {} count {} exceeds limit {}",
                    id, output.getItem().getRegistryName(), output.getCount(), MAX_ITEM_AMOUNT);
            return;
        }

        List<Ingredient> components = infusion.getComponents();
        if (components == null) {
            Pollution.LOGGER.warn("Skipping industrial infusion {}: missing components", id);
            return;
        }
        List<Ingredient> ingredients = new ArrayList<>(components.size() + 1);
        ingredients.add(infusion.getRecipeInput());
        ingredients.addAll(components);
        Map<GTRecipeItemInput, Integer> items = new LinkedHashMap<>();
        for (int slot = 0; slot < ingredients.size(); slot++) {
            Ingredient ingredient = ingredients.get(slot);
            ItemStack[] matches = ingredient == null ? null : ingredient.getMatchingStacks();
            if (matches == null || Arrays.asList(matches).contains(null)) {
                Pollution.LOGGER.warn("Skipping industrial infusion {}: ingredient {} has missing item matches (0 is the center)",
                        id, slot);
                return;
            }
            ItemStack[] alternatives = Arrays.stream(matches)
                    .filter(stack -> !stack.isEmpty())
                    .sorted(ALTERNATIVE_ORDER)
                    .toArray(ItemStack[]::new);
            if (alternatives.length == 0) {
                Pollution.LOGGER.warn("Skipping industrial infusion {}: ingredient {} has no matching items (0 is the center)",
                        id, slot);
                return;
            }
            // The original altar consumes one item from each pedestal.
            items.merge(new GTRecipeItemInput(alternatives, 1), 1, Integer::sum);
        }
        int itemLimit = INDUSTRIAL_INFUSION_RECIPES.getMaxInputs();
        if (items.size() > itemLimit) {
            Pollution.LOGGER.error("Skipping industrial infusion {}: item input types {} exceed limit {}",
                    id, items.size(), itemLimit);
            return;
        }
        for (var input : items.entrySet()) {
            if (input.getValue() > MAX_ITEM_AMOUNT) {
                Pollution.LOGGER.error("Skipping industrial infusion {}: item input {} count {} exceeds limit {}",
                        id, input.getKey(), input.getValue(), MAX_ITEM_AMOUNT);
                return;
            }
        }

        Map<Fluid, Long> fluids = new LinkedHashMap<>();
        AspectList aspects = infusion.getAspects();
        Aspect[] requiredAspects = aspects == null ? null : aspects.getAspects();
        if (requiredAspects == null) {
            Pollution.LOGGER.warn("Skipping industrial infusion {}: missing aspects", id);
            return;
        }
        long duration = 0;
        for (Aspect aspect : requiredAspects) {
            if (aspect == null) {
                Pollution.LOGGER.warn("Skipping industrial infusion {}: missing aspect in aspect list", id);
                return;
            }
            Material material = POAspectToGtFluidList.aspectToGtFluidList.get(aspect);
            Fluid fluid = material != null && material.hasFluid() ? material.getFluid() : null;
            if (fluid == null) {
                Pollution.LOGGER.warn("Skipping industrial infusion {}: no fluid for aspect {}", id, aspect.getTag());
                return;
            }
            int amount = aspects.getAmount(aspect);
            if (amount <= 0) {
                Pollution.LOGGER.error("Skipping industrial infusion {}: aspect {} has invalid amount {}",
                        id, aspect.getTag(), amount);
                return;
            }
            fluids.merge(fluid, (long) amount * MILLIBUCKETS_PER_ASPECT, Long::sum);
            duration += (long) amount * TICKS_PER_ASPECT;
        }

        int fluidLimit = INDUSTRIAL_INFUSION_RECIPES.getMaxFluidInputs();
        if (fluids.size() > fluidLimit) {
            Pollution.LOGGER.error("Skipping industrial infusion {}: fluid input types {} exceed limit {}",
                    id, fluids.size(), fluidLimit);
            return;
        }
        for (var input : fluids.entrySet()) {
            if (input.getValue() > Integer.MAX_VALUE) {
                Pollution.LOGGER.error("Skipping industrial infusion {}: fluid {} amount {} exceeds limit {}",
                        id, input.getKey().getName(), input.getValue(), Integer.MAX_VALUE);
                return;
            }
        }
        if (duration <= 0 || duration > Integer.MAX_VALUE) {
            Pollution.LOGGER.error("Skipping industrial infusion {}: invalid duration {} ticks", id, duration);
            return;
        }

        var builder = INDUSTRIAL_INFUSION_RECIPES.recipeBuilder();
        // Match narrower choices first when a fixed ingredient is also a valid alternative.
        items.entrySet().stream()
                .sorted(Comparator.comparingInt(input -> input.getKey().getInputStacks().length))
                .forEach(input -> builder.input(input.getKey().copyWithAmount(input.getValue())));
        fluids.forEach((fluid, amount) -> builder.fluidInputs(new FluidStack(fluid, amount.intValue())));
        MagicRecipeProperties.thaumcraftResearch(builder, infusion.getResearch());
        builder.outputs(output.copy()).duration((int) duration).EUt(VA[HV]).buildAndRegister();
    }
}
