package meowmel.pollution.common.metatileentity.multiblockpart;

import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.item.ItemBloodOrb;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import meowmel.pollution.api.capability.IBloodMagicHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Reads LP directly from the Blood Magic network bound to the inserted blood orb.
 * The orb is never consumed; it is the explicit ownership and authorization token.
 */
public class MetaTileEntityBloodMagicHatch extends MetaTileEntityMagicItemHatch
        implements IMultiblockAbilityPart<IBloodMagicHatch>, IBloodMagicHatch {

    public MetaTileEntityBloodMagicHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBloodMagicHatch(metaTileEntityId, getTier());
    }

    @Override
    protected boolean isAcceptedStack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemBloodOrb && Binding.fromStack(stack) != null;
    }

    @Override
    protected SimpleOverlayRenderer getOverlay() {
        return POTextures.BLOOD_MAGIC_HATCH;
    }

    @Override
    public MultiblockAbility<IBloodMagicHatch> getAbility() {
        return POMultiblockAbility.BLOOD_MAGIC_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Nullable
    private SoulNetwork getSoulNetwork() {
        ItemStack orb = getFocusStack();
        if (!isAcceptedStack(orb)) return null;
        Binding binding = Binding.fromStack(orb);
        return binding == null ? null : NetworkHelper.getSoulNetwork(binding);
    }

    @Override
    public int getLifeEssence() {
        SoulNetwork network = getSoulNetwork();
        return network == null ? 0 : Math.max(0, network.getCurrentEssence());
    }

    @Override
    public int getLifeEssenceCapacity() {
        SoulNetwork network = getSoulNetwork();
        return network == null ? 0 : Math.max(0, NetworkHelper.getCurrentMaxOrb(network));
    }

    @Override
    public boolean consumeLifeEssence(int amount, boolean simulate) {
        if (amount <= 0) return true;
        SoulNetwork network = getSoulNetwork();
        if (network == null || network.getCurrentEssence() < amount) return false;
        if (!simulate) {
            network.syphon(SoulTicket.block(getWorld(), getPos(), amount));
        }
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.blood_magic_hatch.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.blood_magic_hatch.tooltip.2"));
    }
}
