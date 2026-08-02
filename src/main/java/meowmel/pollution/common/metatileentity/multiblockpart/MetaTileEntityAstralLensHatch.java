package meowmel.pollution.common.metatileentity.multiblockpart;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import hellfirepvp.astralsorcery.common.constellation.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import meowmel.pollution.api.capability.IAstralHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.recipes.properties.AstralCondition;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * A sky-facing Astral Sorcery focus. It deliberately evaluates the live server
 * constellation state rather than trusting item NBT or client time.
 */
public class MetaTileEntityAstralLensHatch extends MetaTileEntityMagicItemHatch
        implements IMultiblockAbilityPart<IAstralHatch>, IAstralHatch {

    public MetaTileEntityAstralLensHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAstralLensHatch(metaTileEntityId, getTier());
    }

    @Override
    protected boolean isAcceptedStack(ItemStack stack) {
        return getConstellation(stack) != null;
    }

    @Override
    protected SimpleOverlayRenderer getOverlay() {
        return POTextures.ASTRAL_LENS_HATCH;
    }

    @Nullable
    private IConstellation getConstellation(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.getItem() instanceof ItemConstellationPaper) {
            return ItemConstellationPaper.getConstellation(stack);
        }
        if (stack.getItem() instanceof ItemTunedCrystalBase) {
            return ((ItemTunedCrystalBase) stack.getItem()).getFocusConstellation(stack);
        }
        if (ItemStack.areItemsEqual(stack, PollutionMetaItems.CONSTELLATION_DATA_WAFER.getStackForm())
                && stack.hasTagCompound()) {
            return IConstellation.readFromNBT(stack.getTagCompound());
        }
        return null;
    }

    @Override
    public String getFocusedConstellation() {
        IConstellation constellation = getConstellation(getFocusStack());
        return constellation == null ? "" : constellation.getSimpleName().toLowerCase(Locale.ROOT);
    }

    @Nullable
    private WorldSkyHandler getWorldSkyHandler() {
        if (getWorld() == null) return null;
        return ConstellationSkyHandler.getInstance().getWorldHandler(getWorld());
    }

    @Override
    public boolean isSkyVisible() {
        return getWorld() != null && getWorld().canSeeSky(getPos().up());
    }

    @Override
    public boolean isNight() {
        return getWorld() != null && ConstellationSkyHandler.getInstance().isNight(getWorld());
    }

    @Override
    public boolean isFocusedConstellationActive() {
        IConstellation focus = getConstellation(getFocusStack());
        WorldSkyHandler handler = getWorldSkyHandler();
        return focus != null && handler != null && handler.isActive(focus);
    }

    @Override
    public float getFocusedDistribution() {
        IConstellation focus = getConstellation(getFocusStack());
        WorldSkyHandler handler = getWorldSkyHandler();
        if (focus == null || handler == null) return 0.0F;
        Float distribution = handler.getCurrentDistribution(focus, Function.identity());
        return distribution == null ? 0.0F : distribution;
    }

    @Override
    public String getMoonPhase() {
        WorldSkyHandler handler = getWorldSkyHandler();
        return handler == null || handler.getCurrentMoonPhase() == null
                ? "" : handler.getCurrentMoonPhase().name();
    }

    @Override
    public String getCelestialEvent() {
        WorldSkyHandler handler = getWorldSkyHandler();
        return handler == null || handler.getCurrentlyActiveEvent() == null
                ? "" : handler.getCurrentlyActiveEvent().name();
    }

    @Override
    public boolean matches(AstralCondition condition) {
        if (condition == null || !condition.isConfigured()) return true;
        IConstellation focus = getConstellation(getFocusStack());
        if (focus == null || getWorld() == null || !isSkyVisible()) return false;

        ConstellationSkyHandler skyHandler = ConstellationSkyHandler.getInstance();
        WorldSkyHandler worldHandler = getWorldSkyHandler();
        if (worldHandler == null) return false;
        if (condition.isNightRequired() && !skyHandler.isNight(getWorld())) return false;

        if (!condition.getMoonPhase().isEmpty()) {
            try {
                if (worldHandler.getCurrentMoonPhase() != MoonPhase.valueOf(condition.getMoonPhase().toUpperCase(Locale.ROOT))) {
                    return false;
                }
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }

        if (!condition.getCelestialEvent().isEmpty()) {
            try {
                if (worldHandler.getCurrentlyActiveEvent() != CelestialEvent.valueOf(
                        condition.getCelestialEvent().toUpperCase(Locale.ROOT))) {
                    return false;
                }
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }

        if (!condition.getConstellation().isEmpty()) {
            IConstellation target = ConstellationRegistry.getConstellationByName(condition.getConstellation());
            if (target == null || !focus.getSimpleName().equalsIgnoreCase(target.getSimpleName())
                    || !worldHandler.isActive(target)) {
                return false;
            }
            Float distribution = worldHandler.getCurrentDistribution(target, Function.identity());
            return distribution != null && distribution >= condition.getMinimumDistribution();
        }
        return true;
    }

    @Override
    public MultiblockAbility<IAstralHatch> getAbility() {
        return POMultiblockAbility.ASTRAL_LENS_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        String key = getTier() >= GTValues.LuV
                ? "pollution.machine.astral_lens_hatch_advanced.tooltip."
                : "pollution.machine.astral_lens_hatch.tooltip.";
        tooltip.add(I18n.format(key + "1"));
        tooltip.add(I18n.format(key + "2"));
    }
}
