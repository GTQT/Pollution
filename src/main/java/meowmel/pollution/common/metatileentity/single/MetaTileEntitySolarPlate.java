package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

import static gregtech.api.GTValues.VA;

public class MetaTileEntitySolarPlate extends TieredMetaTileEntity {

    /** 发电量按对应电压的 1/3 计算 */
    private static final int ENERGY_DIVISOR = 3;

    private final SolarPlateType type;
    private final ICubeRenderer renderer;
    private boolean isActive;

    public MetaTileEntitySolarPlate(ResourceLocation metaTileEntityId, int tier, SolarPlateType type,
                                    ICubeRenderer renderer) {
        super(metaTileEntityId, tier);
        this.type = type;
        this.renderer = renderer;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntitySolarPlate(metaTileEntityId, getTier(), type, renderer);
    }

    /**
     * @return 太阳能板的种类
     */
    public SolarPlateType getSolarPlateType() {
        return type;
    }

    /**
     * 发电量：满足增产条件时按高一档电压计算
     *
     * @param tier    机器等级
     * @param boosted 是否满足增产条件
     * @return 每 tick 发电量
     */
    public static long getEnergyOutput(int tier, boolean boosted) {
        return VA[tier + (boosted ? 1 : 0)] / ENERGY_DIVISOR;
    }

    @Override
    public void update() {
        super.update();

        // 工作条件由种类提供，客户端同样计算，贴图/能量状态保持一致
        this.isActive = type.meetsWorkCondition(getWorld(), getPos(), getFrontFacing());
        if (!getWorld().isRemote && this.isActive) {
            energyContainer.changeEnergy(getEnergyOutput(getTier(),
                    type.isBoosted(getWorld(), getPos(), getFrontFacing())));
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public boolean isWorkingEnabled() {
        return isActive;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("来自魔法的免费能源", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format(type.getBoostTooltipKey()));
        tooltip.add(I18n.format("pollution.machine.solar_plate.tooltip.level",
                getTier(), I18n.format(type.getNameKey())));
        tooltip.add(I18n.format("pollution.machine.solar_plate.tooltip.output",
                getEnergyOutput(getTier(), false), getEnergyOutput(getTier(), true)));
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.renderer.renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                isActive(), isWorkingEnabled());

        // 四个水平面渲染对应元素的贴图
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            type.getOverlay().renderSided(facing, renderState, translation, pipeline);
        }
    }

    @Override
    protected boolean isEnergyEmitter() {
        return true;
    }
}
