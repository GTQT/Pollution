package keqing.pollution.api.recipes.properties;

import gregtech.api.recipes.properties.RecipeProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

public class ManaProperty extends RecipeProperty<Integer> {

    public static final String KEY = "TotalMana";

    private static ManaProperty INSTANCE;

    private ManaProperty() {
        super(KEY, Integer.class);
    }

    public static ManaProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ManaProperty();
        }
        return INSTANCE;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format("魔力总计：%s",
                castValue(value)), x, y, color);
    }

    @Override
    public NBTBase serialize(Object value) {
        return new NBTTagInt(castValue(value));
    }

    @Override
    public Object deserialize( NBTBase nbt) {
        return ((NBTTagInt) nbt).getInt();
    }
}
