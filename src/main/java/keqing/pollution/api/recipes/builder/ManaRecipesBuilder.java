package keqing.pollution.api.recipes.builder;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.EnumValidationResult;
import keqing.gtqtcore.api.recipes.properties.ELEProperties;
import keqing.gtqtcore.api.utils.GTQTLog;
import keqing.pollution.api.recipes.properties.ManaProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ManaRecipesBuilder extends RecipeBuilder<ManaRecipesBuilder> {

    public ManaRecipesBuilder() {}

    public ManaRecipesBuilder(Recipe recipe, RecipeMap<ManaRecipesBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public ManaRecipesBuilder(RecipeBuilder<ManaRecipesBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public ManaRecipesBuilder copy() {
        return new ManaRecipesBuilder(this);
    }

    public int getTotalMana() {
        return this.recipePropertyStorage == null ? 0 :
                this.recipePropertyStorage.getRecipePropertyValue(ManaProperty.getInstance(), 0);
    }

    public ManaRecipesBuilder TotalMana(int TotalMana) {
        if (TotalMana < 0) {
            GTQTLog.logger.error("Total Mana cannot be less than or equal to 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }

        this.applyProperty(ManaProperty.getInstance(), TotalMana);
        return this;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(ManaProperty.getInstance().getKey(), getTotalMana())
                .toString();
    }
}