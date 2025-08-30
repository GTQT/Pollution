package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtechfoodoption.machines.GTFOTileEntities;
import keqing.gtqtcore.common.metatileentities.GTQTMetaTileEntities;
import keqing.pollution.Pollution;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import keqing.pollution.common.items.PollutionMetaItems;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.unification.ore.OrePrefix.ingotHot;
import static gregtech.common.items.MetaItems.*;
import static keqing.gtqtcore.common.items.GTQTMetaItems.*;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_GREENHOUSE_RECIPES;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.common.items.PollutionMetaItems.*;

public class CircuitManager {
    public static void init() {

        //和磁共振一样就行
        //高级电路=一个低级电路放在中间+四个更低级电路+阶段材料+当前阶段SMD
        //电路板都必须是当前 蕴魔 系列

        //ULV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_ULV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_ULV.getStackForm(),
                2,
                new AspectList().add(Aspect.MECHANISM, 1).add(Aspect.MAGIC, 1).add(Aspect.DESIRE, 1),
                VACUUM_TUBE.getStackForm(),
                CAPACITOR.getStackForm(),
                TRANSISTOR.getStackForm(),
                DIODE.getStackForm()
        ));

        //LV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_LV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_LV.getStackForm(),
                2,
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MAGIC, 2).add(Aspect.DESIRE, 2),
                MAGIC_CIRCUIT_ULV.getStackForm(),
                CAPACITOR.getStackForm(),
                TRANSISTOR.getStackForm(),
                DIODE.getStackForm()
        ));

        //MV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_MV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_MV.getStackForm(),
                2,
                new AspectList().add(Aspect.MECHANISM, 4).add(Aspect.MAGIC,4 ).add(Aspect.DESIRE, 4),
                MAGIC_CIRCUIT_LV.getStackForm(),

                MAGIC_CIRCUIT_ULV.getStackForm(),
                MAGIC_CIRCUIT_ULV.getStackForm(),
                MAGIC_CIRCUIT_ULV.getStackForm(),
                MAGIC_CIRCUIT_ULV.getStackForm(),

                CAPACITOR.getStackForm(),
                TRANSISTOR.getStackForm(),
                DIODE.getStackForm()
        ));

        //HV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_HV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_HV.getStackForm(),
                2,
                new AspectList().add(Aspect.MECHANISM, 8).add(Aspect.MAGIC,8).add(Aspect.DESIRE, 8),
                MAGIC_CIRCUIT_MV.getStackForm(),

                MAGIC_CIRCUIT_LV.getStackForm(),
                MAGIC_CIRCUIT_LV.getStackForm(),
                MAGIC_CIRCUIT_LV.getStackForm(),
                MAGIC_CIRCUIT_LV.getStackForm(),

                CAPACITOR.getStackForm(),
                TRANSISTOR.getStackForm(),
                DIODE.getStackForm()
        ));

        //EV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_EV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_EV.getStackForm(),
                4,
                new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC,16).add(Aspect.DESIRE, 16),
                MAGIC_CIRCUIT_HV.getStackForm(),

                MAGIC_CIRCUIT_MV.getStackForm(),
                MAGIC_CIRCUIT_MV.getStackForm(),
                MAGIC_CIRCUIT_MV.getStackForm(),
                MAGIC_CIRCUIT_MV.getStackForm(),

                SMD_CAPACITOR.getStackForm(),
                SMD_TRANSISTOR.getStackForm(),
                SMD_DIODE.getStackForm()
        ));

        //IV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_IV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_IV.getStackForm(),
                4,
                new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC,16).add(Aspect.DESIRE, 16),
                MAGIC_CIRCUIT_EV.getStackForm(),

                MAGIC_CIRCUIT_HV.getStackForm(),
                MAGIC_CIRCUIT_HV.getStackForm(),
                MAGIC_CIRCUIT_HV.getStackForm(),
                MAGIC_CIRCUIT_HV.getStackForm(),

                SMD_CAPACITOR.getStackForm(),
                SMD_TRANSISTOR.getStackForm(),
                SMD_DIODE.getStackForm()
        ));

        //LuV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_LuV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_LuV.getStackForm(),
                4,
                new AspectList().add(Aspect.MECHANISM, 32).add(Aspect.MAGIC,32).add(Aspect.DESIRE, 32),
                MAGIC_CIRCUIT_IV.getStackForm(),

                MAGIC_CIRCUIT_EV.getStackForm(),
                MAGIC_CIRCUIT_EV.getStackForm(),
                MAGIC_CIRCUIT_EV.getStackForm(),
                MAGIC_CIRCUIT_EV.getStackForm(),

                ADVANCED_SMD_CAPACITOR.getStackForm(),
                ADVANCED_SMD_TRANSISTOR.getStackForm(),
                ADVANCED_SMD_DIODE.getStackForm()
        ));

        //ZPM
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_ZPM.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_ZPM.getStackForm(),
                4,
                new AspectList().add(Aspect.MECHANISM, 32).add(Aspect.MAGIC,32).add(Aspect.DESIRE, 32),
                MAGIC_CIRCUIT_LuV.getStackForm(),

                MAGIC_CIRCUIT_IV.getStackForm(),
                MAGIC_CIRCUIT_IV.getStackForm(),
                MAGIC_CIRCUIT_IV.getStackForm(),
                MAGIC_CIRCUIT_IV.getStackForm(),

                ADVANCED_SMD_CAPACITOR.getStackForm(),
                ADVANCED_SMD_TRANSISTOR.getStackForm(),
                ADVANCED_SMD_DIODE.getStackForm()
        ));

        //UV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_UV.getStackForm(),
                6,
                new AspectList().add(Aspect.MECHANISM, 32).add(Aspect.MAGIC,32).add(Aspect.DESIRE, 32),
                MAGIC_CIRCUIT_ZPM.getStackForm(),

                MAGIC_CIRCUIT_LuV.getStackForm(),
                MAGIC_CIRCUIT_LuV.getStackForm(),
                MAGIC_CIRCUIT_LuV.getStackForm(),
                MAGIC_CIRCUIT_LuV.getStackForm(),

                ADVANCED_SMD_CAPACITOR.getStackForm(),
                ADVANCED_SMD_TRANSISTOR.getStackForm(),
                ADVANCED_SMD_DIODE.getStackForm()
        ));

        //UHV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UHV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_UHV.getStackForm(),
                6,
                new AspectList().add(Aspect.MECHANISM, 96).add(Aspect.MAGIC,96).add(Aspect.DESIRE, 96),
                MAGIC_CIRCUIT_UV.getStackForm(),

                MAGIC_CIRCUIT_ZPM.getStackForm(),
                MAGIC_CIRCUIT_ZPM.getStackForm(),
                MAGIC_CIRCUIT_ZPM.getStackForm(),
                MAGIC_CIRCUIT_ZPM.getStackForm(),

                GOOWARE_SMD_CAPACITOR.getStackForm(),
                GOOWARE_SMD_TRANSISTOR.getStackForm(),
                GOOWARE_SMD_DIODE.getStackForm()
        ));

        //UEV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UEV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_UEV.getStackForm(),
                6,
                new AspectList().add(Aspect.MECHANISM, 96).add(Aspect.MAGIC,96).add(Aspect.DESIRE, 96),
                MAGIC_CIRCUIT_UHV.getStackForm(),

                MAGIC_CIRCUIT_UV.getStackForm(),
                MAGIC_CIRCUIT_UV.getStackForm(),
                MAGIC_CIRCUIT_UV.getStackForm(),
                MAGIC_CIRCUIT_UV.getStackForm(),

                OPTICAL_SMD_CAPACITOR.getStackForm(),
                OPTICAL_SMD_TRANSISTOR.getStackForm(),
                OPTICAL_SMD_DIODE.getStackForm()
        ));

        //UIV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UIV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_UIV.getStackForm(),
                6,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                MAGIC_CIRCUIT_UEV.getStackForm(),

                MAGIC_CIRCUIT_UHV.getStackForm(),
                MAGIC_CIRCUIT_UHV.getStackForm(),
                MAGIC_CIRCUIT_UHV.getStackForm(),
                MAGIC_CIRCUIT_UHV.getStackForm(),

                SPINTRONIC_SMD_CAPACITOR.getStackForm(),
                SPINTRONIC_SMD_TRANSISTOR.getStackForm(),
                SPINTRONIC_SMD_DIODE.getStackForm()
        ));

        //UXV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UXV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_UXV.getStackForm(),
                8,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                MAGIC_CIRCUIT_UIV.getStackForm(),

                MAGIC_CIRCUIT_UEV.getStackForm(),
                MAGIC_CIRCUIT_UEV.getStackForm(),
                MAGIC_CIRCUIT_UEV.getStackForm(),
                MAGIC_CIRCUIT_UEV.getStackForm(),

                COSMIC_SMD_CAPACITOR.getStackForm(),
                COSMIC_SMD_TRANSISTOR.getStackForm(),
                COSMIC_SMD_DIODE.getStackForm()
        ));

        //OpV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_OpV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_OpV.getStackForm(),
                8,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                MAGIC_CIRCUIT_UXV.getStackForm(),

                MAGIC_CIRCUIT_UIV.getStackForm(),
                MAGIC_CIRCUIT_UIV.getStackForm(),
                MAGIC_CIRCUIT_UIV.getStackForm(),
                MAGIC_CIRCUIT_UIV.getStackForm(),

                SUPRACAUSAL_SMD_CAPACITOR.getStackForm(),
                SUPRACAUSAL_SMD_TRANSISTOR.getStackForm(),
                SUPRACAUSAL_SMD_DIODE.getStackForm()
        ));

        //MAX
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_MAX.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                MAGIC_CIRCUIT_MAX.getStackForm(),
                8,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                MAGIC_CIRCUIT_OpV.getStackForm(),

                MAGIC_CIRCUIT_UXV.getStackForm(),
                MAGIC_CIRCUIT_UXV.getStackForm(),
                MAGIC_CIRCUIT_UXV.getStackForm(),
                MAGIC_CIRCUIT_UXV.getStackForm(),

                SUPRACAUSAL_SMD_CAPACITOR.getStackForm(),
                SUPRACAUSAL_SMD_TRANSISTOR.getStackForm(),
                SUPRACAUSAL_SMD_DIODE.getStackForm()
        ));
    }

}
