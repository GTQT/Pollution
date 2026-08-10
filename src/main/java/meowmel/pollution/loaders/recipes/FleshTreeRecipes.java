package meowmel.pollution.loaders.recipes;

import WayofTime.bloodmagic.core.RegistrarBloodMagic;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.core.registry.OrbRegistry;
import WayofTime.bloodmagic.item.ItemActivationCrystal;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.types.ShardType;
import meowmel.pollution.Pollution;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

/** 血肉之树终局入口配方。 */
public final class FleshTreeRecipes {

    private FleshTreeRecipes() {}

    public static void init() {
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "flesh_tree_sapling"),
                new InfusionRecipe(
                        "INFUSION@2",
                        new ItemStack(PollutionBlocksInit.FLESH_SAPLING),
                        12,
                        new AspectList()
                                .add(Aspect.LIFE, 250)
                                .add(Aspect.DEATH, 250)
                                .add(Aspect.SOUL, 250)
                                .add(Aspect.PLANT, 128)
                                .add(Aspect.MAGIC, 128)
                                .add(Aspect.FLUX, 64),
                        PollutionMetaItems.ASTRAL_NEURAL_BUNDLE.getStackForm(),
                        ItemSlate.SlateType.ETHEREAL.getStack(1),
                        ItemSlate.SlateType.ETHEREAL.getStack(1),
                        new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL),
                        new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL),
                        ShardType.DEMONIC.getStack(1),
                        ShardType.DEMONIC.getStack(1),
                        PollutionMetaItems.INTEGRATECORE.getStackForm(),
                        PollutionMetaItems.INTEGRATECORE.getStackForm(),
                        PollutionMetaItems.STONE_OF_PHILOSOPHER_3.getStackForm(),
                        OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_ARCHMAGE),
                        ItemActivationCrystal.CrystalType.AWAKENED.getStack(1),
                        new ItemStack(ItemsTC.primordialPearl)));
    }
}
