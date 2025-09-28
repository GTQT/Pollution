package keqing.pollution.common.metatileentity.multiblockpart;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import gregtech.api.capability.GregtechTileCapabilities;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityFluidHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;

import net.minecraftforge.fluids.FluidTank;

import java.util.List;
import java.util.Objects;

public class MetaTileEntityManaHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IManaHatch>, IManaHatch {
    private final ItemStackHandler containerInventory;

    private int basic_magicFluidTank = 64000; //
    private FluidTank magicFluidTank = new FluidTank(basic_magicFluidTank); //
    private int basic_fluidToManaRate = 40; // 每tick转换10单位液体为10 mana
    private boolean hasFluidUpgrade = false;

    int mana = 0;
    int MAX_MANA;
    int tier;
    double timeReduce;//耗时减免
    double energyReduce;//耗能减免
    int OverclockingEnhance;//超频加强
    int ParallelEnhance;//并行加强



    @Override
    public void onRemoval() {
        super.onRemoval();
        for (int i = 0; i < containerInventory.getSlots(); i++) {
            var pos = getPos();
            if(!containerInventory.getStackInSlot(i).isEmpty())
            {
                getWorld().spawnEntity(new EntityItem(getWorld(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,containerInventory.getStackInSlot(i)));
                containerInventory.extractItem(i,1,false);
            }

        }
    }

    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("等级 %s 容积上限 %s", tier, (int) (Math.pow(2, tier) * 1024)));
        tooltip.add(I18n.format("请配合植物魔法相关系统使用"));
        tooltip.add(I18n.format("UI内的八个插槽在插入魔导核心升级后可对魔力多方块就行加强"));
    }
    public MetaTileEntityManaHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier = tier;
        this.MAX_MANA = (int) (Math.pow(2, tier) * 1024);
        this.containerInventory = new GTItemStackHandler(this, 9);
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        MultiblockControllerBase controller = this.getController();
        if (controller != null) {
            return this.hatchTexture = controller.getBaseTexture(this);
        } else if (this.hatchTexture != null) {
            return this.hatchTexture != Textures.getInactiveTexture(this.hatchTexture) ? (this.hatchTexture = Textures.getInactiveTexture(this.hatchTexture)) : this.hatchTexture;
        } else {
            return POTextures.MAGIC_VOLTAGE_CASINGS[this.tier];
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaHatch(metaTileEntityId, getTier());
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 180, 240);
        builder.dynamicLabel(28, 12, () -> "魔力仓", 0xFFFFFF);
        builder.widget(new SlotWidget(containerInventory, 0, 8, 8, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("输入槽位"));

        builder.image(4, 28, 172, 128, GuiTextures.DISPLAY);

        builder.widget(new SlotWidget(containerInventory, 1, 54, 105, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 2, 72, 105, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 3, 90, 105, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 4, 108, 105, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 5, 54, 123, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 6, 72, 123, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 7, 90, 123, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));
        builder.widget(new SlotWidget(containerInventory, 8, 108, 123, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("升级槽位"));


        builder.widget((new AdvancedTextWidget(8, 32, this::addDisplayText, 16777215)).setMaxWidthLimit(180));
        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, 160);
        return builder.build(this.getHolder(), entityPlayer);
    }

    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentTranslation("等级: %s", this.getTier()));
        textList.add(new TextComponentTranslation("魔力源: " + mana + "/" + MAX_MANA));
        textList.add(new TextComponentTranslation("超频加强: " + OverclockingEnhance));
        textList.add(new TextComponentTranslation("并行加强: " + ParallelEnhance));
        textList.add(new TextComponentTranslation("耗时减免: " + timeReduce));
        textList.add(new TextComponentTranslation("耗能减免: " + energyReduce));

        if (hasFluidUpgrade) {
            textList.add(new TextComponentTranslation("GT魔力: " + magicFluidTank.getFluidAmount() + '/'
                                                        + basic_magicFluidTank));
        }
    }
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(createImportFluidHandler());
        }
        return super.getCapability(capability, facing);
    }
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }
    protected FluidTankList createImportFluidHandler() {
        if( hasFluidUpgrade ) return new FluidTankList(true, this.magicFluidTank);
        else return new FluidTankList(true);
    }

    @Override
    public void update() {
        super.update();
        int energy_reduce=0;
        int time_increase=0;
        int overclocking_enhance=0;
        int parallel_enhance=0;
        boolean has_TransformEnhance=false;
        for (int i = 1; i < 9; i++) {
            if (!containerInventory.getStackInSlot(i).isEmpty()) {

                ItemStack item = containerInventory.getStackInSlot(i);
                if(item.getItem() == PollutionMetaItems.POLLUTION_META_ITEM)
                {
                    switch (item.getMetadata()) {
                        case 220:{energy_reduce+=1;break;}
                        case 221:{time_increase+=1;break;}
                        case 222:{overclocking_enhance+=1;break;}
                        case 223:{parallel_enhance+=1;break;}
                        case 224:{has_TransformEnhance=true;break;}
                    }
                }
            }
        }
        hasFluidUpgrade=has_TransformEnhance;//
        if (hasFluidUpgrade
                && !isFull()
                && magicFluidTank.getFluidAmount() >= basic_fluidToManaRate*this.getTier()
            )
        {
            magicFluidTank.drain(basic_fluidToManaRate*this.getTier(), true);
            receiveMana(basic_fluidToManaRate*this.getTier()*144);
        }
        timeReduce=1-time_increase*0.1;
        energyReduce=1-energy_reduce*0.1;
        OverclockingEnhance=overclocking_enhance;
        ParallelEnhance=parallel_enhance;
    }
    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (this.shouldRenderOverlay()) {
            OrientedOverlayRenderer overlayRenderer;
            if (this.getTier() <= 3) {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK1_OVERLAY;
            } else if (this.getTier() <= 7) {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK2_OVERLAY;
            } else if (this.getTier() <= 10) {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK3_OVERLAY;
            } else {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK4_OVERLAY;
            }

            if (this.getController() != null && this.getController() instanceof RecipeMapMultiblockController) {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), this.getController().isActive(), Objects.requireNonNull(this.getController().getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null)).isWorkingEnabled());
            } else {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), false, false);
            }
        }

    }

    public boolean canPartShare() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("ContainerInventory", this.containerInventory.serializeNBT());
        data.setInteger("mana", this.mana);
        data.setDouble("timeReduce", this.timeReduce);
        data.setDouble("energyReduce", this.energyReduce);
        data.setInteger("ParallelEnhance", this.ParallelEnhance);
        data.setInteger("OverclockingEnhance", this.OverclockingEnhance);
        data.setTag("MagicFluidTank", magicFluidTank.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.containerInventory.deserializeNBT(data.getCompoundTag("ContainerInventory"));
        this.mana = data.getInteger("mana");
        this.timeReduce=data.getDouble("timeReduce");
        this.energyReduce=data.getDouble("energyReduce");
        this.ParallelEnhance=data.getInteger("ParallelEnhance");
        this.OverclockingEnhance=data.getInteger("OverclockingEnhance");
        if (data.hasKey("MagicFluidTank")) {
            this.magicFluidTank.readFromNBT(data.getCompoundTag("MagicFluidTank"));
        }
    }


    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.mana);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.mana = buf.readInt();
    }

    @Override
    public int getMaxMana() {
        return MAX_MANA;
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public boolean consumeMana(int amount, boolean simulate) {
        if (mana > amount) {
            if (!simulate) mana -= amount;
            return true;
        }
        return false;
    }

    @Override
    public double getTimeReduce() {
        return timeReduce;
    }

    @Override
    public double getEnergyReduce() {
        return energyReduce;
    }

    @Override
    public int getOverclockingEnhance() {
        return OverclockingEnhance;
    }

    @Override
    public int getParallelEnhance() {
        return ParallelEnhance;
    }

    public boolean isFull() {
        return mana >= MAX_MANA;
    }

    public void receiveMana(int amount) {
        if (!isFull()) mana += amount;
        this.mana = Math.min(this.mana, this.getMaxMana());
    }

    public MultiblockAbility<IManaHatch> getAbility() {
        return POMultiblockAbility.MANA_HATCH;
    }

    @Override
    public void registerAbilities(AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

}

