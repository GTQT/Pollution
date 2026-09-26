package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import gregtech.api.GTValues;
import gregtech.api.capability.IMufflerHatch;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gregtech.common.metatileentities.electric.MetaTileEntityDustCollector;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityFluxMuffler extends MetaTileEntityMufflerHatch {

    public MetaTileEntityFluxMuffler(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFluxMuffler(metaTileEntityId, getTier());
    }

    @Override
    public boolean checkFrontFaceFree() {
        return true;
    }

    @Override
    public boolean isMufflerFull() {
        return false;
    }

    @Override
    public double getPollutionAmount() {
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.flux_muffler_hatch.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.flux_muffler_hatch.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.flux_muffler_hatch.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.flux_muffler_hatch.tooltip.5"));
        tooltip.add(I18n.format("pollution.machine.flux_muffler_hatch.tooltip.6"));

        tooltip.add(I18n.format("gregtech.muffler.recovery_tooltip", getRecoveryChance()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("pollution.machine.flux_muffler_hatch.tooltip.2"));
    }
}
