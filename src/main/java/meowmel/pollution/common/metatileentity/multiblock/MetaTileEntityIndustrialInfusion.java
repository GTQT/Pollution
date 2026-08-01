package meowmel.pollution.common.metatileentity.multiblock;

import com.mojang.authlib.GameProfile;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.ICasing;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.api.pattern.POTieredCasingGroups;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

import java.util.List;
import java.util.StringJoiner;


public class MetaTileEntityIndustrialInfusion extends RecipeMapMultiblockController {
    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:industrial_infusion", () -> DeclarativePatternBuilder.start()
                .aisle("             ABA             ", "          BBBABABBB          ", "             ABA             ")
                .aisle("           BBABABB           ", "        BBB C   C BBB        ", "           BBABABB           ")
                .aisle("         BB  ABA  BB         ", "      BBB CBBABABBC BBB      ", "         BB  ABA  BB         ")
                .aisle("       BB           BB       ", "    BBB CBBB D D BBBC BBB    ", "       BB           BB       ")
                .aisle("     BB               BB     ", "   BB CBBB   D D   BBBC BB   ", "     BB               BB     ")
                .aisle("    B  E             E  B    ", "   B BBB     D D     BBB B   ", "    B  E             E  B    ")
                .aisle("    B  E             E  B    ", "  BBCB       D D       BCBB  ", "    B  E             E  B    ")
                .aisle("   B EEE      D      EEE B   ", "  B BB ED    DED    DE BB B  ", "   B EEE      D      EEE B   ")
                .aisle("   B    D     D     D    B   ", " BBCB  DED         DED  BCBB ", "   B    D     D     D    B   ")
                .aisle("  B      D    D    D      B  ", " B BB   DED       DED   BB B ", "  B      D    D    D      B  ")
                .aisle("  B       A   D   A       B  ", "BBCB     DEA  F  AED     BCBB", "  B       A   D   A       B  ")
                .aisle(" B         A     A         B ", "B BB      AEFFFFFEA      BB B", " B         A     A         B ")
                .aisle(" B                         B ", "BCB        FF G FF        BCB", " B                         B ")
                .aisle("AAA          GGG          AAA", "A ADDDDD   F GGG F   DDDDDA A", "AAA          GGG          AAA")
                .aisle("BBB    DDDD  GGG  DDDD    BBB", "B B    E  FFGGGGGFF  E    B B", "BBB    DDDD  GGG  DDDD    BBB")
                .aisle("AAA          GGG          AAA", "A ADDDDD   F GGG F   DDDDDA A", "AAA          GGG          AAA")
                .aisle(" B                         B ", "BCB        FF G FF        BCB", " B                         B ")
                .aisle(" B         A     A         B ", "B BB      AEFFFFFEA      BB B", " B         A     A         B ")
                .aisle("  B       A   D   A       B  ", "BBCB     DEA  F  AED     BCBB", "  B       A   D   A       B  ")
                .aisle("  B      D    D    D      B  ", " B BB   DED       DED   BB B ", "  B      D    D    D      B  ")
                .aisle("   B    D     D     D    B   ", " BBCB  DED         DED  BCBB ", "   B    D     D     D    B   ")
                .aisle("   B EEE      D      EEE B   ", "  B BB ED    DED    DE BB B  ", "   B EEE      D      EEE B   ")
                .aisle("    B  E             E  B    ", "  BBCB       D D       BCBB  ", "    B  E             E  B    ")
                .aisle("    B  E             E  B    ", "   B BBB     D D     BBB B   ", "    B  E             E  B    ")
                .aisle("     BB               BB     ", "   BB CBBB   D D   BBBC BB   ", "     BB               BB     ")
                .aisle("       BB           BB       ", "    BBB CBBB D D BBBC BBB    ", "       BB           BB       ")
                .aisle("         BB  ABA  BB         ", "      BBB CBBABABBC BBB      ", "         BB  ABA  BB         ")
                .aisle("           BBABABB           ", "        BBB C   C BBB        ", "           BBABABB           ")
                .aisle("             ABA             ", "          BBBASABBB          ", "             ABA             ")
                .self('S', MetaTileEntityIndustrialInfusion.class)
                .tieredCasing('A', POTieredCasingGroups.coilCasings().group()).withChannel(POTieredCasingGroups.coilCasings().channel())
                .where('B', Elements.choice(Elements.block(getCasingState()),
                        Elements.abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.IMPORT_ITEMS,
                                MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
                                MultiblockAbility.EXPORT_FLUIDS)))
                .block('C', getCasingState1()).block('D', getCasingState2()).block('E', getCasingState3())
                .block('F', getCasingState4())
                .tieredCasing('G', POTieredCasingGroups.glasses().group()).withChannel(POTieredCasingGroups.glasses().channel())
                .any(' ')
                .buildStructureDefinition());
    int glass;
    int coil;
    private String lastRequiredResearch = "";
    private Recipe lastDiagnosticRecipe;

    private static final byte RESEARCH_WAITING = 0;
    private static final byte OWNER_MISSING = 1;
    private static final byte OWNER_OFFLINE = 2;
    private static final byte RESEARCH_LOCKED = 3;
    private static final byte RESEARCH_READY = 4;

    public MetaTileEntityIndustrialInfusion(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.INDUSTRIAL_INFUSION_RECIPES);
        this.recipeMapWorkable = new IndustrialInfusionRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID);
    }

    private static IBlockState getCasingState1() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TITANIUM_PIPE);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TITANIUM_GEARBOX);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.MAGIC_BATTERY);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityIndustrialInfusion(this.metaTileEntityId);
    }

    private EntityPlayerMP getBoundPlayer() {
        if (getOwnerGT() == null || getWorld() == null || getWorld().getMinecraftServer() == null) {
            return null;
        }
        return getWorld().getMinecraftServer().getPlayerList().getPlayerByUUID(getOwnerGT());
    }

    private String getBoundPlayerName() {
        if (getOwnerGT() == null) return "-";
        EntityPlayerMP onlinePlayer = getBoundPlayer();
        if (onlinePlayer != null) return onlinePlayer.getName();
        if (getWorld() != null && getWorld().getMinecraftServer() != null) {
            GameProfile profile = getWorld().getMinecraftServer().getPlayerProfileCache()
                    .getProfileByUUID(getOwnerGT());
            if (profile != null && profile.getName() != null) return profile.getName();
        }
        return getOwnerGT().toString();
    }

    private String getDisplayedResearch() {
        Recipe activeRecipe = recipeMapWorkable.getPreviousRecipe();
        if (activeRecipe != null && recipeMapWorkable.isActive()) {
            String activeResearch = activeRecipe.getProperty(MagicRecipeProperties.THAUMCRAFT_RESEARCH, "");
            if (!activeResearch.isEmpty()) return activeResearch;
        }
        return lastRequiredResearch;
    }

    private byte getResearchStatus() {
        if (getOwnerGT() == null) return OWNER_MISSING;
        EntityPlayerMP player = getBoundPlayer();
        if (player == null) return OWNER_OFFLINE;
        String research = getDisplayedResearch();
        if (research.isEmpty()) return RESEARCH_WAITING;
        return ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)
                ? RESEARCH_READY
                : RESEARCH_LOCKED;
    }

    @Override
    public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
        if (!super.checkRecipe(recipe, consumeIfSuccess)) return false;

        lastDiagnosticRecipe = recipe;
        String research = recipe.getProperty(MagicRecipeProperties.THAUMCRAFT_RESEARCH, "");
        lastRequiredResearch = research;
        EntityPlayerMP player = getBoundPlayer();
        if (player == null) return false;

        return research.isEmpty() || ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research);
    }

    @Override
    public void update() {
        super.update();
        if (getWorld() != null && !getWorld().isRemote && !recipeMapWorkable.isActive()
                && getOffsetTimer() % 20L == 0L && getResearchStatus() == RESEARCH_READY) {
            recipeMapWorkable.forceRecipeRecheck();
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        super.configureDisplayText(builder);
        builder.addCustom((manager, syncer) -> {
            String ownerName = syncer.syncString(getBoundPlayerName());
            String research = syncer.syncString(getDisplayedResearch());
            String missingItems = syncer.syncString(getMissingItemsText());
            String missingFluids = syncer.syncString(getMissingFluidsText());
            byte status = syncer.syncByte(getResearchStatus());

            manager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "pollution.machine.industrial_infusion.owner", ownerName));
            if (!research.isEmpty()) {
                manager.add(KeyUtil.lang(TextFormatting.GRAY,
                        "pollution.machine.industrial_infusion.required_research", research));
            }
            manager.add(KeyUtil.lang(status == RESEARCH_READY ? TextFormatting.GREEN : TextFormatting.YELLOW,
                    getResearchStatusTranslationKey(status)));
            if (!missingItems.isEmpty()) {
                manager.add(KeyUtil.lang(TextFormatting.YELLOW,
                        "pollution.machine.industrial_infusion.missing_items", missingItems));
            }
            if (!missingFluids.isEmpty()) {
                manager.add(KeyUtil.lang(TextFormatting.YELLOW,
                        "pollution.machine.industrial_infusion.missing_fluids", missingFluids));
            }
        });
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((manager, syncer) -> {
            byte status = syncer.syncByte(getResearchStatus());
            if (status == OWNER_MISSING || status == OWNER_OFFLINE || status == RESEARCH_LOCKED) {
                manager.add(KeyUtil.lang(TextFormatting.RED, getResearchStatusTranslationKey(status)));
            }
        });
    }

    private String getResearchStatusTranslationKey(byte status) {
        switch (status) {
            case OWNER_MISSING:
                return "pollution.machine.industrial_infusion.status.owner_missing";
            case OWNER_OFFLINE:
                return "pollution.machine.industrial_infusion.status.owner_offline";
            case RESEARCH_LOCKED:
                return "pollution.machine.industrial_infusion.status.research_locked";
            case RESEARCH_READY:
                return "pollution.machine.industrial_infusion.status.research_ready";
            default:
                return "pollution.machine.industrial_infusion.status.waiting";
        }
    }

    private Recipe getDiagnosticRecipe() {
        Recipe previousRecipe = recipeMapWorkable.getPreviousRecipe();
        return previousRecipe != null ? previousRecipe : lastDiagnosticRecipe;
    }

    private String getMissingItemsText() {
        Recipe recipe = getDiagnosticRecipe();
        if (recipe == null || getInputInventory() == null) return "";

        StringJoiner missing = new StringJoiner(", ");
        int displayed = 0;
        for (GTRecipeInput input : recipe.getInputs()) {
            int available = 0;
            for (int slot = 0; slot < getInputInventory().getSlots(); slot++) {
                ItemStack stack = getInputInventory().getStackInSlot(slot);
                if (!stack.isEmpty() && input.acceptsStack(stack)) available += stack.getCount();
            }
            int amountMissing = Math.max(0, input.getAmount() - available);
            if (amountMissing > 0) {
                ItemStack[] displayStacks = input.getInputStacks();
                String name = displayStacks.length > 0 && !displayStacks[0].isEmpty()
                        ? displayStacks[0].getDisplayName()
                        : "?";
                missing.add(name + " ×" + amountMissing);
                if (++displayed >= 3) break;
            }
        }
        return missing.toString();
    }

    private String getMissingFluidsText() {
        Recipe recipe = getDiagnosticRecipe();
        if (recipe == null || getInputFluidInventory() == null) return "";

        StringJoiner missing = new StringJoiner(", ");
        int displayed = 0;
        for (GTRecipeInput input : recipe.getFluidInputs()) {
            int available = 0;
            for (int tank = 0; tank < getInputFluidInventory().getTanks(); tank++) {
                FluidStack stack = getInputFluidInventory().getTankAt(tank).getFluid();
                if (stack != null && input.acceptsFluid(stack)) available += stack.amount;
            }
            int amountMissing = Math.max(0, input.getAmount() - available);
            if (amountMissing > 0) {
                FluidStack displayStack = input.getInputFluidStack();
                String name = displayStack == null ? "?" : displayStack.getLocalizedName();
                missing.add(name + " " + amountMissing + " mB");
                if (++displayed >= 3) break;
            }
        }
        return missing.toString();
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
        /*
        return FactoryBlockPattern.start()
                .aisle("             ABA             ", "          BBBABABBB          ", "             ABA             ")
                .aisle("           BBABABB           ", "        BBB C   C BBB        ", "           BBABABB           ")
                .aisle("         BB  ABA  BB         ", "      BBB CBBABABBC BBB      ", "         BB  ABA  BB         ")
                .aisle("       BB           BB       ", "    BBB CBBB D D BBBC BBB    ", "       BB           BB       ")
                .aisle("     BB               BB     ", "   BB CBBB   D D   BBBC BB   ", "     BB               BB     ")
                .aisle("    B  E             E  B    ", "   B BBB     D D     BBB B   ", "    B  E             E  B    ")
                .aisle("    B  E             E  B    ", "  BBCB       D D       BCBB  ", "    B  E             E  B    ")
                .aisle("   B EEE      D      EEE B   ", "  B BB ED    DED    DE BB B  ", "   B EEE      D      EEE B   ")
                .aisle("   B    D     D     D    B   ", " BBCB  DED         DED  BCBB ", "   B    D     D     D    B   ")
                .aisle("  B      D    D    D      B  ", " B BB   DED       DED   BB B ", "  B      D    D    D      B  ")
                .aisle("  B       A   D   A       B  ", "BBCB     DEA  F  AED     BCBB", "  B       A   D   A       B  ")
                .aisle(" B         A     A         B ", "B BB      AEFFFFFEA      BB B", " B         A     A         B ")
                .aisle(" B                         B ", "BCB        FF G FF        BCB", " B                         B ")
                .aisle("AAA          GGG          AAA", "A ADDDDD   F GGG F   DDDDDA A", "AAA          GGG          AAA")
                .aisle("BBB    DDDD  GGG  DDDD    BBB", "B B    E  FFGGGGGFF  E    B B", "BBB    DDDD  GGG  DDDD    BBB")
                .aisle("AAA          GGG          AAA", "A ADDDDD   F GGG F   DDDDDA A", "AAA          GGG          AAA")
                .aisle(" B                         B ", "BCB        FF G FF        BCB", " B                         B ")
                .aisle(" B         A     A         B ", "B BB      AEFFFFFEA      BB B", " B         A     A         B ")
                .aisle("  B       A   D   A       B  ", "BBCB     DEA  F  AED     BCBB", "  B       A   D   A       B  ")
                .aisle("  B      D    D    D      B  ", " B BB   DED       DED   BB B ", "  B      D    D    D      B  ")
                .aisle("   B    D     D     D    B   ", " BBCB  DED         DED  BCBB ", "   B    D     D     D    B   ")
                .aisle("   B EEE      D      EEE B   ", "  B BB ED    DED    DE BB B  ", "   B EEE      D      EEE B   ")
                .aisle("    B  E             E  B    ", "  BBCB       D D       BCBB  ", "    B  E             E  B    ")
                .aisle("    B  E             E  B    ", "   B BBB     D D     BBB B   ", "    B  E             E  B    ")
                .aisle("     BB               BB     ", "   BB CBBB   D D   BBBC BB   ", "     BB               BB     ")
                .aisle("       BB           BB       ", "    BBB CBBB D D BBBC BBB    ", "       BB           BB       ")
                .aisle("         BB  ABA  BB         ", "      BBB CBBABABBC BBB      ", "         BB  ABA  BB         ")
                .aisle("           BBABABB           ", "        BBB C   C BBB        ", "           BBABABB           ")
                .aisle("             ABA             ", "          BBBASABBB          ", "             ABA             ")
                .where('S', selfPredicate())
                .where('A', CP_COIL_CASING.get())
                .where('B', states(getCasingState())
                        .or(autoAbilities()))
                .where('C', states(getCasingState1()))
                .where('D', states(getCasingState2()))
                .where('E', states(getCasingState3()))
                .where('F', states(getCasingState4()))
                .where('G', CP_GLASS.get())
                .where(' ', any())
                .build();*/
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_VOID;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        ICasing coil = POTieredCasingGroups.coilCasings().channel().getMatchedCasing(formed);
        ICasing glass = POTieredCasingGroups.glasses().channel().getMatchedCasing(formed);
        this.coil = coil == null ? 0 : coil.getTier();
        this.glass = glass == null ? 0 : glass.getTier();
    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.industrial_infusion.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.industrial_infusion.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.industrial_infusion.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.industrial_infusion.tooltip.4"));
    }

    protected class IndustrialInfusionRecipeLogic extends MultiblockRecipeLogic {
        public IndustrialInfusionRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public int getParallelLimit() {
            return (int) Math.pow(2, coil);
        }

        @Override
        protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPost(ocResult, storage);

            ocResult.setDuration(Math.round((float) (ocResult.duration() * (100 - 5 * glass)) / 100));
        }
    }
}
