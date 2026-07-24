package meowmel.pollution.api.metatileentity;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public abstract class ManaMultiblockController extends MultiMapMultiblockController {

    public ManaMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
        this.recipeMapWorkable = new MultiblockRecipeLogic(this);
    }

    public ManaMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new MultiblockRecipeLogic(this);
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        if (onlyManaEnergy()) {
            List<IEnergyContainer> inputEnergy = new ArrayList<>(this.getAbilities(POMultiblockAbility.MANA_INPUT_HATCH));
            this.energyContainer = new EnergyContainerList(inputEnergy);
        } else {
            List<IEnergyContainer> inputEnergy = new ArrayList<>(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
            inputEnergy.addAll(this.getAbilities(POMultiblockAbility.MANA_INPUT_HATCH));
            this.energyContainer = new EnergyContainerList(inputEnergy);
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(this.getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(this::addCustomCapacity)
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }

    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {

    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("-魔力能源仓支持："));
        if (onlyManaEnergy()) {
            tooltip.add(TextFormatting.GRAY + I18n.format("只允许使用魔力能源仓作为能量输入接口"));
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("允许额外使用魔力能源仓作为能量输入接口"));
        }
        tooltip.add(TextFormatting.GRAY + I18n.format("耗能，双仓升压等计算同普通能源仓"));
    }

    public boolean onlyManaEnergy() {
        return false;
    }

    /**
     * V3 equivalent of this controller family's legacy {@code autoAbilities()} predicate.
     * {@code maxHatches} is the number of casing positions the original predicate could replace.
     */
    protected static DeclarativePatternBuilder configureManaRecipeCasing(
            DeclarativePatternBuilder.CasingSlot casing, RecipeMap<?> recipeMap, int maxHatches) {
        List<MultiblockAbility<?>> abilities = new ArrayList<>();
        abilities.add(POMultiblockAbility.MANA_INPUT_HATCH);
        abilities.add(MultiblockAbility.INPUT_ENERGY);
        abilities.add(MultiblockAbility.MAINTENANCE_HATCH);
        abilities.add(MultiblockAbility.MUFFLER_HATCH);
        if (recipeMap.getMaxInputs() > 0) abilities.add(MultiblockAbility.IMPORT_ITEMS);
        if (recipeMap.getMaxOutputs() > 0) abilities.add(MultiblockAbility.EXPORT_ITEMS);
        if (recipeMap.getMaxFluidInputs() > 0) abilities.add(MultiblockAbility.IMPORT_FLUIDS);
        if (recipeMap.getMaxFluidOutputs() > 0) abilities.add(MultiblockAbility.EXPORT_FLUIDS);

        return casing
                .custom(Elements.abilities(0, maxHatches,
                        abilities.toArray(new MultiblockAbility<?>[0])), maxHatches)
                .done()
                .abilityGroup(POMultiblockAbility.MANA_INPUT_HATCH, 1, 2,
                        POMultiblockAbility.MANA_INPUT_HATCH, MultiblockAbility.INPUT_ENERGY)
                .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                .globalAbilityLimit(MultiblockAbility.MUFFLER_HATCH, 1, 1);
    }
}
