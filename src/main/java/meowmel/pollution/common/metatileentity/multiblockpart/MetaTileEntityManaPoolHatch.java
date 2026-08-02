package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
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
import lombok.Getter;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.List;

import static gregtech.api.GTValues.V;

public class MetaTileEntityManaPoolHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IManaHatch>, IManaHatch {

    protected final boolean isExport;
    protected final PoolType poolType;
    protected ManaContainer manaContainer;
    private long lastExternalReceiveTick = Long.MIN_VALUE;
    private long externalManaReceivedThisTick;
    protected long manaTransferredThisTick;

    public MetaTileEntityManaPoolHatch(ResourceLocation metaTileEntityId, PoolType poolType, boolean isExport) {
        super(metaTileEntityId, poolType.getMachineTier());
        this.poolType = poolType;
        this.isExport = isExport;
        manaContainer = new ManaContainer(poolType.getCapacity());
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaPoolHatch(this.metaTileEntityId, poolType, isExport);
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public void update() {
        super.update();
        manaTransferredThisTick = 0L;
        if(!isExport)return;
        if (!getWorld().isRemote) {
            for(EnumFacing facing : EnumFacing.VALUES)
            {
                TileEntity tileEntity = getWorld().getTileEntity(getPos().offset(facing));
                if(tileEntity instanceof IManaReceiver manaReceiver)
                {
                    if(!manaReceiver.isFull()){
                        long trans = Math.min(manaContainer.getMana(), poolType.getTransferRate());
                        int before = manaReceiver.getCurrentMana();
                        manaReceiver.recieveMana((int) trans);
                        int after = manaReceiver.getCurrentMana();
                        long removed = manaContainer.removeMana(
                                Math.min(trans, Math.max(0L, (long) after - before)));
                        manaTransferredThisTick += removed;
                        return;
                    }
                }

                MetaTileEntity metaTileEntity = GTUtility.getMetaTileEntity(getWorld(), this.getPos().offset(facing));
                if(metaTileEntity instanceof MetaTileEntityManaPoolHatch manaHatch){
                    if(!manaHatch.isExport){
                        if(!manaHatch.isFull()){
                            long trans = Math.min(manaContainer.getMana(), poolType.getTransferRate());
                            long before = manaHatch.getMana();
                            manaHatch.receiveMana((int) trans);
                            long removed = manaContainer.removeMana(
                                    Math.min(trans, Math.max(0L, manaHatch.getMana() - before)));
                            manaTransferredThisTick += removed;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public MultiblockAbility<IManaHatch> getAbility() {
        if (isExport) return POMultiblockAbility.MANA_OUTPUT_POOL;
        return POMultiblockAbility.MANA_INPUT_POOL;
    }

    @Override
    public long getMaxMana() {
        return manaContainer.getMaxMana();
    }

    @Override
    public long getMana() {
        return manaContainer.getMana();
    }

    @Override
    public boolean consumeMana(long amount, boolean simulate) {
        return manaContainer.drainMana(amount, simulate);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("store", manaContainer.serializeNBT());
        return super.writeToNBT(data);
    }


    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        long stored = data.getCompoundTag("store").getLong("Stored");
        this.manaContainer = new ManaContainer(poolType.getCapacity());
        this.manaContainer.addMana(stored);
    }

    public boolean isFull() {
        return manaContainer.isFull();
    }

    public void receiveMana(long amount) {
        if (!isFull()) manaContainer.addMana(amount);
    }

    @Override
    public void receiveManaFromBurst(int amount) {
        if (!canReceiveManaFromBursts()) return;
        receiveExternalMana(amount);
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        if (isExport || manaContainer.isFull()) return false;
        refreshExternalReceiveCounter();
        return getRemainingExternalReceiveRate() > 0L;
    }

    protected long receiveExternalMana(long amount) {
        if (amount <= 0L || isExport) return 0L;
        refreshExternalReceiveCounter();
        long accepted = manaContainer.addMana(Math.min(amount, getRemainingExternalReceiveRate()));
        externalManaReceivedThisTick += accepted;
        return accepted;
    }

    private void refreshExternalReceiveCounter() {
        if (getWorld() == null) return;
        long currentTick = getWorld().getTotalWorldTime();
        if (currentTick != lastExternalReceiveTick) {
            lastExternalReceiveTick = currentTick;
            externalManaReceivedThisTick = 0L;
        }
    }

    protected long getRemainingExternalReceiveRate() {
        refreshExternalReceiveCounter();
        return Math.max(0L, poolType.getTransferRate() - externalManaReceivedThisTick);
    }

    protected long getRemainingOutputTransferRate() {
        return Math.max(0L, poolType.getTransferRate() - manaTransferredThisTick);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.mana_pool_hatch.tooltip"));
        tooltip.add(I18n.format("pollution.machine.mana_pool_hatch.type",
                I18n.format("pollution.machine.mana_pool_hatch.type." + poolType.getName())));
        tooltip.add(I18n.format(isExport
                ? "pollution.machine.mana_pool_output_hatch.tooltip"
                : "pollution.machine.mana_pool_input_hatch.tooltip"));
        tooltip.add(I18n.format("pollution.machine.mana_pool_hatch.capacity", getMaxMana()));
        tooltip.add(I18n.format("pollution.machine.mana_pool_hatch.transfer", poolType.getTransferRate()));
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
        return isExport ? POTextures.MANA_POOL_HATCH_OUTPUT : POTextures.MANA_POOL_HATCH_INPUT;
    }

    public enum PoolType {
        DILUTED("diluted", GTValues.LV, 10_000L),
        NORMAL("normal", GTValues.LuV, 1_000_000L),
        MYTHIC("mythic", GTValues.UEV, 1_000_000L);

        private final String name;
        private final int machineTier;
        private final long capacity;

        PoolType(String name, int machineTier, long capacity) {
            this.name = name;
            this.machineTier = machineTier;
            this.capacity = capacity;
        }

        public String getName() {
            return name;
        }

        public int getMachineTier() {
            return machineTier;
        }

        public long getCapacity() {
            return capacity;
        }

        public long getTransferRate() {
            return V[machineTier];
        }
    }
}
