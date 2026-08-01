package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.utils.PipelineUtil;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.List;

import static gregtech.api.GTValues.V;

public class MetaTileEntityManaHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IEnergyContainer>, IManaHatch {

    protected final boolean isExportHatch;
    protected final int amperage;
    protected final IEnergyContainer energyContainer;

    public MetaTileEntityManaHatch(ResourceLocation metaTileEntityId, int tier, int amperage, boolean isExport) {
        super(metaTileEntityId, tier);
        this.isExportHatch = isExport;
        this.amperage = amperage;

        if (isExport) {
            this.energyContainer = EnergyContainerHandler.emitterContainer(this, GTValues.V[tier] * 64L * amperage,
                    GTValues.V[tier], amperage);
            ((EnergyContainerHandler) this.energyContainer).setSideOutputCondition(s -> s == getFrontFacing());
        } else {
            this.energyContainer = EnergyContainerHandler.receiverContainer(this, GTValues.V[tier] * 16L * amperage,
                    GTValues.V[tier], amperage);
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaHatch(this.metaTileEntityId, this.getTier(), amperage, isExportHatch);
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(energyContainer);
    }

    @Override
    public void update() {
        super.update();
        if (!isExportHatch) return;
        if (!getWorld().isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tileEntity = getWorld().getTileEntity(getPos().offset(facing));
                if (tileEntity instanceof IManaReceiver manaReceiver) {
                    if (!manaReceiver.isFull()) {
                        long trans = Math.min(energyContainer.getEnergyStored(), V[getTier()] * amperage);
                        manaReceiver.recieveMana((int) trans);
                        energyContainer.removeEnergy(trans);
                        return;
                    }
                }

                MetaTileEntity metaTileEntity = GTUtility.getMetaTileEntity(getWorld(), this.getPos().offset(facing));
                if (metaTileEntity instanceof MetaTileEntityManaHatch manaHatch) {
                    if (!manaHatch.isExportHatch) {
                        if (!manaHatch.isFull()) {
                            long trans = Math.min(energyContainer.getEnergyStored(), V[getTier()] * amperage);
                            manaHatch.receiveMana((int) trans);
                            energyContainer.removeEnergy(trans);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public MultiblockAbility<IEnergyContainer> getAbility() {
        if (isExportHatch) return POMultiblockAbility.MANA_OUTPUT_HATCH;
        return POMultiblockAbility.MANA_INPUT_HATCH;
    }

    @Override
    public long getMaxMana() {
        return energyContainer.getEnergyCapacity();
    }

    @Override
    public long getMana() {
        return energyContainer.getEnergyStored();
    }

    public boolean isFull() {
        return getMana() >= getMaxMana();
    }

    public void receiveMana(long amount) {
        if (!isFull()) energyContainer.addEnergy(amount);
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return !isExportHatch && !isFull();
    }

    @Override
    public boolean consumeMana(long amount, boolean simulate) {
        if(simulate) return energyContainer.getEnergyStored() >= amount;
        return energyContainer.removeEnergy(amount) > 0;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            getOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @NotNull
    protected SimpleOverlayRenderer getOverlay() {
        if (isExportHatch) {
            if (amperage <= 1) {
                return POTextures.MANA_HATCH_OUTPUT_1A;
            } else if (amperage <= 4) {
                return POTextures.MANA_HATCH_OUTPUT_4A;
            } else if (amperage <= 16) {
                return POTextures.MANA_HATCH_OUTPUT_16A;
            } else {
                return POTextures.MANA_HATCH_OUTPUT_64A;
            }
        } else {
            if (amperage <= 1) {
                return POTextures.MANA_HATCH_INPUT_1A;
            } else if (amperage <= 4) {
                return POTextures.MANA_HATCH_INPUT_4A;
            } else if (amperage <= 16) {
                return POTextures.MANA_HATCH_INPUT_16A;
            } else {
                return POTextures.MANA_HATCH_INPUT_64A;
            }
        }
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, boolean advanced) {
        String tierName = GTValues.VNF[getTier()];
        tooltip.add(I18n.format("pollution.machine.mana_energy_hatch.tooltip"));
        addDescriptorTooltip(stack, world, tooltip, advanced);

        if (isExportHatch) {
            tooltip.add(
                    I18n.format("gregtech.universal.tooltip.voltage_out", energyContainer.getOutputVoltage(), tierName));
            tooltip.add(
                    I18n.format("gregtech.universal.tooltip.amperage_out_till", energyContainer.getOutputAmperage()));
            tooltip.add(
                    I18n.format("gregtech.universal.tooltip.throughput", energyContainer.getOutputVoltage() * energyContainer.getOutputAmperage()));
        } else {
            tooltip.add(
                    I18n.format("gregtech.universal.tooltip.voltage_in", energyContainer.getInputVoltage(), tierName));
            tooltip.add(
                    I18n.format("gregtech.universal.tooltip.amperage_in_till", energyContainer.getInputAmperage()));
            tooltip.add(
                    I18n.format("gregtech.universal.tooltip.throughput", energyContainer.getInputVoltage() * energyContainer.getInputAmperage()));
        }
        tooltip.add(
                I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
    }

    protected void addDescriptorTooltip(ItemStack stack, @Nullable World world, List<String> tooltip,
                                        boolean advanced) {
        if (isExportHatch) {
            if (amperage > 2) {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.output_hi_amp.tooltip"));
            } else {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.output.tooltip"));
            }
        } else {
            if (amperage > 2) {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.input_hi_amp.tooltip"));
            } else {
                tooltip.add(I18n.format("gregtech.machine.energy_hatch.input.tooltip"));
            }
        }
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }
}
