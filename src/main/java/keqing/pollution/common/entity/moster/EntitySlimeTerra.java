package keqing.pollution.common.entity.moster;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntitySlimeTerra extends  EntityTcSlime{
    public EntitySlimeTerra(World worldIn) {
        super(worldIn);
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return this.getSlimeSize() == 1 ? EntityTcSlime.LOOT_TABLE_TERRA : LootTableList.EMPTY;
    }
    @Override
    protected EntitySlime createInstance()
    {
        return new EntitySlimeTerra(this.world);
    }
}
