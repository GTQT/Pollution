package keqing.pollution.loaders.recipes;

import WayofTime.bloodmagic.meteor.Meteor;
import WayofTime.bloodmagic.meteor.MeteorComponent;
import WayofTime.bloodmagic.meteor.MeteorRegistry;
import com.google.common.collect.Lists;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.List;

import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.Materials.Diamond;
import static gregtech.api.unification.ore.OrePrefix.gem;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_METEORS_RECIPES;

public class MeteorsHelper {
    public static void init() {
        initMeteors();
    }

    private static void initMeteors() {
        registerMeteor(
                MetaItems.SENSOR_LV.getStackForm(),
                15,
                16,
                2,
                300000,
                Materials.Tin, 100,
                Materials.Gold, 100,
                Materials.Copper, 100,
                Materials.Iron, 100,
                Materials.Silver, 100,
                Materials.Lapis, 100,
                Materials.Redstone, 100,
                Materials.Salt, 100,
                Materials.RockSalt, 100,
                Materials.Lead, 100,
                Materials.Sphalerite, 100,
                Materials.Nickel, 100,
                Materials.Calcite, 100,
                Materials.Cassiterite, 100,
                Materials.Tantalite, 100
        );
        registerMeteor(
                MetaItems.EMITTER_LV.getStackForm(),
                15,
                18,
                2,
                300000,
                Materials.Oilsands, 100,
                Materials.Ruby, 100,
                Materials.FullersEarth, 100,
                Materials.Bentonite, 100,
                Materials.Mica, 100,
                Materials.Lepidolite, 100,
                Materials.Almandine, 100,
                Materials.Grossular, 100,
                Materials.Pyrope, 100,
                Materials.Spessartine, 100,
                Materials.GarnetRed, 100,
                Materials.GarnetYellow, 100,
                Materials.Pollucite, 100,
                Materials.GarnetSand, 100,
                Materials.Asbestos, 100
        );
        registerMeteor(
                OreDictUnifier.get(gem,Diamond),
                15,
                13,
                2,
                420000,
                Diamond, 100,
                Materials.Coal, 100,
                Materials.Graphite, 100,
                GTQTMaterials.Lignite, 100,
                GTQTMaterials.Gashydrate, 100
        );
        registerMeteor(
                MetaItems.SENSOR_MV.getStackForm(),
                15,
                16,
                2,
                400000,
                Materials.Saltpeter, 100,
                Materials.Diatomite, 100,
                Materials.Electrotine, 100,
                Materials.Alunite, 100,
                Materials.Topaz, 100,
                Materials.Chalcocite, 100,
                Materials.Bornite, 100,
                Materials.Beryllium, 100,
                Materials.Emerald, 100,
                Materials.NetherQuartz, 100,
                Materials.QuartzSand, 100,
                Materials.Quartzite, 100,
                Materials.Sulfur, 100,
                Materials.Trona, 100,
                GTQTMaterials.Cryolite, 100
        );
        registerMeteor(
                MetaItems.EMITTER_MV.getStackForm(),
                12,
                16,
                2,
                400000,
                PollutionMaterials.infused_air, 100,
                PollutionMaterials.infused_fire, 100,
                PollutionMaterials.infused_water, 100,
                PollutionMaterials.infused_earth, 100,
                PollutionMaterials.infused_entropy, 100,
                PollutionMaterials.infused_order, 100
        );
        registerMeteor(
                MetaItems.FIELD_GENERATOR_MV.getStackForm(),
                18,
                16,
                2,
                480000,
                PollutionMaterials.thaumium, 100,
                PollutionMaterials.syrmorite, 100,
                PollutionMaterials.octine, 100,
                PollutionMaterials.scabyst, 100,
                PollutionMaterials.valonite, 100,
                PollutionMaterials.FlameCoal, 100,
                PollutionMaterials.DumbTin, 100,
                PollutionMaterials.MeltGold, 100,
                PollutionMaterials.AuthorityLead, 100,
                PollutionMaterials.Pyrargyrite, 100,
                PollutionMaterials.PlutoZinc, 100
        );
        registerMeteor(
                MetaItems.SENSOR_HV.getStackForm(),
                15,
                13,
                2,
                500000,
                Materials.Bastnasite, 100,
                Materials.Monazite, 100,
                Materials.Ilmenite, 100,
                Materials.Pitchblende, 100,
                Materials.Neodymium, 100,
                Materials.Molybdenum, 100,
                Materials.Rutile, 100,
                Materials.Bauxite, 100,
                Materials.Cooperite, 100,
                Materials.Cobaltite, 100,
                Materials.Magnesite, 100,
                Materials.Molybdenite, 100,
                Materials.Arsenic, 100,
                Materials.Astatine, 100,
                Materials.Thorium, 100
        );
        registerMeteor(
                MetaItems.SENSOR_EV.getStackForm(),
                15,
                18,
                2,
                750000,
                Materials.Tungstate, 100,
                Materials.Plutonium, 100,
                Materials.Plutonium239, 100,
                Materials.Plutonium241, 100,
                Materials.Aluminium, 100,
                Materials.Lithium, 100,
                Materials.Beryllium, 100,
                Materials.Boron, 100,
                Materials.Cadmium, 100,
                Materials.Germanium, 100,
                Materials.Gallium, 100,
                Materials.Hafnium, 100,
                Materials.Zirconium, 100,
                Materials.Scheelite, 100,
                Materials.Rutile, 100
        );
        registerMeteor(
                MetaItems.FIELD_GENERATOR_IV.getStackForm(),
                15,
                13,
                3,
                1200000,
                Materials.Platinum, 100,
                Materials.Palladium, 100,
                Materials.Osmium, 100,
                Materials.Iridium, 100,
                Materials.Rhodium, 100,
                Materials.Ruthenium, 100,
                Materials.Niobium, 100
        );
    }

