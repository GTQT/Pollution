package meowmel.pollution.common.block.tile;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTUtility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

import javax.annotation.Nullable;

public class TileEntityMineralExtractor extends TileEntity implements ITickable, IAspectSource {

    //常量
    private static final int MAX_STOCK = 10000;            // 两仓容量（输入足够时可存满 10000）
    private static final int MAGIC_COST_PER_TICK = 5;      // 每 tick 消耗 5 魔法
    private static final int ENTROPY_COST_PER_TICK = 5;    // 每 tick 消耗 5 熵
    private static final int MINE_INTERVAL = 5;            // 每 5 tick 采/产 1 个方块（= 每方块 25 熵 + 25 魔法）
    private static final int MINE_RADIUS = 24;             // 3×3×3 区块 = 每个方向 ±24 格
    private static final int SCAN_PER_TICK = 2048;         // 每 tick 扫描方块数
    private static final int OUTPUT_SLOTS = 9;             // 物品输出槽数
    private static final int OUTPUT_TANK_CAPACITY = 10000; // 流体输出槽容量

    //两个源质仓
    private final Aspect chaosAspect = Aspect.ENTROPY;
    private int chaosAmount = 0;
    private final Aspect magicAspect = Aspect.MAGIC;
    private int magicAmount = 0;

    //产物槽
    private final ItemStackHandler outputInventory = new ItemStackHandler(OUTPUT_SLOTS);
    private final FluidTank outputTank = new FluidTank(OUTPUT_TANK_CAPACITY);

    //状态
    private int mode = 0;                 //0=实矿 1=拟矿 2=虚流
    private boolean enabled = false;      //放置默认停

    //扫描、开采状态
    private boolean scanInitialized = false;
    private int scanRing = 0;
    private int scanDx = 0, scanDy = 0, scanDz = 0;
    private BlockPos pendingTarget = null;
    private int pendingOreItemId = 0;
    private int pendingOreMeta = 0;
    private int mineTimer = 0;            //距下次采/产方块的剩余 tick

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        //停机也抽取、输出
        //运行时扫描、采、消耗
        pullEssentiaFromNeighbors();

        if (enabled) {
            tickRunning();
        }

