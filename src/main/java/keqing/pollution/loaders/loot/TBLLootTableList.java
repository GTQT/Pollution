package keqing.pollution.loaders.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

import java.io.File;
import java.util.Set;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.ResourceLocation;

public class TBLLootTableList
{
    private static final Set<ResourceLocation> LOOT_TABLES = Sets.<ResourceLocation>newHashSet();
    private static final Set<ResourceLocation> READ_ONLY_LOOT_TABLES = Collections.<ResourceLocation>unmodifiableSet(LOOT_TABLES);
    public static final ResourceLocation EMPTY = register("empty");

    public static final ResourceLocation COMMON_CHEST = register("loot_tables/loot/common_chest_loot");
    public static final ResourceLocation COMMON_POT = register("loot_tables/loot/common_pot_loot");
    public static final ResourceLocation CRAGROCK_TOWER_CHEST = register("loot_tables/loot/cragrock_tower_chest");
    public static final ResourceLocation CRAGROCK_TOWER_POT = register("loot_tables/loot/cragrock_tower_pot");
    public static final ResourceLocation DUNGEON_CHEST = register("loot_tables/loot/dungeon_chest_loot");
    public static final ResourceLocation DUNGEON_POT = register("loot_tables/loot/dungeon_pot_loot");
    public static final ResourceLocation WIGHT_FORTRESS_CHEST = register("loot_tables/loot/weight_fortress_chest");
    public static final ResourceLocation WIGHT_FORTRESS_POT = register("loot_tables/loot/weight_fortress_loot");
    private static ResourceLocation register(String id)
    {
        return register(new ResourceLocation("thebetweenlands", id));
    }

    public static ResourceLocation register(ResourceLocation id)
    {
        if (LOOT_TABLES.add(id))
        {
            return id;
        }
        else
        {
            throw new IllegalArgumentException(id + " is already a registered built-in loot table");
        }
    }

    public static Set<ResourceLocation> getAll()
    {
        return READ_ONLY_LOOT_TABLES;
    }

    public static boolean test()
    {
        LootTableManager loottablemanager = new LootTableManager((File)null);

        for (ResourceLocation resourcelocation : READ_ONLY_LOOT_TABLES)
        {
            if (loottablemanager.getLootTableFromLocation(resourcelocation) == LootTable.EMPTY_LOOT_TABLE)
            {
                return false;
            }
        }

        return true;
    }
}
