package keqing.pollution.loaders.recipes;


import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import gregtech.api.recipes.ingredients.GTRecipeOreInput;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.stack.UnificationEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.items.ItemsTC;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static keqing.gtqtcore.api.unification.GCYSMaterials.Curium247;
import static keqing.gtqtcore.api.unification.GCYSMaterials.Plutonium242;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Californium252;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Zircon;

public class BloodAltar {
    public static void init() {
        addBloodAltar(Iron,Cobalt,1);
        addBloodAltar(Cobalt,Copper,1);
        addBloodAltar(Nickel,Copper,1);
        addBloodAltar(Copper,Zinc,1);
        addBloodAltar(Zinc,Arsenic,1);
        addBloodAltar(Arsenic,Strontium,2);
        addBloodAltar(Zirconium,Molybdenum,2);
        addBloodAltar(Molybdenum,Ruthenium,2);
        addBloodAltar(Ruthenium,Silver,2);
        addBloodAltar(Silver,Tin,1);
        addBloodAltar(Tin,Iodine,1);
        addBloodAltar(Iodine,Caesium,3);
        addBloodAltar(Caesium,Neodymium,4);
        addBloodAltar(Neodymium,Samarium,4);
        addBloodAltar(Samarium,Terbium,5);
        addBloodAltar(Europium,Terbium,5);
        addBloodAltar(Terbium,Erbium,5);
        addBloodAltar(Erbium,Ytterbium,5);
        addBloodAltar(Ytterbium,Hafnium,4);
        addBloodAltar(Hafnium,Tungsten,4);
        addBloodAltar(Tungsten,Osmium,2);
        addBloodAltar(Osmium,Iridium,3);
        addBloodAltar(Iridium,Platinum,3);
        addBloodAltar(Lead,Bismuth,2);
        addBloodAltar(Bismuth,Polonium,5);
        addBloodAltar(Polonium,Radium,5);
        addBloodAltar(Radium,Thorium,5);
        addBloodAltar(Thorium,Uranium238,5);
        addBloodAltar(Uranium238,Plutonium242,5);
        addBloodAltar(Plutonium242,Curium247,5);
        addBloodAltar(Curium247,Californium252,5);
        //BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(Ingredient.fromStacks(OreDictUnifier.get(dust, Iron, 1)),OreDictUnifier.get(dust, Copper, 1),1,1,1,1);
        addBloodAltar(Curium247,Californium252,5);


        addBloodAltarEx(OreDictUnifier.get(dust, Gold, 1), new ItemStack(ItemsTC.quicksilver),2);
        addBloodAltarEx(new ItemStack(ItemsTC.quicksilver),OreDictUnifier.get(dust, Lead, 1),1);
    }
    public static void addBloodAltar(Material material1, Material material2, int tier)
    {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(Ingredient.fromStacks(OreDictUnifier.get(dust, material1, 1)),OreDictUnifier.get(dust, material2, 1),tier,(int) Math.pow(2,tier+2)*500,(int) Math.pow(2,tier+2),(int) Math.pow(2,tier+3));
    }
    public static void addBloodAltarEx(ItemStack material1, ItemStack material2, int tier)
    {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(Ingredient.fromStacks(material1),material2,tier,(int) Math.pow(2,tier+2)*500,(int) Math.pow(2,tier+2),(int) Math.pow(2,tier+3));
    }
}
