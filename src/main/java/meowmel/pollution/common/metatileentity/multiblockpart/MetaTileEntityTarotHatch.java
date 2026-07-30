package meowmel.pollution.common.metatileentity.multiblockpart;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import meowmel.pollution.api.capability.ITarotHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.recipes.properties.TarotCards;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Non-consumable major-arcana selector for ritual-grade magic recipes. */
public class MetaTileEntityTarotHatch extends MetaTileEntityMagicItemHatch
        implements IMultiblockAbilityPart<ITarotHatch>, ITarotHatch {

    public MetaTileEntityTarotHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityTarotHatch(metaTileEntityId, getTier());
    }

    @Override
    protected boolean isAcceptedStack(ItemStack stack) {
        return TarotCards.isTarot(stack);
    }

    @Override
    protected SimpleOverlayRenderer getOverlay() {
        return POTextures.TAROT_HATCH;
    }

    @Override
    public String getActiveTarot() {
        String tarot = TarotCards.getId(getFocusStack());
        return tarot == null ? "" : tarot;
    }

    @Override
    public boolean hasTarot(String tarotId) {
        return TarotCards.matches(getFocusStack(), tarotId);
    }

    @Override
    public MultiblockAbility<ITarotHatch> getAbility() {
        return POMultiblockAbility.TAROT_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.tarot_hatch.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.tarot_hatch.tooltip.2"));
    }
}
