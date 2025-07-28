package keqing.pollution.common.entity.moster;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntitySlimeAqua extends  EntityTcSlime{
    public EntitySlimeAqua(World worldIn) {
        super(worldIn);
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return this.getSlimeSize() == 1 ? EntityTcSlime.LOOT_TABLE_AQUA : LootTableList.EMPTY;
    }
}
