package meowmel.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.api.util.KeyUtil;
import meowmel.pollution.api.capability.IAstralHatch;
import meowmel.pollution.api.capability.IBloodMagicHatch;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.capability.ITarotHatch;
import meowmel.pollution.api.capability.IVisHatch;
import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import meowmel.pollution.api.recipes.properties.AstralCondition;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import net.minecraft.block.state.IBlockState;
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
    protected IManaHatch manaPoolHatch;
    protected IBloodMagicHatch bloodMagicHatch;
    protected IAstralHatch astralLensHatch;
    protected ITarotHatch tarotHatch;

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
            DeclarativePatternBuilder builder, char symbol, IBlockState casingState,
            RecipeMap<?> recipeMap, int maxHatches) {
        return configureMagicRecipeCasing(builder, symbol, casingState,
                new RecipeMap<?>[]{recipeMap}, maxHatches, true);
    }

    protected static DeclarativePatternBuilder configureMagicRecipeCasing(
            DeclarativePatternBuilder builder, char symbol, IBlockState casingState,
            RecipeMap<?> recipeMap, int maxHatches, boolean includeMuffler) {
        return configureMagicRecipeCasing(builder, symbol, casingState,
                new RecipeMap<?>[]{recipeMap}, maxHatches, includeMuffler);
    }

    protected static DeclarativePatternBuilder configureMagicRecipeCasing(
            DeclarativePatternBuilder builder, char symbol, IBlockState casingState,
            RecipeMap<?>[] recipeMaps, int maxHatches) {
        return configureMagicRecipeCasing(builder, symbol, casingState, recipeMaps, maxHatches, true);
    }

    protected static DeclarativePatternBuilder configureMagicRecipeCasing(
            DeclarativePatternBuilder builder, char symbol, IBlockState casingState,
            RecipeMap<?>[] recipeMaps, int maxHatches, boolean includeMuffler) {
        List<MultiblockAbility<?>> abilities = new ArrayList<>();
        abilities.add(POMultiblockAbility.MANA_INPUT_HATCH);
        abilities.add(MultiblockAbility.INPUT_ENERGY);
        abilities.add(MultiblockAbility.MAINTENANCE_HATCH);
        if (includeMuffler) abilities.add(MultiblockAbility.MUFFLER_HATCH);
        abilities.add(POMultiblockAbility.VIS_HATCH);
        abilities.add(POMultiblockAbility.INFUSED_FLUID_HATCH);
        abilities.add(POMultiblockAbility.MANA_INPUT_POOL);
        abilities.add(POMultiblockAbility.BLOOD_MAGIC_HATCH);
        abilities.add(POMultiblockAbility.ASTRAL_LENS_HATCH);
        abilities.add(POMultiblockAbility.TAROT_HATCH);
        boolean importsItems = false;
        boolean exportsItems = false;
        boolean importsFluids = false;
        boolean exportsFluids = false;
        for (RecipeMap<?> recipeMap : recipeMaps) {
            importsItems |= recipeMap.getMaxInputs() > 0;
            exportsItems |= recipeMap.getMaxOutputs() > 0;
            importsFluids |= recipeMap.getMaxFluidInputs() > 0;
            exportsFluids |= recipeMap.getMaxFluidOutputs() > 0;
        }
        if (importsItems) abilities.add(MultiblockAbility.IMPORT_ITEMS);
        if (exportsItems) abilities.add(MultiblockAbility.EXPORT_ITEMS);
        if (importsFluids) abilities.add(MultiblockAbility.IMPORT_FLUIDS);
        if (exportsFluids) abilities.add(MultiblockAbility.EXPORT_FLUIDS);

        return builder
                .where(symbol, Elements.choice(
                        Elements.block(casingState),
                        Elements.abilities(0, maxHatches,
                                abilities.toArray(new MultiblockAbility<?>[0]))))
                .abilityGroup(POMultiblockAbility.MANA_INPUT_HATCH, 1, 2,
                        POMultiblockAbility.MANA_INPUT_HATCH, MultiblockAbility.INPUT_ENERGY)
                .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                .globalAbilityLimit(MultiblockAbility.MUFFLER_HATCH, includeMuffler ? 1 : 0, 1)
                .globalAbilityLimit(POMultiblockAbility.VIS_HATCH, 0, 1)
                .globalAbilityLimit(POMultiblockAbility.INFUSED_FLUID_HATCH, 1, 1)
                .globalAbilityLimit(POMultiblockAbility.MANA_INPUT_POOL, 0, 1)
                .globalAbilityLimit(POMultiblockAbility.BLOOD_MAGIC_HATCH, 0, 1)
                .globalAbilityLimit(POMultiblockAbility.ASTRAL_LENS_HATCH, 0, 1)
                .globalAbilityLimit(POMultiblockAbility.TAROT_HATCH, 0, 1);
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((manager, syncer) -> {
            if (syncer.syncBoolean(!checkInfusedFluid())) {
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
        return infusedFluidTank.getFluid() != null && infusedFluidTank.getFluid().getFluid() == getMaterial().getFluid();
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

        this.manaPoolHatch = getFirstAbility(POMultiblockAbility.MANA_INPUT_POOL);
        this.bloodMagicHatch = getFirstAbility(POMultiblockAbility.BLOOD_MAGIC_HATCH);
        this.astralLensHatch = getFirstAbility(POMultiblockAbility.ASTRAL_LENS_HATCH);
        this.tarotHatch = getFirstAbility(POMultiblockAbility.TAROT_HATCH);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        visHatch = null;
        infusedFluidTank = null;
        manaPoolHatch = null;
        bloodMagicHatch = null;
        astralLensHatch = null;
        tarotHatch = null;
    }

    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            int infusedAmount = syncer.syncInt(infusedFluidTank == null ? 0 : infusedFluidTank.getFluidAmount());
            keyManager.add(KeyUtil.string(TextFormatting.GRAY, "源质仓储量：" + getMaterial().getLocalizedName() + " " + infusedAmount + "L"));

            int visStore = syncer.syncInt(getVisStore());
            keyManager.add(KeyUtil.string(TextFormatting.GRAY, "灵气仓储量：" + visStore + "vis"));

            if (manaPoolHatch != null) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "纯魔力池：" + syncer.syncLong(manaPoolHatch.getMana())));
            }
            if (bloodMagicHatch != null) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "生命源质：" + syncer.syncInt(bloodMagicHatch.getLifeEssence())));
            }
            if (astralLensHatch != null) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "星辉焦点：" + astralLensHatch.getFocusedConstellation()));
            }
            if (tarotHatch != null) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "塔罗授权：" + tarotHatch.getActiveTarot()));
            }
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
        if (amount <= 0) return true;
        if (infusedFluidTank == null) return false;
        if (!checkInfusedFluid()) return false;
        if (infusedFluidTank.getFluidAmount() < amount) return false;
        if (simulate) return true;
        infusedFluidTank.drain(amount, true);
        return true;
    }

    public boolean consumeMana(long amount, boolean simulate) {
        return amount <= 0 || manaPoolHatch != null && manaPoolHatch.consumeMana(amount, simulate);
    }

    public boolean consumeLifeEssence(int amount, boolean simulate) {
        return amount <= 0 || bloodMagicHatch != null && bloodMagicHatch.consumeLifeEssence(amount, simulate);
    }

    /** Validates non-consumable magic authorizations before a recipe starts. */
    public boolean checkMagicRequirements(Recipe recipe) {
        long manaPerTick = recipe.getProperty(MagicRecipeProperties.MANA_PER_TICK, 0L);
        if (manaPerTick > 0 && manaPoolHatch == null) return false;

        int lifeEssencePerTick = recipe.getProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK, 0);
        if (lifeEssencePerTick > 0 && bloodMagicHatch == null) return false;

        if (recipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)
                && recipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0) > 0
                && visHatch == null) return false;

        AstralCondition condition = recipe.getProperty(MagicRecipeProperties.ASTRAL_CONDITION, AstralCondition.NONE);
        if (condition.isConfigured() && (astralLensHatch == null || !astralLensHatch.matches(condition))) return false;

        String tarot = recipe.getProperty(MagicRecipeProperties.TAROT, "");
        return tarot.isEmpty() || tarotHatch != null && tarotHatch.hasTarot(tarot);
    }

    private <T> T getFirstAbility(MultiblockAbility<T> ability) {
        List<T> abilities = getAbilities(ability);
        return abilities == null || abilities.isEmpty() ? null : abilities.get(0);
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("- 魔导仓支持："));
        tooltip.add(TextFormatting.GRAY + I18n.format("可选安装灵气、纯魔力池、血魔法、星辉透镜与塔罗牌仓。"));
        tooltip.add(TextFormatting.GRAY + I18n.format("只有配方属性声明的条件才会强制对应仓口；未声明配方保留旧行为。"));
    }
}
