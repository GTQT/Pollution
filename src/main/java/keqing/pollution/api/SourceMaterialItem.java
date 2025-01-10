package keqing.pollution.api;

import gregtech.api.unification.material.Material;
import net.minecraft.item.ItemStack;

public interface SourceMaterialItem {

    int getMaxSourceStore();

    void setSourceStore(int source,ItemStack item);
    int getSourceStore(ItemStack item);

    boolean addSource(int amount,boolean simulate,ItemStack item);
    boolean consumeSource(int amount,boolean simulate,ItemStack item);

    Material getMaterial();
}
