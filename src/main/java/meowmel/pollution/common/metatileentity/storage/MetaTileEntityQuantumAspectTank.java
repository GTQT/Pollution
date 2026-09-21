package meowmel.pollution.common.metatileentity.storage;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import gregtech.api.capability.IQuantumController;
import gregtech.api.capability.IQuantumStorage;
import gregtech.api.capability.impl.FilteredItemHandler;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.storage.MetaTileEntityQuantumStorage;
import meowmel.pollution.api.capability.PODataCodes;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.client.textures.custom.AspectStorageRenderer;
import meowmel.pollution.common.lib.GTEssentiaHandler;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.items.consumables.ItemPhial;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * 量子要素罐：单方块存储神秘要素，和 GT 的 {@code MetaTileEntityQuantumTank} 一样继承
 * {@link MetaTileEntityQuantumStorage}，界面、存档、输出面与自动输出全部沿用那套框架。
 * <p>
 * 存储：一次只能存一种要素（{@link #getAspect()}），可锁定某种要素（{@link #getAspectFilter()}）<br>
 * 交互：顶部可输入/输出要素（{@link IAspectSource} / {@link IEssentiaTransport}）；输入槽放要素罐物品、
 * 要素罐子或要素水晶瓶可倒进内部，输出槽收处理完的容器；开启自动输出后向输出面相邻的要素接受方推送<br>
 * 其它：扳手设置输出面，锁定按钮冻结当前要素，虚空模式在满罐时吞掉同种要素
 */
public class MetaTileEntityQuantumAspectTank extends MetaTileEntityQuantumStorage<IAspectSource>
        implements ITieredMetaTileEntity, IFastRenderMetaTileEntity, IAspectSource, IEssentiaTransport {

    private final int tier;
    private final int maxAspectCapacity;

    /** 当前存储的要素及其数量，两者始终同步（数量为 0 时要素为 null） */
    private @Nullable Aspect aspect;
    private int amount;
    /** 锁定的要素，只有该种要素能进入 */
    private @Nullable Aspect aspectFilter;
    /** 上一次同步给客户端的值 */
    private @Nullable Aspect syncedAspect;
    private int syncedAmount;

    public MetaTileEntityQuantumAspectTank(ResourceLocation metaTileEntityId, int tier, int maxAspectCapacity) {
        super(metaTileEntityId);
        this.tier = tier;
        this.maxAspectCapacity = maxAspectCapacity;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityQuantumAspectTank(this.metaTileEntityId, this.tier, this.maxAspectCapacity);
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    /**
     * @return 该等级的要素存储上限
     */
    public int getMaxAspectCapacity() {
        return this.maxAspectCapacity;
    }

    // ------------------------------------------------------------------
    // 量子存储框架
    // ------------------------------------------------------------------

    /**
     * 要素罐没有对应的 GT 量子存储类型：返回 {@link IQuantumStorage.Type#EXTENDER} 可以避免
     * {@code MetaTileEntityQuantumStorageController} 把它当成流体/物品仓去强转 {@link #getTypeValue()}
     */
    @Override
    public IQuantumStorage.Type getType() {
        return IQuantumStorage.Type.EXTENDER;
    }

    @Override
    public IAspectSource getTypeValue() {
        return this;
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

    @Override
    protected boolean isLocked() {
        return this.aspectFilter != null;
    }

    /**
     * 锁定按钮：锁定当前要素，再次点击解除（与量子罐锁定当前流体一致）
     */
    @Override
    protected void setLocked(boolean locked) {
        if (locked == isLocked()) return;
        if (!this.getWorld().isRemote) {
            this.setAspectFilter(locked ? this.aspect : null);
            this.markDirty();
        }
    }

    // ------------------------------------------------------------------
    // 存储状态
    // ------------------------------------------------------------------

    /**
     * @return 当前存储的要素，空罐返回 null
     */
    public @Nullable Aspect getAspect() {
        return this.aspect;
    }

    /**
     * @return 当前存储的要素数量
     */
    public int getAspectAmount() {
        return this.amount;
    }

    /**
     * @return 锁定的要素，未锁定返回 null
     */
    public @Nullable Aspect getAspectFilter() {
        return this.aspectFilter;
    }

    /**
     * 锁定/解锁要素，并同步给客户端
     */
    public void setAspectFilter(@Nullable Aspect aspect) {
        this.aspectFilter = aspect;
        this.writeCustomData(PODataCodes.QUANTUM_ASPECT_TANK_FILTER, (buf) ->
                buf.writeString(aspect == null ? "" : aspect.getTag()));
    }

    /**
     * 清空内部存储
     */
    private void clearAspect() {
        this.aspect = null;
        this.amount = 0;
    }

    // ------------------------------------------------------------------
    // 每 tick 逻辑
    // ------------------------------------------------------------------

    @Override
    public void update() {
        super.update();
        if (!this.getWorld().isRemote) {
            this.fillTankFromContainers();
            this.fillContainersFromTank();
            if (this.autoOutput) {
                this.pushAspectIntoNearbyHandlers();
            }
        }
        this.syncAspectToClient();
    }

    /**
     * 把要素种类与数量同步给客户端，仅在变化时发包
     */
    private void syncAspectToClient() {
        if (Objects.equals(this.syncedAspect, this.aspect) && this.syncedAmount == this.amount) return;

        this.syncedAspect = this.aspect;
        this.syncedAmount = this.amount;
        this.writeCustomData(PODataCodes.QUANTUM_ASPECT_TANK_CONTENT, (buf) -> {
            buf.writeString(this.aspect == null ? "" : this.aspect.getTag());
            buf.writeInt(this.amount);
        });
    }

    /**
     * 把装满要素的容器（要素罐物品、要素罐子、要素水晶瓶）里的要素倒进内部存储
     */
    public void fillTankFromContainers() {
        for (int i = 0; i < this.importItems.getSlots(); ++i) {
            ItemStack inputContainerStack = this.importItems.extractItem(i, 1, true);
            if (!inputContainerStack.hasTagCompound()) continue;

            NBTTagList aspectList = inputContainerStack.getTagCompound().getTagList("Aspects", 10);
            if (aspectList.isEmpty()) continue;

            NBTTagCompound aspectTag = aspectList.getCompoundTagAt(0);
            Aspect aspect = Aspect.getAspect(aspectTag.getString("key"));
            int amount = aspectTag.getInteger("amount");
            if (aspect == null || amount <= 0) continue;

            ItemStack emptyContainer = getEmptyContainerItem(inputContainerStack);
            if (emptyContainer.isEmpty()) continue;
            if (!GTTransferUtils.insertItem(this.exportItems, emptyContainer, true).isEmpty()) continue;
            if (this.addToContainer(aspect, amount) > 0) continue;

            this.importItems.extractItem(i, 1, false);
            GTTransferUtils.insertItem(this.exportItems, emptyContainer, false);
        }
    }

    /**
     * 用内部存储把空的容器（要素罐物品、要素罐子、要素水晶瓶）装满
     */
    public void fillContainersFromTank() {
        if (this.aspect == null || this.amount <= 0) return;

        for (int i = 0; i < this.importItems.getSlots(); ++i) {
            ItemStack inputContainerStack = this.importItems.extractItem(i, 1, true);
            ItemStack filledContainer = fillContainer(inputContainerStack, true);
            if (filledContainer.isEmpty()) continue;
            if (!GTTransferUtils.insertItem(this.exportItems, filledContainer, true).isEmpty()) continue;
            if (fillContainer(inputContainerStack, false).isEmpty()) continue;

            this.importItems.extractItem(i, 1, false);
            GTTransferUtils.insertItem(this.exportItems, filledContainer, false);
        }
    }

    /**
     * 把内部要素推给输出面相邻的要素接受方
     */
    public void pushAspectIntoNearbyHandlers() {
        if (this.aspect == null || this.amount <= 0) return;

        EnumFacing facing = this.getOutputFacing();
        if (GTEssentiaHandler.addEssentiaToTile(this, this.aspect, facing, false, 5)
                || GTEssentiaHandler.addEssentiaToMTE(this, this.aspect, facing, false, 5)) {
            this.takeFromContainer(this.aspect, 1);
        }
    }

    // ------------------------------------------------------------------
    // 容器物品交互
    // ------------------------------------------------------------------

    /**
     * @return 该物品对应的量子要素罐，不是要素罐物品则返回 null
     */
    @Nullable
    public static MetaTileEntityQuantumAspectTank getAspectTankItem(ItemStack stack) {
        if (stack.isEmpty()) return null;

        MetaTileEntity metaTileEntity = GTUtility.getMetaTileEntity(stack);
        return metaTileEntity instanceof MetaTileEntityQuantumAspectTank tank ? tank : null;
    }

    /**
     * @return 倒空后的容器物品，不认识的容器返回空
     */
    private static ItemStack getEmptyContainerItem(ItemStack fullContainer) {
        Item item = fullContainer.getItem();
        if (item instanceof BlockJarItem) return new ItemStack(BlocksTC.jarNormal, 1);
        if (item instanceof ItemPhial) return new ItemStack(ItemsTC.phial, 1);

        MetaTileEntityQuantumAspectTank tank = getAspectTankItem(fullContainer);
        return tank == null ? ItemStack.EMPTY : tank.getStackForm();
    }

    /**
     * 用罐内要素装填容器物品
     *
     * @param simulate 为 true 时只试算，不扣除罐内要素
     * @return 装填后的容器，无法装填则返回空
     */
    public ItemStack fillContainer(ItemStack itemStack, boolean simulate) {
        Aspect aspect = this.aspect;
        if (aspect == null || this.amount <= 0 || itemStack.hasTagCompound()) {
            return ItemStack.EMPTY;
        }

        Item item = itemStack.getItem();
        int fillAmount;
        if (item instanceof BlockJarItem) {
            fillAmount = Math.min(250, this.amount);
        } else if (item instanceof ItemPhial) {
            if (this.amount < 10) return ItemStack.EMPTY;
            fillAmount = 10;
        } else {
            MetaTileEntityQuantumAspectTank containerTank = getAspectTankItem(itemStack);
            if (containerTank == null) return ItemStack.EMPTY;
            fillAmount = Math.min(containerTank.getMaxAspectCapacity(), this.amount);
        }

        if (!simulate) {
            this.amount -= fillAmount;
            if (this.amount <= 0) {
                clearAspect();
            }
            this.markDirty();
        }

        if (item instanceof ItemPhial) {
            return ItemPhial.makePhial(aspect, fillAmount);
        }

        ItemStack result = itemStack.copy();
        writeAspectsToItem(result, aspect, fillAmount);
        return result;
    }

    private static void writeAspectsToItem(ItemStack itemStack, Aspect aspect, int amount) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        (new AspectList()).add(aspect, amount).writeToNBT(itemStack.getTagCompound());
    }

    // ------------------------------------------------------------------
    // 存档 / 物品数据
    // ------------------------------------------------------------------

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (this.aspect != null) {
            data.setString("Aspect", this.aspect.getTag());
        }
        if (this.aspectFilter != null) {
            data.setString("AspectFilter", this.aspectFilter.getTag());
        }
        // 必须用 int：高等级容量远超 short 上限
        data.setInteger("Amount", this.amount);
        // 父类只在 ITEM/FLUID 类型下保存这些，这里按自己的类型再写一遍
        data.setInteger(OUTPUT_FACING, this.getOutputFacing().getIndex());
        data.setBoolean(AUTO_OUTPUT_ITEMS, this.autoOutput);
        data.setBoolean(IS_VOIDING, this.voiding);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        this.aspect = Aspect.getAspect(data.getString("Aspect"));
        this.aspectFilter = Aspect.getAspect(data.getString("AspectFilter"));
        // 旧存档用 short 存的，getInteger 也能读
        this.amount = Math.max(0, Math.min(data.getInteger("Amount"), this.maxAspectCapacity));
        if (this.aspect == null) {
            clearAspect();
        }
        if (data.hasKey("ContainerInventory")) {
            legacyTankItemHandlerNBTReading(this, data.getCompoundTag("ContainerInventory"), 0, 1);
        }
        if (data.hasKey(OUTPUT_FACING)) {
            this.outputFacing = EnumFacing.VALUES[data.getInteger(OUTPUT_FACING)];
        }
        this.autoOutput = data.getBoolean(AUTO_OUTPUT_ITEMS) || data.getBoolean("AutoOutputAspect");
        this.voiding = data.getBoolean(IS_VOIDING) || data.getBoolean("IsPartiallyVoiding");
    }

    @Override
    public void initFromItemStackData(NBTTagCompound tag) {
        super.initFromItemStackData(tag);

        if (tag.getBoolean(IS_VOIDING) || tag.getBoolean("IsPartiallyVoiding")) {
            this.voiding = true;
        }
        if (tag.hasKey("AspectFilter")) {
            this.aspectFilter = Aspect.getAspect(tag.getString("AspectFilter"));
        }

        NBTTagList aspectList = tag.getTagList("Aspects", 10);
        if (!aspectList.isEmpty()) {
            NBTTagCompound aspectTag = aspectList.getCompoundTagAt(0);
            Aspect storedAspect = Aspect.getAspect(aspectTag.getString("key"));
            if (storedAspect != null) {
                this.aspect = storedAspect;
                this.amount = Math.max(0, Math.min(aspectTag.getInteger("amount"), this.maxAspectCapacity));
            }
        }
    }

    @Override
    public void writeItemStackData(NBTTagCompound tag) {
        super.writeItemStackData(tag);
        if (this.voiding) {
            tag.setBoolean(IS_VOIDING, true);
        }
        if (this.aspectFilter != null) {
            tag.setString("AspectFilter", this.aspectFilter.getTag());
        }
        if (this.aspect != null) {
            NBTTagCompound aspectTag = new NBTTagCompound();
            aspectTag.setString("key", this.aspect.getTag());
            aspectTag.setInteger("amount", this.amount);

            NBTTagList aspectList = new NBTTagList();
            aspectList.appendTag(aspectTag);
            tag.setTag("Aspects", aspectList);
        }
    }

    /**
     * 兼容旧版量子流体罐的输入/输出槽存档
     */
    public static void legacyTankItemHandlerNBTReading(MetaTileEntity mte, NBTTagCompound nbt, int inputSlot,
                                                       int outputSlot) {
        if (mte == null || nbt == null) return;

        NBTTagList items = nbt.getTagList("Items", 10);
        if (mte.getExportItems().getSlots() < 1 || mte.getImportItems().getSlots() < 1
                || inputSlot < 0 || outputSlot < 0 || inputSlot == outputSlot) {
            return;
        }

        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound itemTags = items.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            if (slot == inputSlot) {
                mte.getImportItems().setStackInSlot(0, new ItemStack(itemTags));
            } else if (slot == outputSlot) {
                mte.getExportItems().setStackInSlot(0, new ItemStack(itemTags));
            }
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

        AspectStorageRenderer.renderTankAspect(renderState, translation, pipeline, this.aspect,
                this.maxAspectCapacity, this.amount, this.getWorld(), this.getPos(), this.getFrontFacing());
    }

    @Override
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.aspect != null || this.aspectFilter != null) {
            AspectStorageRenderer.renderAspect(x, y, z, this.getFrontFacing(), this.aspect, this.aspectFilter);
            AspectStorageRenderer.renderAspectAmount(x, y, z, this.getFrontFacing(), this.amount);
        }
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
    public boolean needsSneakToRotate() {
        return true;
    }

    @Override
    public ItemStack getPickItem(EntityPlayer player) {
        if (!player.isCreative()) {
            return super.getPickItem(player);
        }

        ItemStack baseItemStack = this.getStackForm();
        NBTTagCompound tag = new NBTTagCompound();
        this.writeItemStackData(tag);
        if (!tag.isEmpty()) {
            baseItemStack.setTagCompound(tag);
        }
        return baseItemStack;
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
        return new FilteredItemHandler(this, 1);
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new GTItemStackHandler(this, 1);
    }

    // ------------------------------------------------------------------
    // 同步
    // ------------------------------------------------------------------

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeString(this.aspect == null ? "" : this.aspect.getTag());
        buf.writeString(this.aspectFilter == null ? "" : this.aspectFilter.getTag());
        buf.writeInt(this.amount);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);

        String aspectTag = buf.readString(100);
        String aspectFilterTag = buf.readString(100);
        this.aspect = aspectTag.isEmpty() ? null : Aspect.getAspect(aspectTag);
        this.aspectFilter = aspectFilterTag.isEmpty() ? null : Aspect.getAspect(aspectFilterTag);
        this.amount = this.aspect == null ? 0 : Math.min(buf.readInt(), this.maxAspectCapacity);

        this.syncedAspect = this.aspect;
        this.syncedAmount = this.amount;
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == PODataCodes.QUANTUM_ASPECT_TANK_CONTENT) {
            String aspectTag = buf.readString(100);
            int syncedAmount = buf.readInt();
            this.aspect = aspectTag.isEmpty() ? null : Aspect.getAspect(aspectTag);
            this.amount = this.aspect == null ? 0 : Math.min(syncedAmount, this.maxAspectCapacity);
            this.scheduleRenderUpdate();
        } else if (dataId == PODataCodes.QUANTUM_ASPECT_TANK_FILTER) {
            String aspectTag = buf.readString(100);
            this.aspectFilter = aspectTag.isEmpty() ? null : Aspect.getAspect(aspectTag);
            this.scheduleRenderUpdate();
        }
    }

    // ------------------------------------------------------------------
    // 要素源 / 要素管道接口
    // ------------------------------------------------------------------

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public AspectList getAspects() {
        AspectList aspects = new AspectList();
        if (this.aspect != null && this.amount > 0) {
            aspects.add(this.aspect, this.amount);
        }
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspectList) {
        if (aspectList == null || aspectList.size() == 0) {
            clearAspect();
            return;
        }

        Aspect sortedAspect = aspectList.getAspectsSortedByAmount()[0];
        this.aspect = sortedAspect;
        this.amount = Math.max(0, Math.min(aspectList.getAmount(sortedAspect), this.maxAspectCapacity));
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return this.aspectFilter == null || aspect == this.aspectFilter;
    }

    @Override
    public int addToContainer(Aspect aspect, int i) {
        if (aspect == null || i <= 0) return i;
        if (this.aspectFilter != null && aspect != this.aspectFilter) {
            return i;
        }

        if (this.amount >= this.maxAspectCapacity && this.aspect == aspect) {
            // 满罐：虚空模式直接吞掉，否则原样退回
            return this.isVoiding() ? 0 : i;
        }
        if (this.amount > 0 && this.aspect != aspect) {
            // 一次只能存一种要素
            return i;
        }

        this.aspect = aspect;
        int added = Math.min(i, this.maxAspectCapacity - this.amount);
        this.amount += added;
        this.markDirty();
        return i - added;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int i) {
        if (i <= 0 || this.amount < i || aspect != this.aspect) return false;

        this.amount -= i;
        if (this.amount <= 0) {
            clearAspect();
        }
        this.markDirty();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        return this.amount >= i && aspect == this.aspect;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        if (this.amount <= 0 || this.aspect == null) return false;

        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == this.aspect) return true;
        }
        return false;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == this.aspect ? this.amount : 0;
    }

    // ------------------------------------------------------------------
    // 要素管道（Essentia Transport）
    // ------------------------------------------------------------------

    @Override
    public boolean isConnectable(EnumFacing facing) {
        return facing == EnumFacing.UP;
    }

    @Override
    public boolean canInputFrom(EnumFacing facing) {
        return facing == EnumFacing.UP;
    }

    @Override
    public boolean canOutputTo(EnumFacing facing) {
        return facing == EnumFacing.UP;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(EnumFacing facing) {
        return this.aspectFilter != null ? this.aspectFilter : this.aspect;
    }

    @Override
    public int getSuctionAmount(EnumFacing facing) {
        if (this.amount >= 250) return 0;
        return this.aspectFilter != null ? 64 : 32;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing facing) {
        return this.canOutputTo(facing) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing facing) {
        return this.canInputFrom(facing) ? amount - this.addToContainer(aspect, amount) : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing facing) {
        return this.aspect;
    }

    @Override
    public int getEssentiaAmount(EnumFacing facing) {
        return this.amount;
    }

    @Override
    public int getMinimumSuction() {
        return this.aspectFilter != null ? 64 : 32;
    }

    // ------------------------------------------------------------------
    // 信息 / UI
    // ------------------------------------------------------------------

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.quantum_aspect_tank.tooltip"));
        tooltip.add(I18n.format("pollution.universal.tooltip.aspect_storage_capacity", this.maxAspectCapacity));

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;

        NBTTagList aspectList = tag.getTagList("Aspects", 10);
        if (!aspectList.isEmpty()) {
            NBTTagCompound aspectTag = aspectList.getCompoundTagAt(0);
            Aspect aspect = Aspect.getAspect(aspectTag.getString("key"));
            if (aspect != null) {
                tooltip.add(I18n.format("pollution.universal.tooltip.aspect_storage",
                        aspect.getLocalizedDescription(), aspectTag.getInteger("amount")));
            }
        }
        if (tag.hasKey("AspectFilter")) {
            Aspect aspectFilter = Aspect.getAspect(tag.getString("AspectFilter"));
            if (aspectFilter != null) {
                tooltip.add(I18n.format("pollution.universal.tooltip.aspect_locked",
                        aspectFilter.getLocalizedDescription()));
            }
        }
        if (tag.getBoolean(IS_VOIDING) || tag.getBoolean("IsPartiallyVoiding")) {
            tooltip.add(I18n.format("gregtech.machine.quantum_tank.tooltip.voiding_enabled"));
        }
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.auto_output_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    /**
     * 要素显示 + 要素过滤弹窗按钮，位置与量子罐的流体页一致
     */
    @Override
    protected void createWidgets(ModularPanel mainPanel, PanelSyncManager syncManager) {
        mainPanel.child(createQuantumDisplay(
                "pollution.machine.quantum_aspect_tank.display_amount",
                () -> this.aspect == null ? "" : this.aspect.getLocalizedDescription(),
                widget -> this.aspect != null || this.aspectFilter != null,
                () -> TextFormattingUtil.formatNumbers(this.amount) + " / "
                        + TextFormattingUtil.formatNumbers(this.maxAspectCapacity)));

        // 过滤值挂在主面板上（双端注册），弹窗里的按钮只负责改它的值
        StringSyncValue filterSync = new StringSyncValue(
                () -> this.aspectFilter == null ? "" : this.aspectFilter.getTag(),
                tag -> {
                    if (!this.getWorld().isRemote) {
                        this.setAspectFilter(tag.isEmpty() ? null : Aspect.getAspect(tag));
                    }
                });
        syncManager.syncValue("aspect_filter", filterSync);

        // 纯客户端弹窗：内容里没有任何同步值，因此不需要服务端往返，点了必定打开
        IPanelHandler filterPopup = IPanelHandler.simple(mainPanel,
                (parentPanel, player) -> createAspectFilterPopup(filterSync), true);
        mainPanel.child(new ButtonWidget<>()
                .pos(61, 63)
                .size(18)
                .background(GuiTextures.MC_BUTTON, GTGuiTextures.FILTER_SETTINGS_OVERLAY.asIcon().size(16))
                .hoverBackground(GuiTextures.MC_BUTTON_HOVERED,
                        GTGuiTextures.FILTER_SETTINGS_OVERLAY.asIcon().size(16))
                .addTooltipLine(IKey.lang("pollution.machine.quantum_aspect_tank.filter"))
                .onMousePressed(mouseButton -> {
                    filterPopup.togglePanel();
                    return true;
                }));
    }

    /**
     * 要素过滤弹窗：8 列要素图标，左键锁定、右键解除
     */
    private ModularPanel createAspectFilterPopup(StringSyncValue filterSync) {
        Grid grid = new Grid()
                .minElementMargin(0, 0)
                .minColWidth(18)
                .minRowHeight(18);

        int column = 0;
        for (Aspect aspect : Aspect.aspects.values()) {
            grid.child(createAspectFilterCell(aspect, filterSync));
            if (++column % 8 == 0) {
                grid.nextRow();
            }
        }

        int rows = Math.max(1, MathHelper.ceil(Aspect.aspects.size() / 8.0f));
        return GTGuis.createPopupPanel("aspect_filter_popup", 176, 26 + rows * 18, false)
                .child(IKey.lang("pollution.machine.quantum_aspect_tank.filter").asWidget().pos(6, 6))
                .child(grid.pos(4, 20).width(168));
    }

    private ButtonWidget<?> createAspectFilterCell(Aspect aspect, StringSyncValue filterSync) {
        ButtonWidget<?> cell = new ButtonWidget<>()
                .size(18)
                .background(GTGuiTextures.SLOT)
                .addTooltipLine(IKey.str(aspect.getLocalizedDescription()))
                .onMousePressed(mouseButton -> {
                    // 左键锁定该要素，右键解除锁定
                    filterSync.setStringValue(mouseButton == 1 ? "" : aspect.getTag(), true, true);
                    return true;
                });

        // 少量第三方要素没有图标，退化成文字
        if (aspect.getImage() != null) {
            cell.overlay(new UITexture(aspect.getImage(), 0, 0, 1, 1, null));
        } else {
            cell.overlay(IKey.str(aspect.getTag()).asIcon().size(16));
        }
        return cell;
    }

    /**
     * 按钮行：自动输出 + 锁定 + 虚空，图标与量子罐的流体页一致
     */
    @Override
    public Flow createQuantumButtonRow() {
        return Flow.row()
                .coverChildren()
                .pos(7, 63)
                .child(new ToggleButton()
                        .overlay(GTGuiTextures.BUTTON_FLUID_OUTPUT)
                        .addTooltip(true, IKey.lang("gregtech.gui.fluid_auto_output.tooltip.enabled"))
                        .addTooltip(false, IKey.lang("gregtech.gui.fluid_auto_output.tooltip.disabled"))
                        .value(new BooleanSyncValue(this::isAutoOutputFluids, this::setAutoOutput)))
                .child(new ToggleButton()
                        .overlay(GTGuiTextures.FLUID_LOCK_OVERLAY)
                        .addTooltip(true, IKey.lang("gregtech.gui.fluid_lock.tooltip.enabled"))
                        .addTooltip(false, IKey.lang("gregtech.gui.fluid_lock.tooltip.disabled"))
                        .value(new BooleanSyncValue(this::isLocked, this::setLocked)))
                .child(new ToggleButton()
                        .overlay(GTGuiTextures.FLUID_VOID_OVERLAY)
                        .addTooltip(true, IKey.lang("gregtech.gui.fluid_voiding.tooltip.enabled"))
                        .addTooltip(false, IKey.lang("gregtech.gui.fluid_voiding.tooltip.disabled"))
                        .value(new BooleanSyncValue(this::isVoiding, this::setVoiding)));
    }
}
