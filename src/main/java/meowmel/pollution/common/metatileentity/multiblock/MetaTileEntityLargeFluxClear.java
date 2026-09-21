package meowmel.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.GTQTDateHelper;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.ICubeRenderer;
import meowmel.pollution.api.capability.IFilterHatch;
import meowmel.pollution.api.capability.IFluxClearInfo;
import meowmel.pollution.api.capability.ipml.MultiblockFluxClearLogic;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

/**
 * 大型空气过滤机，等级由 {@link IFluxClearType} 提供，写法参考 {@code MetaTileEntityLargeTurbine}。
 * 滤芯由结构中的滤芯仓提供，控制器只显示数据。
 */
public class MetaTileEntityLargeFluxClear extends MetaTileEntityBaseWithControl implements ITieredMetaTileEntity, IFluxClearInfo {

    private static final String STRUCTURE_POOL_KEY = "pollution:large_flux_clear";

    public final IFluxClearType type;
    private final MultiblockFluxClearLogic fluxClearLogic;

    public MetaTileEntityLargeFluxClear(ResourceLocation metaTileEntityId, IFluxClearType type) {
        super(metaTileEntityId);
        this.type = type;
        this.fluxClearLogic = new MultiblockFluxClearLogic(this, type);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLargeFluxClear(metaTileEntityId, type);
    }

    /**
     * @return 机器的运行逻辑
     */
    public MultiblockFluxClearLogic getFluxClearLogic() {
        return fluxClearLogic;
    }

    /**
     * @return 结构中的滤芯仓，没有则返回 null
     */
    @Nullable
    public IFilterHatch getFilterHatch() {
        List<IFilterHatch> hatches = getAbilities(POMultiblockAbility.FILTER_HATCH);
        return hatches.isEmpty() ? null : hatches.get(0);
    }

    @Override
    protected void updateFormedValid() {
        this.fluxClearLogic.performClearing();
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        this.fluxClearLogic.writeInitialSyncData(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.fluxClearLogic.receiveInitialSyncData(buf);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        this.fluxClearLogic.receiveCustomData(dataId, buf);
    }

    @Override
    public float getCurrentFlux() {
        World world = getWorld();
        return world == null ? 0.0f : AuraHelper.getFlux(world, getPos());
    }

    @Override
    public double getVisPerTick() {
        return this.fluxClearLogic.getVisPerTick();
    }

    @Override
    public int getFilterDamage() {
        return this.fluxClearLogic.getFilterDamage();
    }

    @Override
    public int getFilterMaxDurability() {
        return this.fluxClearLogic.getFilterMaxDurability();
    }

    private static StructureDefinition<?> buildStructureDefinition(IFluxClearType type) {
        return DeclarativePatternBuilder.start()
                .aisle("XXX", "XXX", "XXX", "AAA", "AAA")
                .aisle("XXX", "XXX", "XXX", "AXA", "AAA")
                .aisle("XXX", "XSX", "XXX", "AAA", "AAA")
                .self('S', MetaTileEntityLargeFluxClear.class)
                .block('A', type.getIntakeState())
                .casing('X', type.getCasingState())
                .hatch(MultiblockAbility.INPUT_ENERGY,1,2)
                .hatch(MultiblockAbility.MAINTENANCE_HATCH, 1)
                .hatch(POMultiblockAbility.FILTER_HATCH, 1)
                .buildStructureDefinition();
    }

    @NotNull
    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return StructureDefinition.getOrBuild(STRUCTURE_POOL_KEY, type.getName(),
                () -> buildStructureDefinition(type));
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(isWorkingEnabled(), isActive())
                .addCustom(this::addFluxClearDisplayText)
                .addWorkingStatusLine();
    }

    private void addFluxClearDisplayText(KeyManager keyManager, UISyncer syncer) {
        int radius = syncer.syncInt(fluxClearLogic.getRadius());
        float flux = syncer.syncFloat(fluxClearLogic::getFlux);
        double rate = syncer.syncDouble(fluxClearLogic::getVisPerTick);
        long energyPerOperation = syncer.syncLong(fluxClearLogic::getEnergyPerOperation);
        boolean hasFilter = syncer.syncBoolean(fluxClearLogic::hasValidFilter);
        int filterDamage = syncer.syncInt(fluxClearLogic::getFilterDamage);
        int filterMaxDurability = syncer.syncInt(fluxClearLogic::getFilterMaxDurability);
        long workTime = syncer.syncLong(fluxClearLogic::getWorkTime);
        long maxWorkTime = syncer.syncLong(fluxClearLogic::getMaxWorkTime);

        keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                "pollution.machine.large_flux_clear.display.radius", radius));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.large_flux_clear.display.flux", flux));
        keyManager.add(KeyUtil.lang(TextFormatting.GREEN,
                "pollution.machine.large_flux_clear.display.rate", rate));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.large_flux_clear.display.energy", energyPerOperation));

        if (!hasFilter) {
            keyManager.add(KeyUtil.lang(TextFormatting.RED,
                    "pollution.machine.large_flux_clear.display.no_filter"));
            return;
        }
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.large_flux_clear.display.filter",
                filterMaxDurability - filterDamage, filterMaxDurability));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.large_flux_clear.display.work_time",
                GTQTDateHelper.getTimeFromTicks(workTime)));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.large_flux_clear.display.durability",
                GTQTDateHelper.getTimeFromTicks(maxWorkTime - workTime)));
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("污染清理专家！", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.flux_clear.tire", type.getTier(), fluxClearLogic.getVisPerTick()));
        tooltip.add(I18n.format("pollution.flux_clear.amount", type.getRadius()));
        tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
    }

    @Override
    public IBlockState getCasingBlock() {
        return type.getCasingState();
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return type.getFrontOverlay();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return type.getCasingRenderer();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public int getTier() {
        return type.getTier();
    }
}
