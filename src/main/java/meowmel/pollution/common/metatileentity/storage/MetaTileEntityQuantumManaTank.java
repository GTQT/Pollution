package meowmel.pollution.common.metatileentity.storage;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IQuantumController;
import gregtech.api.capability.IQuantumStorage;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.storage.MetaTileEntityQuantumStorage;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.capability.PODataCodes;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.client.textures.custom.AspectStorageRenderer;
import meowmel.pollution.common.metatileentity.multiblockpart.ManaContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.List;

/**
 * 量子魔力罐：单方块存储植物魔法的魔力，和 GT 的 {@code MetaTileEntityQuantumTank} 一样继承
 * {@link MetaTileEntityQuantumStorage}，界面、存档、输出面与自动输出全部沿用那套框架。
 * <p>
 * 输入：魔力脉冲（GT 的 {@code MetaTileEntityHolder} 被 mixin 成 {@code IManaCollector}，
 * 会把脉冲交给实现 {@link IManaHatch} 的机器），或输入槽中的魔力石板/魔力之戒等 {@code IManaItem}<br>
 * 输出：开启自动输出后，向输出面相邻的 {@code IManaReceiver}（魔力池等）推送；输入槽中处理完的
 * 魔力物品会被送到输出槽
 */
public class MetaTileEntityQuantumManaTank extends MetaTileEntityQuantumStorage<ManaContainer>
        implements ITieredMetaTileEntity, IFastRenderMetaTileEntity, IManaHatch {

    /** 传输速率相对容量的比例：每 tick 最多进出 1% 容量 */
    private static final int TRANSFER_RATE_DIVISOR = 100;
    /** 魔力填充颜色 */
    private static final int MANA_COLOR = 0x00C6FF;

    private final int tier;
    private final int maxManaCapacity;
    private final int transferRate;
    private final ManaContainer manaContainer;

    /** 上一次同步给客户端的魔力，-1 表示尚未同步 */
    private long syncedMana = -1L;

    public MetaTileEntityQuantumManaTank(ResourceLocation metaTileEntityId, int tier, int maxManaCapacity) {
        super(metaTileEntityId);
        this.tier = tier;
        this.maxManaCapacity = maxManaCapacity;
        this.transferRate = Math.max(1, maxManaCapacity / TRANSFER_RATE_DIVISOR);
        this.manaContainer = new ManaContainer(this.maxManaCapacity);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityQuantumManaTank(metaTileEntityId, tier, maxManaCapacity);
    }

    @Override
    public int getTier() {
        return tier;
    }

    /**
     * @return 该等级的魔力存储上限
     */
    public int getMaxManaCapacity() {
        return maxManaCapacity;
    }

    /**
     * @return 该等级的魔力传输速率
     */
    public int getTransferRate() {
        return transferRate;
    }

    // ------------------------------------------------------------------
    // 量子存储框架
    // ------------------------------------------------------------------

    /**
     * 魔力罐没有对应的 GT 量子存储类型：返回 {@link IQuantumStorage.Type#EXTENDER} 可以避免
     * {@code MetaTileEntityQuantumStorageController} 把它当成流体/物品仓去强转 {@link #getTypeValue()}
     */
    @Override
    public IQuantumStorage.Type getType() {
        return IQuantumStorage.Type.EXTENDER;
    }

    @Override
    public ManaContainer getTypeValue() {
        return this.manaContainer;
    }

    @Override
    public void tryFindNetwork() {
        // 不接入 GT 量子存储网络
    }

    @Override
    public void setConnected(IQuantumController controller) {
        // 不接入 GT 量子存储网络
    }

    @Override
    public void setDisconnected() {
        // 不接入 GT 量子存储网络
    }

    @Override
    protected ButtonWidget<?> createConnectionButton() {
        // 没有网络连接按钮，留一个占位保持父类布局
        ButtonWidget<?> placeholder = new ButtonWidget<>().size(0);
        placeholder.setEnabled(false);
        return placeholder;
    }

    // ------------------------------------------------------------------
    // IManaHatch：魔力脉冲输入
    // ------------------------------------------------------------------

    @Override
    public long getMaxMana() {
        return maxManaCapacity;
    }

    @Override
    public long getMana() {
        return manaContainer.getMana();
    }

    @Override
    public boolean isFull() {
        return manaContainer.isFull();
    }

    @Override
    public void receiveMana(long mana) {
        if (mana <= 0) return;
        if (manaContainer.addMana(mana) > 0) {
            this.markDirty();
        }
    }

    @Override
    public boolean consumeMana(long amount, boolean simulate) {
        if (amount <= 0) return true;
        if (manaContainer.getMana() < amount) return false;

        if (!simulate) {
            manaContainer.removeMana(amount);
            this.markDirty();
        }
        return true;
    }

    // ------------------------------------------------------------------
    // 每 tick 逻辑
    // ------------------------------------------------------------------

    @Override
    public void update() {
        super.update();
        if (this.getWorld().isRemote) return;

        this.handleManaContainers();
        if (this.autoOutput && manaContainer.getMana() > 0) {
            this.pushManaIntoNearbyHandler();
        }
        this.syncManaToClient();
    }

    /**
     * 处理输入槽里的魔力物品：有魔力就抽进罐内，没充满就充能，处理完（抽空或充满）后送到输出槽
     */
    private void handleManaContainers() {
        ItemStack input = this.importItems.extractItem(0, 1, false);
        if (input.isEmpty()) return;

        if (!(input.getItem() instanceof IManaItem manaItem) || manaItem.isNoExport(input)) {
            this.importItems.insertItem(0, input, false);
            return;
        }

        int stored = manaItem.getMana(input);
        int itemCapacity = manaItem.getMaxMana(input);
        if (stored > 0 && (!manaContainer.isFull() || this.isVoiding())) {
            int movable = (int) Math.min(Math.min(stored, transferRate),
                    Math.max(maxManaCapacity - manaContainer.getMana(), 0));
            if (movable <= 0 && this.isVoiding()) {
                // 虚空模式：满罐时吞掉物品里的魔力
                movable = (int) Math.min(stored, transferRate);
            }
            if (movable > 0) {
                manaItem.addMana(input, -movable);
                if (!manaContainer.isFull()) {
                    manaContainer.addMana(movable);
                }
                this.markDirty();
            }
        } else if (stored < itemCapacity && manaContainer.getMana() > 0) {
            int movable = (int) Math.min(Math.min(itemCapacity - stored, transferRate), manaContainer.getMana());
            if (movable > 0) {
                manaItem.addMana(input, movable);
                manaContainer.removeMana(movable);
                this.markDirty();
            }
        }

        boolean finished = manaItem.getMana(input) <= 0 || manaItem.getMana(input) >= manaItem.getMaxMana(input);
        if (finished && this.exportItems.insertItem(0, input, true).isEmpty()) {
            this.exportItems.insertItem(0, input, false);
        } else if (!this.importItems.insertItem(0, input, false).isEmpty()) {
            // 放不回输入槽就退回输出槽，避免物品消失
            this.exportItems.insertItem(0, input, false);
        }
    }

    /**
     * 把魔力推给输出面相邻的魔力接收方，按实测接收量扣除
     */
    private void pushManaIntoNearbyHandler() {
        TileEntity tileEntity = this.getWorld().getTileEntity(this.getPos().offset(this.getOutputFacing()));
        if (!(tileEntity instanceof IManaReceiver manaReceiver) || manaReceiver.isFull()) return;

        long toSend = Math.min(manaContainer.getMana(), transferRate);
        int before = manaReceiver.getCurrentMana();
        manaReceiver.recieveMana((int) Math.min(toSend, Integer.MAX_VALUE));
        long accepted = Math.max(0L, (long) manaReceiver.getCurrentMana() - before);
        if (accepted <= 0) return;

        manaContainer.removeMana(Math.min(toSend, accepted));
        this.markDirty();
    }

    /**
     * 魔力变化时同步给客户端（渲染填充用），最多每 10 tick 发包一次
     */
    private void syncManaToClient() {
        long currentMana = manaContainer.getMana();
        if (this.syncedMana == currentMana) return;
        if (this.getOffsetTimer() % 10 != 0 && currentMana != 0 && !manaContainer.isFull()) return;

        this.syncedMana = currentMana;
        this.writeCustomData(PODataCodes.MANA_TANK_AMOUNT, buf -> buf.writeLong(currentMana));
    }

    // ------------------------------------------------------------------
    // 存档 / 物品数据
    // ------------------------------------------------------------------

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        // 父类只在 ITEM/FLUID 类型下保存输出面/自动输出，这里按自己的类型再写一遍
        data.setLong("Mana", manaContainer.getMana());
        data.setInteger("OutputFacing", this.getOutputFacing().getIndex());
        data.setBoolean("AutoOutputMana", this.autoOutput);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        // ManaContainer 构造时容量已确定，这里按容量夹取
        manaContainer.addMana(data.getLong("Mana"));
        if (data.hasKey("OutputFacing")) {
            this.outputFacing = EnumFacing.VALUES[data.getInteger("OutputFacing")];
        }
        this.autoOutput = data.getBoolean("AutoOutputMana");
    }

    @Override
    public void initFromItemStackData(NBTTagCompound tag) {
        super.initFromItemStackData(tag);
        this.manaContainer.addMana(tag.getLong("Mana"));
        this.autoOutput = tag.getBoolean("AutoOutputMana");
    }

    @Override
    public void writeItemStackData(NBTTagCompound tag) {
        super.writeItemStackData(tag);
        long mana = manaContainer.getMana();
        if (mana > 0) {
            tag.setLong("Mana", mana);
        }
        if (this.autoOutput) {
            tag.setBoolean("AutoOutputMana", true);
        }
    }

    @Override
    public ItemStack getPickItem(EntityPlayer player) {
        if (!player.isCreative()) return super.getPickItem(player);

        ItemStack baseItemStack = this.getStackForm();
        NBTTagCompound tag = new NBTTagCompound();
        this.writeItemStackData(tag);
        if (!tag.isEmpty()) {
            baseItemStack.setTagCompound(tag);
        }
        return baseItemStack;
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeLong(manaContainer.getMana());
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.syncedMana = buf.readLong();
        this.manaContainer.addMana(this.syncedMana);
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == PODataCodes.MANA_TANK_AMOUNT) {
            this.syncedMana = buf.readLong();
            this.manaContainer.addMana(this.syncedMana); // 客户端只需显示，容量足够
            this.scheduleRenderUpdate();
        }
    }

    // ------------------------------------------------------------------
    // 渲染
    // ------------------------------------------------------------------

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        AspectStorageRenderer.renderMachineFrame(renderState, translation,
                ArrayUtils.add(pipeline,
                        new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering()))),
                this.tier, this.getFrontFacing());
        POTextures.QUANTUM_ASPECT_TANK_OVERLAY.renderSided(EnumFacing.UP, renderState, translation, pipeline);

        if (this.outputFacing != null) {
            POTextures.PIPE_ASPECT_OUT_OVERLAY.renderSided(this.outputFacing, renderState, translation, pipeline);
            if (this.autoOutput) {
                POTextures.ASPECT_OUTPUT_OVERLAY.renderSided(this.outputFacing, renderState, translation, pipeline);
            }
        }

        AspectStorageRenderer.renderTankFill(renderState, translation, pipeline, MANA_COLOR,
                this.maxManaCapacity, this.getMana(), this.getWorld(), this.getPos(), this.getFrontFacing());
    }

    @Override
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        AspectStorageRenderer.renderAspectAmount(x, y, z, this.getFrontFacing(), this.getMana());
    }

    @Override
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        return Pair.of(Textures.VOLTAGE_CASINGS[this.tier].getParticleSprite(), this.getPaintingColorForRendering());
    }

    // ------------------------------------------------------------------
    // 交互 / Capability
    // ------------------------------------------------------------------

    @Override
    public boolean onWrenchClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                 CuboidRayTraceResult hitResult) {
        if (playerIn.isSneaking()) {
            return super.onWrenchClick(playerIn, hand, facing, hitResult);
        }
        if (this.getOutputFacing() == facing || this.getFrontFacing() == facing) {
            return false;
        }
        if (!this.getWorld().isRemote) {
            this.setOutputFacing(facing);
        }
        return true;
    }

    @Override
    public boolean isAutoOutputItems() {
        return false;
    }

    @Override
    public boolean isAutoOutputFluids() {
        return this.autoOutput;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_ACTIVE_OUTPUT_SIDE && side == getOutputFacing()) {
            return GregtechTileCapabilities.CAPABILITY_ACTIVE_OUTPUT_SIDE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean needsSneakToRotate() {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos());
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new GTItemStackHandler(this, 1);
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new GTItemStackHandler(this, 1);
    }

    // ------------------------------------------------------------------
    // 信息 / UI
    // ------------------------------------------------------------------

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        for (int i = 1; i <= 4; i++) {
            tooltip.add(I18n.format("pollution.machine.quantum_mana_tank.tooltip." + i));
        }
        tooltip.add(I18n.format("pollution.universal.tooltip.mana_storage_capacity", this.maxManaCapacity));
        tooltip.add(I18n.format("pollution.universal.tooltip.mana_transfer_rate", this.transferRate));

        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.getLong("Mana") > 0) {
            tooltip.add(I18n.format("pollution.machine.quantum_mana_tank.mana",
                    TextFormattingUtil.formatNumbers(tag.getLong("Mana")),
                    TextFormattingUtil.formatNumbers(this.maxManaCapacity)));
        }
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.auto_output_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    /**
     * 魔力数量显示，与量子罐的流体显示同款
     */
    @Override
    protected void createWidgets(ModularPanel mainPanel, PanelSyncManager syncManager) {
        mainPanel.child(createQuantumDisplay(
                "pollution.machine.quantum_mana_tank.display_amount",
                () -> I18n.format("pollution.machine.quantum_mana_tank.display_name"),
                widget -> true,
                () -> TextFormattingUtil.formatNumbers(this.getMana()) + " / "
                        + TextFormattingUtil.formatNumbers(this.maxManaCapacity)));    }

    /**
     * 按钮行：自动输出 + 虚空，图标与量子罐的流体页一致
     */
    @Override
    public Flow createQuantumButtonRow() {
        return Flow.row()
                .coverChildren()
                .pos(7, 63)
                .child(new ToggleButton()
                        .overlay(GTGuiTextures.BUTTON_FLUID_OUTPUT)
                        .addTooltipLine(IKey.lang("pollution.machine.quantum_mana_tank.auto_output"))
                        .value(new BooleanSyncValue(this::isAutoOutputFluids, this::setAutoOutput)))
                .child(new ToggleButton()
                        .overlay(GTGuiTextures.FLUID_VOID_OVERLAY)
                        .addTooltip(true, IKey.lang("gregtech.gui.fluid_voiding.tooltip.enabled"))
                        .addTooltip(false, IKey.lang("gregtech.gui.fluid_voiding.tooltip.disabled"))
                        .value(new BooleanSyncValue(this::isVoiding, this::setVoiding)));
    }
}
