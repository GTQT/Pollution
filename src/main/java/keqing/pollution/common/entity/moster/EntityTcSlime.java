package keqing.pollution.common.entity.moster;

import keqing.pollution.dimension.biome.POBiomeHandler;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;



import static keqing.pollution.Pollution.MODID;

public class EntityTcSlime extends EntitySlime {
    public static final ResourceLocation LOOT_TABLE_AER = LootTableList.register(new ResourceLocation(MODID,"entities/loot_table_aer"));
    public static final ResourceLocation LOOT_TABLE_IGNIS = LootTableList.register(new ResourceLocation(MODID,"entities/loot_table_ignis"));
    public static final ResourceLocation LOOT_TABLE_AQUA = LootTableList.register(new ResourceLocation(MODID,"entities/loot_table_aqua"));
    public static final ResourceLocation LOOT_TABLE_TERRA = LootTableList.register(new ResourceLocation(MODID,"entities/loot_table_terra"));
    public static final ResourceLocation LOOT_TABLE_ORDO = LootTableList.register(new ResourceLocation(MODID,"entities/loot_table_ordo"));
    public static final ResourceLocation LOOT_TABLE_PERDITIO = LootTableList.register(new ResourceLocation(MODID,"entities/loot_table_perditio"));

    private  ResourceLocation selft_loot;
    public EntityTcSlime(World worldIn) {
        super(worldIn);
    }

    @Override
    protected EntitySlime createInstance()
    {
        return new EntityTcSlime(this.world);
    }

    @Override
    protected Item getDropItem()
    {
        return this.getSlimeSize() == 1 ? Items.SLIME_BALL : null;
    }
    @Override
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return this.getSlimeSize() == 1 ? selft_loot : LootTableList.EMPTY;
    }
    @Override
    public boolean getCanSpawnHere()
    {
        BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
        Chunk chunk = this.world.getChunk(blockpos);

        if (this.world.getWorldInfo().getTerrainType().handleSlimeSpawnReduction(rand, world))
        {
            return false;
        }
        else
        {
            if (this.world.getDifficulty() != EnumDifficulty.PEACEFUL)
            {
                Biome biome = this.world.getBiome(blockpos);

                if (biome == POBiomeHandler.UnderWorld_BIOME  && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.world.getCurrentMoonPhaseFactor() && this.world.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8))
                {
                    return super.getCanSpawnHere();
                }
            }

            return false;
        }
    }
}
