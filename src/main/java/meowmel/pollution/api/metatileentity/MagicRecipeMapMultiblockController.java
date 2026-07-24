package meowmel.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.api.util.KeyUtil;
import meowmel.pollution.api.capability.IVisHatch;
import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

public abstract class MagicRecipeMapMultiblockController extends ManaMultiblockController {

    protected IVisHatch visHatch;
    protected IFluidTank infusedFluidTank;

    public MagicRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
        this.recipeMapWorkable = new MagicMultiblockRecipeLogic(this);
    }


    public MagicRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new MagicMultiblockRecipeLogic(this);
    }

    /**
     * Declares the exact hatch set previously supplied by {@code autoAbilities()}.
     * The caller supplies the old pattern's available hatch count
     * ({@code X occurrences - old X minimum}) so V3 computes the same casing
     * minimum instead of silently relaxing the structure.
     */
    protected static DeclarativePatternBuilder configureMagicRecipeCasing(
            DeclarativePatternBuilder.CasingSlot casing, RecipeMap<?> recipeMap, int maxHatches) {
        return configureMagicRecipeCasing(casing, recipeMap, maxHatches, true);
    }

    protected static DeclarativePatternBuilder configureMagicRecipeCasing(
            DeclarativePatternBuilder.CasingSlot casing, RecipeMap<?> recipeMap, int maxHatches,
            boolean includeMuffler) {
        List<MultiblockAbility<?>> abilities = new ArrayList<>();
        abilities.add(POMultiblockAbility.MANA_INPUT_HATCH);
        abilities.add(MultiblockAbility.INPUT_ENERGY);
        abilities.add(MultiblockAbility.MAINTENANCE_HATCH);
        if (includeMuffler) abilities.add(MultiblockAbility.MUFFLER_HATCH);
        abilities.add(POMultiblockAbility.VIS_HATCH);
        abilities.add(POMultiblockAbility.INFUSED_FLUID_HATCH);
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
                .globalAbilityLimit(MultiblockAbility.MUFFLER_HATCH, includeMuffler ? 1 : 0, 1)
                .globalAbilityLimit(POMultiblockAbility.VIS_HATCH, 0, 1)
                .globalAbilityLimit(POMultiblockAbility.INFUSED_FLUID_HATCH, 1, 1);
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((manager, syncer) -> {
            if (syncer.syncBoolean(checkInfusedFluid())) {
                manager.add(KeyUtil.lang(TextFormatting.RED,
                        "要素不符合"));
            }
            if(syncer.syncBoolean(visHatch == null)){
                manager.add(KeyUtil.lang(TextFormatting.RED,
                        "未安装灵气仓"));
            }
        });
    }

    public boolean checkInfusedFluid() {
        if (infusedFluidTank == null) return false;
        return infusedFluidTank.getFluid().getFluid() == getMaterial().getFluid();
    }

    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        List<IVisHatch> visHatches = this.getAbilities(POMultiblockAbility.VIS_HATCH);
        if (visHatches != null && !visHatches.isEmpty() && visHatches.get(0) != null) {
            this.visHatch = visHatches.get(0);
        }

        List<IFluidTank> infusedFluidTanks = this.getAbilities(POMultiblockAbility.INFUSED_FLUID_HATCH);
        if (infusedFluidTanks != null && !infusedFluidTanks.isEmpty() && infusedFluidTanks.get(0) != null) {
            this.infusedFluidTank = infusedFluidTanks.get(0);
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        visHatch = null;
        infusedFluidTank = null;
    }

    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            int infusedAmount = syncer.syncInt(infusedFluidTank.getFluidAmount());
            keyManager.add(KeyUtil.string(TextFormatting.GRAY, "源质仓储量：" + getMaterial().getLocalizedName() + " " + infusedAmount + "L"));

            int visStore = syncer.syncInt(getVisStore());
            keyManager.add(KeyUtil.string(TextFormatting.GRAY, "灵气仓储量：" + visStore + "vis"));
        }
    }

    public int getVisCapacity() {
        if (visHatch == null) return 0;
        return visHatch.getMaxVisStore();
    }

    public int getVisStore() {
        if (visHatch == null) return 0;
        return visHatch.getVisStore();
    }

    public boolean consumeVis(int vis, boolean simulate) {
        if (visHatch == null) return false;
        return visHatch.drainVis(vis, simulate);
    }

    public abstract Material getMaterial();

    public boolean drainInfusedFluid(int amount, boolean simulate) {
        if (infusedFluidTank == null) return false;
        if (!checkInfusedFluid()) return false;
        if (infusedFluidTank.getFluidAmount() < amount) return false;
        if (simulate) return true;
        infusedFluidTank.drain(amount, true);
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("-灵气仓支持："));
        tooltip.add(TextFormatting.GRAY + I18n.format("允许安装灵气仓开启超频模式许可"));
        tooltip.add(TextFormatting.GRAY + I18n.format("每个配方会消耗灵气仓1vis来开启超频（并行不叠算）"));
        tooltip.add(TextFormatting.GREEN + I18n.format("-塔罗牌支持："));
        tooltip.add(TextFormatting.GRAY + I18n.format("允许安装塔罗牌仓开启特殊配方机制"));
        tooltip.add(TextFormatting.GRAY + I18n.format("详细信息见塔罗牌介绍"));
    }
}
