package meowmel.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.api.util.KeyUtil;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.screen.ModularPanel;
import meowmel.pollution.api.capability.IAstralHatch;
import meowmel.pollution.api.capability.IBloodMagicHatch;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.capability.ITarotHatch;
import meowmel.pollution.api.capability.IVisHatch;
import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import meowmel.pollution.api.amplification.AstralAmplifierSnapshot;
import meowmel.pollution.api.amplification.MagicAmplificationResult;
import meowmel.pollution.api.amplification.MagicAmplificationEngine;
import meowmel.pollution.api.amplification.MagicProcessTag;
import meowmel.pollution.api.amplification.MagicMachineProfileRegistry;
import meowmel.pollution.api.recipes.properties.AstralCondition;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.client.gui.AstralConstellationPanelWidget;
import com.cleanroommc.modularui.value.sync.GenericSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.utils.Alignment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

    @Override
    protected MultiblockUIFactory createUIFactory() {
        // Preserve the MultiMap controller's stock factory: it owns the recipe
        // selector and all standard bottom/side controls.
        return super.createUIFactory().addScreenChildren((screen, syncManager) -> {
            GenericSyncValue<String> state = syncManager.getOrCreateSyncHandler("pollution_astral_mini", 0,
                    GenericSyncValue.class, () -> GenericSyncValue.builder(String.class)
                            .getter(this::getAstralPanelState)
                            .serializer((buffer, value) -> buffer.writeString(value))
                            .deserializer(buffer -> buffer.readString(256))
                            .copyImmutable()
                            .build());
            // This factory is executed on both logical sides before the parent
            // panel is initialised. Build the client-only secondary panel on
            // its first click, not during initial screen construction.
            final IPanelHandler[] astralWindow = new IPanelHandler[1];
            ButtonWidget<?> toggle = new ButtonWidget<>()
                    .size(18)
                    .top(3)
                    .right(3)
                    .onMousePressed(button -> {
                        if (button != 0) return false;
                        if (astralWindow[0] == null) {
                            astralWindow[0] = IPanelHandler.simple(screen.getPanel(), (parent, player) ->
                                    new ModularPanel("pollution_astral_constellation")
                                            .size(AstralConstellationPanelWidget.WIDTH, AstralConstellationPanelWidget.HEIGHT)
                                            .relative(parent)
                                            .left(-184)
                                            .top(0)
                                            .child(new AstralConstellationPanelWidget(state))
                                            .child(ButtonWidget.panelCloseButton()), true);
                        }
                        astralWindow[0].openPanel();
                        return true;
                    });
            toggle.child(new TextWidget<>("✦")
                    .textAlign(Alignment.Center)
                    .size(18));
            screen.child(toggle);

            // Open once the client main panel is valid.  The screen builder
            // itself also runs server-side, so opening here would break every
            // machine GUI on an integrated/dedicated server.
            screen.onUpdateListener(widget -> {
                if (!FMLCommonHandler.instance().getSide().isClient() || astralWindow[0] != null) return;
                astralWindow[0] = IPanelHandler.simple(widget.getPanel(), (parent, player) ->
                        new ModularPanel("pollution_astral_constellation")
                                .size(AstralConstellationPanelWidget.WIDTH, AstralConstellationPanelWidget.HEIGHT)
                                .relative(parent)
                                .left(-184)
                                .top(0)
                                .child(new AstralConstellationPanelWidget(state))
                                .child(ButtonWidget.panelCloseButton()), true);
                astralWindow[0].openPanel();
            }, true);
        });
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
            Recipe currentRecipe = recipeMapWorkable.getPreviousRecipe();
            AstralCondition condition = currentRecipe == null ? AstralCondition.NONE
                    : currentRecipe.getProperty(MagicRecipeProperties.ASTRAL_CONDITION, AstralCondition.NONE);
            if (syncer.syncBoolean(condition.isConfigured())) {
                if (syncer.syncBoolean(astralLensHatch == null)) {
                    manager.add(KeyUtil.string(TextFormatting.RED, "当前配方缺少星辉透镜仓"));
                } else if (syncer.syncBoolean(!astralLensHatch.matches(condition))) {
                    manager.add(KeyUtil.string(TextFormatting.RED,
                            "星座数据、透镜焦点或实时天空与当前配方不一致"));
                }
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
        if (syncer.syncBoolean(isStructureFormed())) {
            int infusedAmount = syncer.syncInt(infusedFluidTank == null ? 0 : infusedFluidTank.getFluidAmount());
            keyManager.add(KeyUtil.string(TextFormatting.GRAY, "源质仓储量：" + getMaterial().getLocalizedName() + " " + infusedAmount + "L"));

            int visStore = syncer.syncInt(getVisStore());
            keyManager.add(KeyUtil.string(TextFormatting.GRAY, "灵气仓储量：" + visStore + "vis"));

            if (syncer.syncBoolean(manaPoolHatch != null)) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "纯魔力池：" +
                        syncer.syncLong(manaPoolHatch == null ? 0L : manaPoolHatch.getMana())));
            }
            if (syncer.syncBoolean(bloodMagicHatch != null)) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "生命源质：" +
                        syncer.syncInt(bloodMagicHatch == null ? 0 : bloodMagicHatch.getLifeEssence())));
            }
            if (syncer.syncBoolean(astralLensHatch != null)) {
                String constellation = syncer.syncString(astralLensHatch == null ? "" : astralLensHatch.getFocusedConstellation());
                boolean skyVisible = syncer.syncBoolean(astralLensHatch != null && astralLensHatch.isSkyVisible());
                boolean night = syncer.syncBoolean(astralLensHatch != null && astralLensHatch.isNight());
                boolean active = syncer.syncBoolean(astralLensHatch != null && astralLensHatch.isFocusedConstellationActive());
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "星辉焦点：" +
                        (constellation.isEmpty() ? "未调谐" : constellation)));
                keyManager.add(KeyUtil.string(skyVisible ? TextFormatting.GREEN : TextFormatting.RED,
                        "露天状态：" + (skyVisible ? "可见天空" : "被遮挡")));
                keyManager.add(KeyUtil.string(TextFormatting.GRAY,
                        "天空状态：" + (night ? "夜间" : "日间") +
                                " / 月相 " + syncer.syncString(astralLensHatch == null ? "" : astralLensHatch.getMoonPhase())));
                keyManager.add(KeyUtil.string(TextFormatting.GRAY,
                        "天象事件：" + syncer.syncString(astralLensHatch == null ? "" : astralLensHatch.getCelestialEvent())));
                keyManager.add(KeyUtil.string(active ? TextFormatting.AQUA : TextFormatting.RED,
                        "星座活跃度：" + String.format("%.1f%%",
                                syncer.syncDouble(astralLensHatch == null ? 0.0D
                                        : astralLensHatch.getFocusedDistribution() * 100.0F))));
            }
            if (syncer.syncBoolean(astralLensHatch != null && astralLensHatch.hasConstellationDataWafer())) {
                int baseStrength = astralLensHatch != null && astralLensHatch.getTier() >= gregtech.api.GTValues.LuV ? 30 : 10;
                boolean skyMatched = syncer.syncBoolean(astralLensHatch != null && astralLensHatch.isSkyVisible()
                        && astralLensHatch.isFocusedConstellationActive());
                keyManager.add(KeyUtil.string(TextFormatting.LIGHT_PURPLE,
                        "星座数据晶圆：常驻 " + baseStrength + "%"
                                + (skyMatched ? "，天相匹配额外 +10%" : "")));
            }
            if (recipeMapWorkable instanceof MagicMultiblockRecipeLogic) {
                MagicAmplificationResult result = ((MagicMultiblockRecipeLogic) recipeMapWorkable)
                        .getActiveAmplification();
                if (syncer.syncBoolean(result.isActive())) {
                    keyManager.add(KeyUtil.string(TextFormatting.AQUA,
                            "魔导增幅：耗时 -" + percent(result.getDurationReduction())
                                    + "% / EU/t -" + percent(result.getEutReduction()) + "% / 并行 +"
                                    + result.getExtraParallel()));
                    if (result.getMagicCostReduction() > 0.0D) {
                        keyManager.add(KeyUtil.string(TextFormatting.LIGHT_PURPLE,
                                "魔法介质消耗 -" + percent(result.getMagicCostReduction()) + "%"));
                    }
                    if (result.getOutputBonus() > 0.0D || result.getChanceExtraRoll() > 0.0D) {
                        keyManager.add(KeyUtil.string(TextFormatting.GOLD,
                                "安全产物 +" + percent(result.getOutputBonus()) + "% / 概率额外判定 "
                                        + percent(result.getChanceExtraRoll()) + "%"));
                    }
                    if (result.getCatalystSaveChance() > 0.0D || result.getProgressRetentionTicks() > 0) {
                        keyManager.add(KeyUtil.string(TextFormatting.GREEN,
                                "催化剂保护 " + percent(result.getCatalystSaveChance()) + "% / 进度保持 "
                                        + result.getProgressRetentionTicks() + " tick"));
                    }
                    if (result.getFurnaceTemperatureBonus() > 0) {
                        keyManager.add(KeyUtil.string(TextFormatting.RED,
                                "有效炉温 +" + result.getFurnaceTemperatureBonus() + " K"));
                    }
                }
            }
            if (tarotHatch != null) {
                keyManager.add(KeyUtil.string(TextFormatting.GRAY, "塔罗授权：" + tarotHatch.getActiveTarot()));
            }
        }
    }

    /**
     * Compact server-authoritative state for the left-hand constellation panel.
     * The actual star geometry is resolved client-side from Astral Sorcery's
     * registered constellation, so only dynamic state crosses the GUI sync.
     */
    public String getAstralPanelState() {
        if (!isStructureFormed() || astralLensHatch == null || !astralLensHatch.hasConstellationDataWafer()) {
            return "";
        }
        String constellation = astralLensHatch.getFocusedConstellation();
        if (constellation.isEmpty()) return "";

        int baseStrength = astralLensHatch.getTier() >= gregtech.api.GTValues.LuV ? 30 : 10;
        boolean skyMatched = astralLensHatch.isSkyVisible() && astralLensHatch.isFocusedConstellationActive();
        int distribution = Math.round(astralLensHatch.getFocusedDistribution() * 100.0F);
        MagicAmplificationResult result = recipeMapWorkable instanceof MagicMultiblockRecipeLogic
                ? ((MagicMultiblockRecipeLogic) recipeMapWorkable).getActiveAmplification()
                : MagicAmplificationResult.NONE;
        return constellation + "|" + baseStrength + "|" + skyMatched + "|" + distribution + "|"
                + percent(result.getDurationReduction()) + "|" + percent(result.getEutReduction()) + "|"
                + percent(result.getMagicCostReduction()) + "|" + result.getExtraParallel() + "|"
                + percent(result.getOutputBonus()) + "|" + percent(result.getChanceExtraRoll()) + "|"
                + percent(result.getCatalystSaveChance()) + "|" + result.getFurnaceTemperatureBonus();
    }

    private static int percent(double value) {
        return (int) Math.round(value * 100.0D);
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
        if (!tarot.isEmpty() && (tarotHatch == null || !tarotHatch.hasTarot(tarot))) return false;

        long tags = getMagicProcessTags(recipe);
        if (MagicProcessTag.hasAny(tags, MagicProcessTag.EXPERIMENTAL)
                && !hasTarot("the_fool")) return false;
        if (MagicProcessTag.hasAny(tags, MagicProcessTag.MAGIC_CONVERSION)
                && !hasTarot("the_magician")) return false;
        if (MagicProcessTag.hasAny(tags, MagicProcessTag.HIDDEN_RITUAL)
                && !hasTarot("the_high_priestess")) return false;
        if (MagicProcessTag.hasAny(tags, MagicProcessTag.RECYCLING)
                && !hasTarot("death") && !hasTarot("judgement")) return false;
        return !MagicProcessTag.hasAny(tags, MagicProcessTag.THREE_MAGIC_SYSTEMS) || hasTarot("the_world");
    }

    private boolean hasTarot(String tarotId) {
        return tarotHatch != null && tarotHatch.hasTarot(tarotId);
    }

    /**
     * Recipe tags override the conservative per-machine profile. Old recipes
     * therefore receive only their machine's safe first-batch enhancement
     * until a content patch declares their exact process domain.
     */
    public long getMagicProcessTags(Recipe recipe) {
        if (recipe != null && recipe.hasProperty(MagicRecipeProperties.PROCESS_TAG_MASK)) {
            return recipe.getProperty(MagicRecipeProperties.PROCESS_TAG_MASK, 0L);
        }
        return MagicMachineProfileRegistry.getFallbackTags(metaTileEntityId);
    }

    public AstralAmplifierSnapshot getAstralAmplifierSnapshot() {
        return AstralAmplifierSnapshot.from(astralLensHatch);
    }

    public ITarotHatch getTarotHatch() {
        return tarotHatch;
    }

    /** Preview used by temperature-gated machines before GT consumes recipe inputs. */
    public MagicAmplificationResult getMagicAmplificationPreview(Recipe recipe, boolean singleParallel) {
        return MagicAmplificationEngine.calculate(getMagicProcessTags(recipe), recipe.getDuration(),
                getAstralAmplifierSnapshot(), tarotHatch, 0, singleParallel);
    }

    /** Keep the non-consumable wafer/card stable for one running recipe. */
    public void setMagicFocusLocked(boolean locked) {
        if (astralLensHatch != null) astralLensHatch.setFocusLocked(locked);
        if (tarotHatch != null) tarotHatch.setFocusLocked(locked);
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
