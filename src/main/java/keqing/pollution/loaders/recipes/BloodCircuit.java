package keqing.pollution.loaders.recipes;


import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.block.BlockLifeEssence;
import gregtech.api.metatileentity.multiblock.CleanroomType;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.items.GTQTMetaItem1;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.COMPONENT_ASSEMBLER_RECIPES;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;
import static keqing.pollution.common.items.PollutionMetaItems.*;
import static keqing.pollution.common.metatileentity.PollutionMetaTileEntities.MAGIC_ASSEMBLER;

public class BloodCircuit {
    public static void init() {
        addBloodAltarEx(MAGIC_CIRCUIT_ULV.getStackForm(1),BLOOD_PORT.getStackForm(1), 3);

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",5,2,null))
                .input(OrePrefix.dust,Meat)
                .fluidInputs(new FluidStack(BlockLifeEssence.getLifeEssence(), 1000), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_PRIMITIVE_MEAT)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(dust, Polytetrafluoroethylene)
                .input(dust, StreptococcusPyogenes,4)
                .fluidInputs(BloodPlasma.getFluid(500), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_MITOCHONDRION_POWER)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(plate, Polytetrafluoroethylene)
                .inputs(GameRegistry.makeItemStack("thaumcraft:jar_brain",0,1,null))
                .fluidInputs(BloodPlasma.getFluid(500), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_ENDORPHINS_STABILIZER)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(plate, Polytetrafluoroethylene, 2)
                .input(COLDCORE)
                .input(BLOOD_PRIMITIVE_MEAT)
                .fluidInputs(BloodPlasma.getFluid(500), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_FREEZE_COOLER)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(plate, Polytetrafluoroethylene, 2)
                .input(OrePrefix.dust,Meat,8)
                .input(BLOOD_PRIMITIVE_MEAT)
                .fluidInputs(BloodPlasma.getFluid(10000), PollutionMaterials.infused_magic.getFluid(288))
                .output(BLOOD_LYSOSOME_STABILIZER)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(dust, StreptococcusPyogenes)
                .fluidInputs(BloodPlasma.getFluid(100), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_RATS_BRAIN)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[LuV]).duration(600)
                .input(BLOOD_PRIMITIVE_MEAT,2)
                .input(STEM_CELLS,4)
                .fluidInputs(GTQTMaterials.GeneTherapyFluid.getFluid(50), BloodPlasma.getFluid(16000), PollutionMaterials.infused_magic.getFluid(1440))
                .output(BLOOD_IPS_HUMAN_BRAIN)
                .buildAndRegister();

        //Circuits
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT,2)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(dust, StreptococcusPyogenes)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(CAPACITOR,4)
                .input(TRANSISTOR, 4)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_MV,2)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[HV]).duration(200)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT,2)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(dust, StreptococcusPyogenes,2)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(CAPACITOR,2)
                .input(DIODE, 2)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_HV,2)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[EV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT,2)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(STEM_CELLS,2)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(CAPACITOR,2)
                .input(DIODE, 1)
                .input(TRANSISTOR, 2)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_EV,2)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[EV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT,2)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(STEM_CELLS,4)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(SMD_CAPACITOR,4)
                .input(SMD_TRANSISTOR, 4)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_IV,2)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[IV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT,2)
                .input(BLOOD_PRIMITIVE_MEAT,2)
                .input(STEM_CELLS,2)
                .input(BLOOD_RATS_BRAIN)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(SMD_RESISTOR,4)
                .input(SMD_CAPACITOR,6)
                .input(SMD_DIODE, 2)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_LuV,2)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[IV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT,2)
                .input(STEM_CELLS,4)
                .input(BLOOD_RATS_BRAIN,2)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(ADVANCED_SMD_TRANSISTOR,2)
                .input(ADVANCED_SMD_CAPACITOR,2)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_ZPM)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[LuV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(STEM_CELLS,8)
                .input(BLOOD_RATS_BRAIN,4)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(ADVANCED_SMD_TRANSISTOR,4)
                .input(ADVANCED_SMD_RESISTOR,6)
                .input(ADVANCED_SMD_DIODE,2)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_UV)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[ZPM]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(BLOOD_IPS_HUMAN_BRAIN)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(ADVANCED_SMD_TRANSISTOR,4)
                .input(ADVANCED_SMD_RESISTOR,6)
                .input(ADVANCED_SMD_DIODE,4)
                .input(ADVANCED_SMD_CAPACITOR,8)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_UHV)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[UV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(BLOOD_IPS_HUMAN_BRAIN)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(GTQTMetaItems.GOOWARE_SMD_TRANSISTOR,2)
                .input(GTQTMetaItems.GOOWARE_SMD_RESISTOR,4)
                .input(GTQTMetaItems.GOOWARE_SMD_DIODE,2)
                .input(GTQTMetaItems.GOOWARE_SMD_CAPACITOR,4)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_UEV)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[UHV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(BLOOD_IPS_HUMAN_BRAIN)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(GTQTMetaItems.OPTICAL_SMD_TRANSISTOR,2)
                .input(GTQTMetaItems.OPTICAL_SMD_RESISTOR,4)
                .input(GTQTMetaItems.OPTICAL_SMD_DIODE,2)
                .input(GTQTMetaItems.OPTICAL_SMD_CAPACITOR,4)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(144))
                .output(BLOOD_CIRCUIT_UIV)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[UEV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(BLOOD_IPS_HUMAN_BRAIN)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(GTQTMetaItems.SPINTRONIC_SMD_TRANSISTOR,2)
                .input(GTQTMetaItems.SPINTRONIC_SMD_RESISTOR,4)
                .input(GTQTMetaItems.SPINTRONIC_SMD_DIODE,2)
                .input(GTQTMetaItems.SPINTRONIC_SMD_CAPACITOR,4)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(288))
                .output(BLOOD_CIRCUIT_UXV)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[UIV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(BLOOD_IPS_HUMAN_BRAIN,2)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .input(GTQTMetaItems.COSMIC_SMD_TRANSISTOR,2)
                .input(GTQTMetaItems.COSMIC_SMD_RESISTOR,4)
                .input(GTQTMetaItems.COSMIC_SMD_CAPACITOR,4)
                .fluidInputs(BloodPlasma.getFluid(100), Enzymesac.getFluid(10), PollutionMaterials.infused_magic.getFluid(288))
                .output(BLOOD_CIRCUIT_OpV)
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder().EUt(VA[UXV]).duration(400)
                .input(plate, Polytetrafluoroethylene)
                .input(BLOOD_PORT)
                .input(BLOOD_PRIMITIVE_MEAT)
                .input(BLOOD_IPS_HUMAN_BRAIN,8)
                .fluidInputs(BloodPlasma.getFluid(400), Enzymesac.getFluid(40), PollutionMaterials.infused_magic.getFluid(720))
                .input(GTQTMetaItems.SUPRACAUSAL_SMD_TRANSISTOR,8)
                .input(GTQTMetaItems.SUPRACAUSAL_SMD_DIODE,8)
                .input(GTQTMetaItems.SUPRACAUSAL_SMD_CAPACITOR,8)
                .inputs(GameRegistry.makeItemStack("bloodmagic:component",26,1,null))
                .output(BLOOD_CIRCUIT_MAX)
                .buildAndRegister();
    }

    public static void addBloodAltarEx(ItemStack material1, ItemStack material2, int tier)
    {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(Ingredient.fromStacks(material1),material2,tier,(int) Math.pow(2,tier+2)*500,(int) Math.pow(2,tier+2),(int) Math.pow(2,tier+3));
    }
}
