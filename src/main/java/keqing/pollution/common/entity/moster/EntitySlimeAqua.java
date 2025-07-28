package keqing.pollution.common.entity.moster;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
    @Override
    protected EntitySlime createInstance()
    {
        return new EntitySlimeAqua(this.world);
    }
    @Override
    protected Item getDropItem()
    {
        return this.getSlimeSize() == 1 ? Items.SLIME_BALL : null;
    }
}
