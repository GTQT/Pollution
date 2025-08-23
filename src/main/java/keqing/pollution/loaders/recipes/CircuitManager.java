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
                new ItemStack(MAGIC_CIRCUIT_ULV.getMetaItem(), 1, 50),
                2,
                new AspectList().add(Aspect.MECHANISM, 1).add(Aspect.MAGIC, 1).add(Aspect.DESIRE, 1),
                new ItemStack(VACUUM_TUBE.getMetaItem(), 1, 516),
                new ItemStack(CAPACITOR.getMetaItem(), 1, 520),
                new ItemStack(TRANSISTOR.getMetaItem(),1, 518),
                new ItemStack(DIODE.getMetaItem(),1, 521)
        ));

        //LV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_LV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_LV.getMetaItem(), 1, 51),
                2,
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MAGIC, 2).add(Aspect.DESIRE, 2),
                new ItemStack(MAGIC_CIRCUIT_ULV.getMetaItem(), 1, 50),
                new ItemStack(VACUUM_TUBE.getMetaItem(), 1, 516),
                new ItemStack(CAPACITOR.getMetaItem(), 1, 520),
                new ItemStack(TRANSISTOR.getMetaItem(),1, 518),
                new ItemStack(DIODE.getMetaItem(),1, 521)
        ));

        //MV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_MV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_MV.getMetaItem(), 1, 52),
                2,
                new AspectList().add(Aspect.MECHANISM, 4).add(Aspect.MAGIC,4 ).add(Aspect.DESIRE, 4),
                new ItemStack(MAGIC_CIRCUIT_LV.getMetaItem(), 1, 51),

                new ItemStack(MAGIC_CIRCUIT_ULV.getMetaItem(), 1, 50),
                new ItemStack(MAGIC_CIRCUIT_ULV.getMetaItem(), 1, 50),
                new ItemStack(MAGIC_CIRCUIT_ULV.getMetaItem(), 1, 50),
                new ItemStack(MAGIC_CIRCUIT_ULV.getMetaItem(), 1, 50),

                new ItemStack(CAPACITOR.getMetaItem(), 1, 520),
                new ItemStack(TRANSISTOR.getMetaItem(),1, 518),
                new ItemStack(DIODE.getMetaItem(),1, 521)
        ));

        //HV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_HV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_HV.getMetaItem(), 1, 53),
                2,
                new AspectList().add(Aspect.MECHANISM, 8).add(Aspect.MAGIC,8).add(Aspect.DESIRE, 8),
                new ItemStack(MAGIC_CIRCUIT_MV.getMetaItem(), 1, 52),

                new ItemStack(MAGIC_CIRCUIT_LV.getMetaItem(), 1, 51),
                new ItemStack(MAGIC_CIRCUIT_LV.getMetaItem(), 1, 51),
                new ItemStack(MAGIC_CIRCUIT_LV.getMetaItem(), 1, 51),
                new ItemStack(MAGIC_CIRCUIT_LV.getMetaItem(), 1, 51),

                new ItemStack(CAPACITOR.getMetaItem(), 1, 520),
                new ItemStack(TRANSISTOR.getMetaItem(),1, 518),
                new ItemStack(DIODE.getMetaItem(),1, 521)
        ));

        //EV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_EV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_EV.getMetaItem(), 1, 54),
                4,
                new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC,16).add(Aspect.DESIRE, 16),
                new ItemStack(MAGIC_CIRCUIT_HV.getMetaItem(), 1, 53),

                new ItemStack(MAGIC_CIRCUIT_MV.getMetaItem(), 1, 52),
                new ItemStack(MAGIC_CIRCUIT_MV.getMetaItem(), 1, 52),
                new ItemStack(MAGIC_CIRCUIT_MV.getMetaItem(), 1, 52),
                new ItemStack(MAGIC_CIRCUIT_MV.getMetaItem(), 1, 52),

                new ItemStack(SMD_CAPACITOR.getMetaItem(), 1, 525),
                new ItemStack(SMD_TRANSISTOR.getMetaItem(),1, 523),
                new ItemStack(SMD_DIODE.getMetaItem(),1, 526)
        ));

        //IV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_IV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_IV.getMetaItem(), 1, 55),
                4,
                new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC,16).add(Aspect.DESIRE, 16),
                new ItemStack(MAGIC_CIRCUIT_EV.getMetaItem(), 1, 54),

                new ItemStack(MAGIC_CIRCUIT_HV.getMetaItem(), 1, 53),
                new ItemStack(MAGIC_CIRCUIT_HV.getMetaItem(), 1, 53),
                new ItemStack(MAGIC_CIRCUIT_HV.getMetaItem(), 1, 53),
                new ItemStack(MAGIC_CIRCUIT_HV.getMetaItem(), 1, 53),

                new ItemStack(SMD_CAPACITOR.getMetaItem(), 1, 525),
                new ItemStack(SMD_TRANSISTOR.getMetaItem(),1, 523),
                new ItemStack(SMD_DIODE.getMetaItem(),1, 526)
        ));

        //LuV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_LuV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_LuV.getMetaItem(), 1, 56),
                4,
                new AspectList().add(Aspect.MECHANISM, 32).add(Aspect.MAGIC,32).add(Aspect.DESIRE, 32),
                new ItemStack(MAGIC_CIRCUIT_IV.getMetaItem(), 1, 55),

                new ItemStack(MAGIC_CIRCUIT_EV.getMetaItem(), 1, 54),
                new ItemStack(MAGIC_CIRCUIT_EV.getMetaItem(), 1, 54),
                new ItemStack(MAGIC_CIRCUIT_EV.getMetaItem(), 1, 54),
                new ItemStack(MAGIC_CIRCUIT_EV.getMetaItem(), 1, 54),

                new ItemStack(ADVANCED_SMD_CAPACITOR.getMetaItem(), 1, 530),
                new ItemStack(ADVANCED_SMD_TRANSISTOR.getMetaItem(),1, 528),
                new ItemStack(ADVANCED_SMD_DIODE.getMetaItem(),1, 531)
        ));

        //ZPM
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_ZPM.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_ZPM.getMetaItem(), 1, 57),
                4,
                new AspectList().add(Aspect.MECHANISM, 32).add(Aspect.MAGIC,32).add(Aspect.DESIRE, 32),
                new ItemStack(MAGIC_CIRCUIT_LuV.getMetaItem(), 1, 56),

                new ItemStack(MAGIC_CIRCUIT_IV.getMetaItem(), 1, 55),
                new ItemStack(MAGIC_CIRCUIT_IV.getMetaItem(), 1, 55),
                new ItemStack(MAGIC_CIRCUIT_IV.getMetaItem(), 1, 55),
                new ItemStack(MAGIC_CIRCUIT_IV.getMetaItem(), 1, 55),

                new ItemStack(ADVANCED_SMD_CAPACITOR.getMetaItem(), 1, 530),
                new ItemStack(ADVANCED_SMD_TRANSISTOR.getMetaItem(),1, 528),
                new ItemStack(ADVANCED_SMD_DIODE.getMetaItem(),1, 531)
        ));

        //UV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_UV.getMetaItem(), 1, 58),
                6,
                new AspectList().add(Aspect.MECHANISM, 32).add(Aspect.MAGIC,32).add(Aspect.DESIRE, 32),
                new ItemStack(MAGIC_CIRCUIT_ZPM.getMetaItem(), 1, 57),

                new ItemStack(MAGIC_CIRCUIT_LuV.getMetaItem(), 1, 56),
                new ItemStack(MAGIC_CIRCUIT_LuV.getMetaItem(), 1, 56),
                new ItemStack(MAGIC_CIRCUIT_LuV.getMetaItem(), 1, 56),
                new ItemStack(MAGIC_CIRCUIT_LuV.getMetaItem(), 1, 56),

                new ItemStack(ADVANCED_SMD_CAPACITOR.getMetaItem(), 1, 530),
                new ItemStack(ADVANCED_SMD_TRANSISTOR.getMetaItem(),1, 528),
                new ItemStack(ADVANCED_SMD_DIODE.getMetaItem(),1, 531)
        ));

        //UHV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UHV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_UHV.getMetaItem(), 1, 59),
                6,
                new AspectList().add(Aspect.MECHANISM, 96).add(Aspect.MAGIC,96).add(Aspect.DESIRE, 96),
                new ItemStack(MAGIC_CIRCUIT_UV.getMetaItem(), 1, 58),

                new ItemStack(MAGIC_CIRCUIT_ZPM.getMetaItem(), 1, 57),
                new ItemStack(MAGIC_CIRCUIT_ZPM.getMetaItem(), 1, 57),
                new ItemStack(MAGIC_CIRCUIT_ZPM.getMetaItem(), 1, 57),
                new ItemStack(MAGIC_CIRCUIT_ZPM.getMetaItem(), 1, 57),

                new ItemStack(GOOWARE_SMD_CAPACITOR.getMetaItem(), 1, 900),
                new ItemStack(GOOWARE_SMD_TRANSISTOR.getMetaItem(),1, 903),
                new ItemStack(GOOWARE_SMD_DIODE.getMetaItem(),1, 901)
        ));

        //UEV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UEV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_UEV.getMetaItem(), 1, 60),
                6,
                new AspectList().add(Aspect.MECHANISM, 96).add(Aspect.MAGIC,96).add(Aspect.DESIRE, 96),
                new ItemStack(MAGIC_CIRCUIT_UHV.getMetaItem(), 1, 59),

                new ItemStack(MAGIC_CIRCUIT_UV.getMetaItem(), 1, 58),
                new ItemStack(MAGIC_CIRCUIT_UV.getMetaItem(), 1, 58),
                new ItemStack(MAGIC_CIRCUIT_UV.getMetaItem(), 1, 58),
                new ItemStack(MAGIC_CIRCUIT_UV.getMetaItem(), 1, 58),

                new ItemStack(OPTICAL_SMD_CAPACITOR.getMetaItem(), 1, 905),
                new ItemStack(OPTICAL_SMD_TRANSISTOR.getMetaItem(),1, 908),
                new ItemStack(OPTICAL_SMD_DIODE.getMetaItem(),1, 906)
        ));

        //UIV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UIV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_UIV.getMetaItem(), 1, 61),
                6,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                new ItemStack(MAGIC_CIRCUIT_UEV.getMetaItem(), 1, 60),

                new ItemStack(MAGIC_CIRCUIT_UHV.getMetaItem(), 1, 59),
                new ItemStack(MAGIC_CIRCUIT_UHV.getMetaItem(), 1, 59),
                new ItemStack(MAGIC_CIRCUIT_UHV.getMetaItem(), 1, 59),
                new ItemStack(MAGIC_CIRCUIT_UHV.getMetaItem(), 1, 59),

                new ItemStack(SPINTRONIC_SMD_CAPACITOR.getMetaItem(), 1, 910),
                new ItemStack(SPINTRONIC_SMD_TRANSISTOR.getMetaItem(),1, 913),
                new ItemStack(SPINTRONIC_SMD_DIODE.getMetaItem(),1, 911)
        ));

        //UXV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_UXV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_UXV.getMetaItem(), 1, 62),
                8,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                new ItemStack(MAGIC_CIRCUIT_UIV.getMetaItem(), 1, 61),

                new ItemStack(MAGIC_CIRCUIT_UEV.getMetaItem(), 1, 60),
                new ItemStack(MAGIC_CIRCUIT_UEV.getMetaItem(), 1, 60),
                new ItemStack(MAGIC_CIRCUIT_UEV.getMetaItem(), 1, 60),
                new ItemStack(MAGIC_CIRCUIT_UEV.getMetaItem(), 1, 60),

                new ItemStack(COSMIC_SMD_CAPACITOR.getMetaItem(), 1, 915),
                new ItemStack(COSMIC_SMD_TRANSISTOR.getMetaItem(),1, 918),
                new ItemStack(COSMIC_SMD_DIODE.getMetaItem(),1, 916)
        ));

        //OpV
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_OpV.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_OpV.getMetaItem(), 1, 63),
                8,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                new ItemStack(MAGIC_CIRCUIT_UXV.getMetaItem(), 1, 62),

                new ItemStack(MAGIC_CIRCUIT_UIV.getMetaItem(), 1, 61),
                new ItemStack(MAGIC_CIRCUIT_UIV.getMetaItem(), 1, 61),
                new ItemStack(MAGIC_CIRCUIT_UIV.getMetaItem(), 1, 61),
                new ItemStack(MAGIC_CIRCUIT_UIV.getMetaItem(), 1, 61),

                new ItemStack(SUPRACAUSAL_SMD_CAPACITOR.getMetaItem(), 1, 915),
                new ItemStack(SUPRACAUSAL_SMD_TRANSISTOR.getMetaItem(),1, 918),
                new ItemStack(SUPRACAUSAL_SMD_DIODE.getMetaItem(),1, 916)
        ));

        //MAX
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "circuit"+MAGIC_CIRCUIT_MAX.unlocalizedName), new InfusionRecipe(
                "INFUSION@2",
                new ItemStack(MAGIC_CIRCUIT_MAX.getMetaItem(), 1, 64),
                8,
                new AspectList().add(Aspect.MECHANISM, 128).add(Aspect.MAGIC,128).add(Aspect.DESIRE, 128),
                new ItemStack(MAGIC_CIRCUIT_OpV.getMetaItem(), 1, 63),

                new ItemStack(MAGIC_CIRCUIT_UXV.getMetaItem(), 1, 62),
                new ItemStack(MAGIC_CIRCUIT_UXV.getMetaItem(), 1, 62),
                new ItemStack(MAGIC_CIRCUIT_UXV.getMetaItem(), 1, 62),
                new ItemStack(MAGIC_CIRCUIT_UXV.getMetaItem(), 1, 62),

                new ItemStack(SUPRACAUSAL_SMD_CAPACITOR.getMetaItem(), 1, 915),
                new ItemStack(SUPRACAUSAL_SMD_TRANSISTOR.getMetaItem(),1, 918),
                new ItemStack(SUPRACAUSAL_SMD_DIODE.getMetaItem(),1, 916)
        ));
    }

}
