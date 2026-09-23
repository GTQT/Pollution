package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.util.GTUtility;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaTileEntityAdvancedMufflerHatch extends MetaTileEntityMufflerHatch {

    public MetaTileEntityAdvancedMufflerHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAdvancedMufflerHatch(metaTileEntityId, getTier());
    }


    @Override
    public boolean checkFrontFaceFree() {
        EnumFacing frontFacing = getFrontFacing();
        BlockPos originPos = getPos();

        MultiblockWithDisplayBase controller = (MultiblockWithDisplayBase) getController();
        boolean isActive = controller != null && controller.isActive();

        for (int i = 1; i <= 3; i++) {
            BlockPos frontPos = originPos.offset(frontFacing, i);
            IBlockState blockState = getWorld().getBlockState(frontPos);

            if (blockState.getBlock().isAir(blockState, getWorld(), frontPos)) {
                continue;
            }

            // break a snow layer if it exists, and if this machine is running
            if (isActive && GTUtility.tryBreakSnow(getWorld(), frontPos, blockState, true)) {
                continue;
            }

            if (!GTUtility.isBlockSnow(blockState)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isMufflerFull() {
        return false;
    }

    @Override
    public double getPollutionAmount() {
        return super.getPollutionAmount() * 0.5;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay())
            POTextures.ADVANCED_MUFFLER_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("pollution.machine.advanced_muffler_hatch.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.advanced_muffler_hatch.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.advanced_muffler_hatch.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.advanced_muffler_hatch.tooltip.5"));
        tooltip.add(I18n.format("pollution.machine.advanced_muffler_hatch.tooltip.6"));

        if (ConfigHolder.machines.enablePollution) {
            tooltip.add(TextFormatting.GREEN + I18n.format("gregtech.tooltip.pollution_mte_available"));
            tooltip.add(I18n.format("gregtech.multiblock.pollution_hatch.tooltip.1"));
            tooltip.add(I18n.format("gregtech.multiblock.pollution_hatch.tooltip.2"));
        }

        tooltip.add(I18n.format("gregtech.muffler.recovery_tooltip", getRecoveryChance()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("pollution.machine.advanced_muffler_hatch.tooltip.2"));
    }
}
