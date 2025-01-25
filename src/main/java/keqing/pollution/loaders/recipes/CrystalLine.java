package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

public class CrystalLine {
    public static void init() {
        infused("aer", BlocksTC.crystalAir, PollutionMaterials.infused_air);
        infused("terra", BlocksTC.crystalEarth, PollutionMaterials.infused_earth);
        infused("aqua", BlocksTC.crystalWater, PollutionMaterials.infused_water);
        infused("ignis", BlocksTC.crystalFire, PollutionMaterials.infused_fire);
        infused("ordo", BlocksTC.crystalOrder, PollutionMaterials.infused_order);
        infused("perditio", BlocksTC.crystalEntropy, PollutionMaterials.infused_entropy);
    }
    public static void infused(String sting, Block block, Material material) {
        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .input(ItemsTC.salisMundus)
                .inputs(createCrystalEssenceWithAspects(sting))
                .output(block)
                .duration(200)
                .EUt(30)
                .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .fluidOutputs(material.getFluid(36))
                .inputs(createCrystalEssenceWithAspects(sting))
                .duration(200)
                .EUt(30)
                .buildAndRegister();
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
}