        pushOutputToNeighbors();
    }

    // 逻辑
    private void tickRunning() {
        //每 tick 消耗 5 熵 + 5 魔法
        //空转依旧扣除 ？待定
        if (magicAmount < MAGIC_COST_PER_TICK || chaosAmount < ENTROPY_COST_PER_TICK) {
            return; //源不足
        }
        magicAmount -= MAGIC_COST_PER_TICK;
        chaosAmount -= ENTROPY_COST_PER_TICK;
        markDirty();

        mineTimer--;
        if (mineTimer > 0) return;
        mineTimer = MINE_INTERVAL;

        switch (mode) {
            case 1:
                produceVirtualBlock(new ItemStack(Blocks.GRASS)); // 虚矿草方块试
                break;
            case 2:
                produceVirtualBlock(new ItemStack(Blocks.STONE)); // 虚流石头试
                break;
            default: {
                if (pendingTarget == null) {
                    scanForOre();
                }
                if (pendingTarget != null) {
                    if (mineBlock(pendingTarget)) {
                        pendingTarget = null;
                        resetScanPosition(); // 采完立即从机器中心重新找最近的矿
                    }
                    //开采失败，重试
                }
                break;
            }
        }
    }

    //虚拟产物 占位
    private void produceVirtualBlock(ItemStack stack) {
        ItemHandlerHelper.insertItemStacked(outputInventory, stack, false);
        markDirty();
    }

    //源质抽取
    private void pullEssentiaFromNeighbors() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighborPos = pos.offset(facing);
            if (!world.isBlockLoaded(neighborPos)) continue;
            TileEntity neighbor = world.getTileEntity(neighborPos);
            if (neighbor == null) continue;
            if (!isEssentiaContainer(neighbor)) continue; //只从源质容器抽
            EnumFacing side = facing.getOpposite();

            if (chaosAmount < MAX_STOCK) {
                pullAspect(neighbor, side, chaosAspect, MAX_STOCK - chaosAmount);
            }
            if (magicAmount < MAX_STOCK) {
                pullAspect(neighbor, side, magicAspect, MAX_STOCK - magicAmount);
            }
        }
    }

    /**            Deepseek发力吧（doge）
     *
     *       又能吃白饭了_\
     *                 (._.)  ~  .-=~=-.  ~  |~~~|  ~      // 鲸鱼娘（单行）
     *            现在老板花钱了，那我要把姿态摆得更到位 你最好是
     */

    private static final int MAX_ESSENTIA_PER_TICK = 1000; //每 tick 单次抽取上限

    private boolean pullAspect(TileEntity neighbor, EnumFacing side, Aspect aspect, int amount) {
        // 想要抽的：目标缺口 与 单次上限 取小
        int want = Math.min(amount, MAX_ESSENTIA_PER_TICK);

        // 1) IEssentiaTransport（TC 罐子 / 超级缸）
        IEssentiaTransport transport = getEssentiaTransport(neighbor);
        if (transport != null) {
            if (transport.canOutputTo(side)
                    && aspect.equals(transport.getEssentiaType(side))
                    && transport.getEssentiaAmount(side) > 0) {
                int take = Math.min(want, transport.getEssentiaAmount(side));
                int taken = transport.takeEssentia(aspect, take, side);
                if (taken > 0) {
                    addToChamber(aspect, taken);
                    return true;
                }
            }
        }

        // 2) IAspectSource（备用）
        IAspectSource source = getAspectSource(neighbor);
        if (source != null) {
            if (source.doesContainerContainAmount(aspect, want)
                    && source.takeFromContainer(aspect, want)) {
                addToChamber(aspect, want);
                return true;
            }
        }
        return false;
    }

    private void addToChamber(Aspect aspect, int amount) {
        if (aspect == chaosAspect) {
            chaosAmount = Math.min(MAX_STOCK, chaosAmount + amount);
        } else if (aspect == magicAspect) {
            magicAmount = Math.min(MAX_STOCK, magicAmount + amount);
        }
        markDirty();
    }

    // ================= 源质容器判定（关键修复）：解开 GT MetaTileEntityHolder =================
    private boolean isEssentiaContainer(TileEntity te) {
        if (te == null) return false;
        if (te instanceof IAspectContainer || te instanceof IEssentiaTransport) return true;
        // GT 方块（如 GTQT 源质缸/超级缸）的 TileEntity 只是 Holder，
        // 真正的接口在 MetaTileEntity 上，必须解开判断。
        MetaTileEntity mte = getMetaTileEntity(te);
        return mte instanceof IAspectContainer || mte instanceof IEssentiaTransport;
    }

    private IAspectSource getAspectSource(TileEntity te) {
        if (te instanceof IAspectSource) return (IAspectSource) te;
        MetaTileEntity mte = getMetaTileEntity(te);
        if (mte instanceof IAspectSource) return (IAspectSource) mte;
        return null;
    }

    private IEssentiaTransport getEssentiaTransport(TileEntity te) {
        if (te instanceof IEssentiaTransport) return (IEssentiaTransport) te;
        MetaTileEntity mte = getMetaTileEntity(te);
        if (mte instanceof IEssentiaTransport) return (IEssentiaTransport) mte;
        return null;
    }

    private MetaTileEntity getMetaTileEntity(TileEntity te) {
        if (te instanceof MetaTileEntityHolder) {
            return ((MetaTileEntityHolder) te).getMetaTileEntity();
        }
        return null;
    }

    // ================= 输出：自动识别（源质容器绝不塞物品/流体） =================
    private void pushOutputToNeighbors() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (te == null) continue;
            // 源质容器（含 GT 源质缸/超级缸）：只做输入，绝不输出物品/流体（防止矿物倒灌）
            if (isEssentiaContainer(te)) continue;

            IItemHandler destItem = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (destItem != null) {
                for (int i = 0; i < outputInventory.getSlots(); i++) {
                    ItemStack stack = outputInventory.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    ItemStack rest = ItemHandlerHelper.insertItemStacked(destItem, stack.copy(), false);
                    outputInventory.setStackInSlot(i, rest);
                }
            }

            if (outputTank.getFluid() != null && outputTank.getFluidAmount() > 0) {
                IFluidHandler destFluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
                if (destFluid != null) {
                    FluidStack fluid = outputTank.getFluid();
                    if (fluid == null) continue;
                    int filled = destFluid.fill(fluid, true);
                    if (filled > 0) {
                        outputTank.drain(filled, true);
                    }
                }
            }
        }
    }

    //又给你充了20块，继续，多干点活
    // ================= 扫描：3×3×3 区块内由近到远（琥珀降级） =================
    private void scanForOre() {
        if (!scanInitialized) {
            resetScanPosition();
            scanInitialized = true;
        }
        BlockPos fallback = null;
        for (int i = 0; i < SCAN_PER_TICK; i++) {
            if (!advanceScanPosition()) {
                resetScanPosition();
                break;
            }
            if (scanDx == 0 && scanDy == 0 && scanDz == 0) continue; // 跳过机器自身
            BlockPos t = pos.add(scanDx, scanDy, scanDz);
            if (t.getY() < 0 || t.getY() >= 256) continue;
            if (!world.isBlockLoaded(t)) continue;
            ItemStack item = getOreItem(t);
            if (item == null || item.isEmpty()) continue;
            if (isAmberOre(item)) {
                if (fallback == null) fallback = t; // 琥珀降级为后备
                continue;
            }
            pendingTarget = t;
            updatePendingOre(item);
            return;
        }
        // 本轮扫描结束仍未找到优先矿
        if (pendingTarget == null) {
            if (fallback != null) {
                pendingTarget = fallback;
                updatePendingOre(getOreItem(fallback));
            } else {
                pendingOreItemId = 0; // 整片扫完确实无矿 → 检测栏显示"无"
                pendingOreMeta = 0;
            }
        }
    }

    private void resetScanPosition() {
        scanRing = 0;
        scanDx = 0;
        scanDy = 0;
        scanDz = -1;
    }

    /**
     * 按环推进：dz → dy → dx → 进入下一个更大的环（3D 壳）。
     * 换环时把 dx/dy/dz 全部重置为新环 -r，避免"负方向最外层面"被跳过。
     */
    private boolean advanceScanPosition() {
        while (true) {
            scanDz++;
            if (scanDz > scanRing) {
                scanDz = -scanRing;
                scanDy++;
                if (scanDy > scanRing) {
                    scanDy = -scanRing;
                    scanDx++;
                    if (scanDx > scanRing) {
                        scanRing++;
                        if (scanRing > MINE_RADIUS) {
                            return false;
                        }
                        scanDx = -scanRing;
                        scanDy = -scanRing;
                        scanDz = -scanRing;
                    }
                }
            }
            int maxAbs = Math.max(Math.abs(scanDx), Math.max(Math.abs(scanDy), Math.abs(scanDz)));
            if (maxAbs == scanRing) {
                return true;
            }
        }
    }

    /** 矿石判定：返回该格的矿石物品栈；空栈不会进入 OreDictionary 查询。 */
    private ItemStack getOreItem(BlockPos pos) {
        if (world == null || pos == null || !world.isBlockLoaded(pos)) return null;
        IBlockState state = world.getBlockState(pos);
        if (state == null || state.getBlock() == null || state.getBlock() == Blocks.AIR) return null;
        ItemStack stack = GTUtility.toItem(state);
        if (stack == null || stack.isEmpty()) return null;
        if (GTUtility.isOre(stack)) return stack;
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id).startsWith("ore")) {
                return stack;
            }
        }
        return null;
    }

    private boolean isAmberOre(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem().getRegistryName() != null
                && stack.getItem().getRegistryName().toString().toLowerCase().contains("amber")) {
            return true;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id).toLowerCase().contains("amber")) {
                return true;
            }
        }
        return false;
    }

    private void updatePendingOre(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            pendingOreItemId = 0;
            pendingOreMeta = 0;
            return;
        }
        pendingOreItemId = Item.getIdFromItem(stack.getItem());
        pendingOreMeta = stack.getMetadata();
    }

    // ================= 开采：单方块，装得下才采；掉落为空则回退方块本身 =================
    private boolean mineBlock(BlockPos target) {
        if (!world.isBlockLoaded(target)) return true;
        if (getOreItem(target) == null) return true;
        IBlockState state = world.getBlockState(target);
        NonNullList<ItemStack> drops = NonNullList.create();
        state.getBlock().getDrops(drops, world, target, state, 0);
        if (drops == null || drops.isEmpty()) {
            // 部分矿石 getDrops 为空 → 回退为方块自身物品，保证必有产出
            ItemStack blockItem = GTUtility.toItem(state);
            if (blockItem == null || blockItem.isEmpty()) return true;
            drops = NonNullList.create();
            drops.add(blockItem);
        }
        if (!canInsertAll(drops)) return false; // 输出装不下：不采，下轮重试
        insertDrops(drops);
        world.setBlockToAir(target);
        return true;
    }

    private boolean canInsertAll(NonNullList<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (drop == null || drop.isEmpty()) continue;
            if (!ItemHandlerHelper.insertItemStacked(outputInventory, drop, true).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean insertDrops(NonNullList<ItemStack> drops) {
        boolean allInserted = true;
        for (ItemStack drop : drops) {
            if (drop == null || drop.isEmpty()) continue;
            ItemStack rest = ItemHandlerHelper.insertItemStacked(outputInventory, drop, false);
            if (!rest.isEmpty()) {
                allInserted = false;
                break;
            }
        }
        return allInserted;
    }

    //查询 GUI用
    public static int getMaxStockStatic() { return MAX_STOCK; }
    public int getMode() { return mode; }
    public void setMode(int mode) { this.mode = ((mode % 3) + 3) % 3; markDirty(); }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; markDirty(); }
    public int getChaosAmount() { return chaosAmount; }
    public int getMagicAmount() { return magicAmount; }
    public int getPendingOreItemId() { return pendingOreItemId; }
    public int getPendingOreMeta() { return pendingOreMeta; }
    public ItemStackHandler getOutputInventory() { return outputInventory; }
    public FluidTank getOutputTank() { return outputTank; }

    //Capability
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) outputInventory;
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) outputTank;
        }
        return super.getCapability(capability, facing);
    }

    //IAspectSource
    @Override public int containerContains(Aspect aspect) {
        if (aspect == chaosAspect) return chaosAmount;
        if (aspect == magicAspect) return magicAmount;
        return 0;
    }
    @Override public boolean doesContainerAccept(Aspect aspect) {
        return aspect == chaosAspect || aspect == magicAspect;
    }
    @Override public int addToContainer(Aspect aspect, int amount) {
        int before = containerContains(aspect);
        addToChamber(aspect, amount);
        return amount - (containerContains(aspect) - before);
    }
    @Override public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect == chaosAspect && chaosAmount >= amount) {
            chaosAmount -= amount; markDirty(); return true;
        }
        if (aspect == magicAspect && magicAmount >= amount) {
            magicAmount -= amount; markDirty(); return true;
        }
        return false;
    }
    @Override public boolean takeFromContainer(AspectList aspectList) { return false; }
    @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return containerContains(aspect) >= amount;
    }
    @Override public boolean doesContainerContain(AspectList aspectList) {
        for (Aspect aspect : aspectList.getAspects()) {
            if (containerContains(aspect) > 0) return true;
        }
        return false;
    }
    @Override public AspectList getAspects() {
        AspectList list = new AspectList();
        if (chaosAmount > 0) list.add(chaosAspect, chaosAmount);
        if (magicAmount > 0) list.add(magicAspect, magicAmount);
        return list;
    }
    @Override public void setAspects(AspectList aspectList) {
        chaosAmount = 0; magicAmount = 0;
        if (aspectList == null) return;
        for (Aspect aspect : aspectList.getAspects()) {
            if (aspect == chaosAspect) chaosAmount = aspectList.getAmount(aspect);
            else if (aspect == magicAspect) magicAmount = aspectList.getAmount(aspect);
        }
    }
    @Override public boolean isBlocked() { return false; }

    //NBT
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        chaosAmount = compound.getInteger("chaosAmount");
        magicAmount = compound.getInteger("magicAmount");
        mode = compound.getInteger("mode");
        enabled = compound.getBoolean("enabled");
        mineTimer = compound.getInteger("mineTimer");
        pendingOreItemId = compound.getInteger("pendingOreItemId");
        pendingOreMeta = compound.getInteger("pendingOreMeta");

        // 手动读取物品槽，避免 NBT 里的旧尺寸(16)覆盖新尺寸(9)
        NBTTagCompound inv = compound.getCompoundTag("outputInventory");
        if (inv.hasKey("Items")) {
            NBTTagList list = inv.getTagList("Items", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("Slot");
                if (slot >= 0 && slot < OUTPUT_SLOTS) {
                    outputInventory.setStackInSlot(slot, new ItemStack(tag));
                }
            }
        }
        if (compound.hasKey("outputTank")) {
            outputTank.readFromNBT(compound.getCompoundTag("outputTank"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("chaosAmount", chaosAmount);
        compound.setInteger("magicAmount", magicAmount);
        compound.setInteger("mode", mode);
        compound.setBoolean("enabled", enabled);
        compound.setInteger("mineTimer", mineTimer);
        compound.setInteger("pendingOreItemId", pendingOreItemId);
        compound.setInteger("pendingOreMeta", pendingOreMeta);
        compound.setTag("outputInventory", outputInventory.serializeNBT());
        compound.setTag("outputTank", outputTank.writeToNBT(new NBTTagCompound()));
        return compound;
    }
}