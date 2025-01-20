package keqing.pollution.api.recipes.properties;

import gregtech.api.recipes.recipeproperties.RecipeProperty;
import gregtech.api.util.TextFormattingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.TreeMap;

public class ManaProperty extends RecipeProperty<Integer> {

    public static final String KEY = "TotalMana";

    private static final TreeMap<Integer, String> registeredTotalMana = new TreeMap<>();
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

    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format("魔力总计：%s", TextFormattingUtil.formatLongToCompactString(this.castValue(value))), x, y, color);
    }


}
