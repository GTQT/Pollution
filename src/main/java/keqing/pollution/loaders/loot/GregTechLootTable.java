package keqing.pollution.loaders.loot;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.ConfigHolder;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.loaders.dungeon.ChestGenHooks;
import io.netty.channel.AbstractEventLoop;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Arrays;
import java.util.List;

import static gregtech.api.unification.material.Materials.*;

public class GregTechLootTable {
    public static void init()
    {
        if (!ConfigHolder.worldgen.addLoot)return;

        List<ResourceLocation> lootTables1 = Arrays.asList(
                TBLLootTableList.COMMON_CHEST,
                TBLLootTableList.COMMON_POT,
                TBLLootTableList.CRAGROCK_TOWER_CHEST,
                TBLLootTableList.CRAGROCK_TOWER_POT,
                TBLLootTableList.DUNGEON_CHEST,
                TBLLootTableList.DUNGEON_POT,
                TBLLootTableList.WIGHT_FORTRESS_CHEST,
                TBLLootTableList.WIGHT_FORTRESS_POT
        );
        List<ResourceLocation> lootTables2 = Arrays.asList(
                LootTableList.CHESTS_SIMPLE_DUNGEON,
                LootTableList.CHESTS_DESERT_PYRAMID,
                LootTableList.CHESTS_JUNGLE_TEMPLE,
                LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER,
                LootTableList.CHESTS_ABANDONED_MINESHAFT,
                LootTableList.CHESTS_VILLAGE_BLACKSMITH,
                LootTableList.CHESTS_STRONGHOLD_CROSSING,
                LootTableList.CHESTS_STRONGHOLD_CORRIDOR,
                LootTableList.CHESTS_END_CITY_TREASURE
        );

        for (ResourceLocation lootTable : lootTables1) {
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeSmallFluid,Copper)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeNormalFluid,Copper)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeLargeFluid,Copper)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeSmallItem,Tin)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeNormalItem,Tin)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeLargeItem,Tin)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.ingot,Invar)), 2, 6, 10);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.ingot,Aluminium)), 1, 4, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Tin)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Copper)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Iron)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,RedAlloy)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Lead)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Gold)), 1, 5, 10);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Silver)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Tin)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Copper)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,RedAlloy)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Lead)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Gold)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_BOILER_LAVA_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm(), 1, 2, 5);

            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_FURNACE_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_ROCK_BREAKER_BRONZE.getStackForm(), 1, 2, 5);
            ChestGenHooks.addItem(lootTable, MetaTileEntities.STEAM_MINER.getStackForm(), 1, 2, 5);

            ChestGenHooks.addItem(lootTable, new ItemStack(Blocks.PISTON), 2, 4, 15);
            ChestGenHooks.addItem(lootTable, new ItemStack(Blocks.PISTON_EXTENSION), 2, 4, 15);
        }
        for (ResourceLocation lootTable : lootTables2) {
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeSmallFluid,Steel)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeNormalFluid,Steel)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeLargeFluid,Steel)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeSmallItem,Invar)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeNormalItem,Invar)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.pipeLargeItem,Invar)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.ingot,Invar)), 4, 8, 10);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.ingot,Aluminium)), 2, 6, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Copper)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Tin)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Steel)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Invar)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Lead)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Gold)), 1, 5, 10);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.plate,Silver)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Tin)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Copper)), 4, 8, 20);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,RedAlloy)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Lead)), 2, 6, 15);
            ChestGenHooks.addItem(lootTable, OreDictUnifier.get(new UnificationEntry(OrePrefix.wireGtSingle,Gold)), 1, 5, 10);

            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.EXTRACTOR.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.MACERATOR.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.ALLOY_SMELTER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.BENDER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.BREWERY.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.CENTRIFUGE.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.CHEMICAL_BATH.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.COMPRESSOR.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.CUTTER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.ELECTRIC_FURNACE.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.FERMENTER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.FORGE_HAMMER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.LATHE.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.MIXER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.ORE_WASHER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.PACKER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.SIFTER.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.WIREMILL.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.STEAM_TURBINE.getStackForm(), 1, 1, 5);
            ChestGenHooks.addItem(lootTable, GTSteamMetaTileEntities.COMBUSTION_GENERATOR.getStackForm(), 1, 1, 5);

            ChestGenHooks.addItem(lootTable, new ItemStack(Blocks.PISTON), 2, 4, 30);
            ChestGenHooks.addItem(lootTable, new ItemStack(Blocks.PISTON_EXTENSION), 2, 4, 30);
        }

    }

}
