package keqing.pollution.loaders.recipes;

import WayofTime.bloodmagic.meteor.Meteor;
import WayofTime.bloodmagic.meteor.MeteorComponent;
import com.google.common.collect.Lists;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import keqing.pollution.api.utils.PollutionLog;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static WayofTime.bloodmagic.meteor.MeteorRegistry.meteorMap;
import static WayofTime.bloodmagic.meteor.MeteorRegistry.registerMeteor;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.ore;
import static net.minecraft.init.Blocks.DIRT;

public class MeteorsHelper {
    public static void init() {
        List<MeteorComponent> diamondMeteorList = Lists.newArrayList();
        diamondMeteorList.add(new MeteorComponent(50, "oreIron"));
        diamondMeteorList.add(new MeteorComponent(100, "oreGold"));
        diamondMeteorList.add(new MeteorComponent(10, "oreLapis"));
        diamondMeteorList.add(new MeteorComponent(250, "oreDiamond"));
        diamondMeteorList.add(new MeteorComponent(180, "oreEmerald"));
        diamondMeteorList.add(new MeteorComponent(50, "oreRedstone"));
        diamondMeteorList.add(new MeteorComponent(400, "oreDiamond"));
        Meteor diamondMeteor = new Meteor(new ItemStack(Blocks.DIRT), diamondMeteorList, 10.0F, 3);

        //List<MeteorComponent> componentList = Lists.newArrayList();
        //componentList.add(new MeteorComponent(1,"oreIron"));
        //componentList.add(new MeteorComponent(2,"oreCopper"));
        //registerMeteor(new ItemStack(DIRT,1),componentList,1, 1);
        meteorMap.put(new ItemStack(DIRT,1), diamondMeteor);
        //addNewMeteor(new ItemStack(DIRT,1), Arrays.asList(Iron, Copper), new int[]{1,2},1,1);
    }
    public static void addNewMeteor(ItemStack catalystStack, List<Material> materials,int[] weight, float explosionStrength, int radius)
    {
        int count = 0;
        List<MeteorComponent> componentList = new ArrayList<>();
        for(int weights:weight) {

            List<String> ores=Arrays.stream(OreDictionary.getOreIDs(OreDictUnifier.get(ore, materials.get(count), 1))).mapToObj(OreDictionary::getOreName).collect(Collectors.toList());

            componentList.add(new MeteorComponent(weights,ores.get(0)));
            PollutionLog.logger.info("1145141919810");
            PollutionLog.logger.info("ore"+materials.get(count).toString()) ;
            count++;
        }

        registerMeteor(catalystStack,componentList,explosionStrength, radius);
    }
}