    public static void registerMeteor(ItemStack catalyst, float explosionStrength, int radius, int version, int cost, Object... materialsAndWeights) {
        // 校验参数有效性
        if (materialsAndWeights.length % 2 != 0) {
            throw new IllegalArgumentException("Materials and weights must be in pairs!");
        }

        List<MeteorComponent> meteorList = Lists.newArrayList();

        RecipeBuilder<?> builder;
        builder = MAGIC_METEORS_RECIPES.recipeBuilder()
                .inputs(catalyst);

        int totalNumber = (int) (4.18879 * radius * radius * radius);
        int totalWeight = 0;

        // 处理可变参数中的材料和重量对
        for (int i = 0; i < materialsAndWeights.length; i += 2) {
            Material material = (Material) materialsAndWeights[i];
            int weight = Math.max(1, (int) materialsAndWeights[i + 1]);
            meteorList.add(toMeteorComponent(material, weight));
            totalWeight += weight;
        }
        for (int i = 0; i < materialsAndWeights.length; i += 2) {
            Material material = (Material) materialsAndWeights[i];
            int weight = (int) materialsAndWeights[i + 1];
            builder.output(OrePrefix.ore, material, totalNumber * weight / totalWeight);
        }

        // 创建并配置陨星
        Meteor meteor = new Meteor(catalyst, meteorList, explosionStrength, radius);
        meteor.setVersion(version);
        meteor.setCost(cost);

        builder.fluidInputs(Materials.Water.getFluid(cost))
                .EUt(VA[6 + version])
                .duration(totalWeight/20)
                .buildAndRegister();
        // 注册陨星
        MeteorRegistry.registerMeteor(meteor.getCatalystStack(), meteor);
    }

    public static MeteorComponent toMeteorComponent(Material material, int weight) {
        StringBuilder sb = new StringBuilder("ore");
        for (String part : material.toString().split("_")) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1));
            }
        }
        return new MeteorComponent(weight, sb.toString());
    }
}
