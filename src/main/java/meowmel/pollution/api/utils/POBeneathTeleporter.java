package meowmel.pollution.api.utils;

import meowmel.pollution.POConfig;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.block.tile.TileEntityBeneathReturnPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.Nullable;

/**
 * 地下世界（The Beneath，{@link POConfig.WorldSettingSwitch#UndergroundDimensionID}）的双向传送。
 *
 * <p>地下世界唯一的出入口：控制器面板上的按钮把玩家送到一个现场搭建的小平台，平台中央的返程传送器
 * （{@link PollutionBlocksInit#BLOCK_BENEATH_RETURN} 加上 {@link TileEntityBeneathReturnPad}）
 * 再按记录的坐标把人送回来。</p>
 */
public final class POBeneathTeleporter {

    /** 平台生成高度。地下世界全是虚空与洞穴，固定高度可以保证落点一定有落脚点。 */
    public static final int PLATFORM_Y = 64;
    /** 落点后的免传送冷却（tick）。 */
    private static final int PORTAL_COOLDOWN = 10;
    /** 平台半径（以返程传送器为中心）。 */
    private static final int PLATFORM_RADIUS = 2;
    /** 返程时在记录坐标附近搜索安全落点的最大半径。 */
    private static final int SAFE_SEARCH_RADIUS = 8;

    private POBeneathTeleporter() {
    }

    /**
     * 把玩家送到地下世界，并在落点搭好平台与返程传送器。
     *
     * <p>返程坐标记录的是「玩家点击按钮时所在的位置」，与具体是哪台控制器无关。</p>
     *
     * <p>只在服务端调用；内部会补一个 {@code server.addScheduledTask}，
     * 避免在 GUI 数据包处理过程中写方块或切换维度。</p>
     *
     * @return 是否成功发起传送
     */
    public static boolean sendToBeneath(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP playerMP)) return false;
        if (playerMP.dimension == POConfig.WorldSettingSwitch.UndergroundDimensionID) {
            playerMP.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.already_there"), true);
            return false;
        }

        MinecraftServer server = playerMP.getServer();
        if (server == null) return false;

        // 先把返程点记下来再落地，即使之后某一步失败，返程信息也不会丢
        BlockPos returnPos = new BlockPos(playerMP.posX, playerMP.posY, playerMP.posZ);
        int returnDimension = playerMP.dimension;

        server.addScheduledTask(() -> teleportToBeneath(server, playerMP, returnPos, returnDimension));
        return true;
    }

    private static void teleportToBeneath(MinecraftServer server, EntityPlayerMP player,
                                          BlockPos returnPos, int returnDimension) {
        int targetDimension = POConfig.WorldSettingSwitch.UndergroundDimensionID;
        WorldServer target = server.getWorld(targetDimension);
        if (target == null) {
            player.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.dimension_missing",
                    targetDimension), true);
            return;
        }

        BlockPos landing = new BlockPos(returnPos.getX(), PLATFORM_Y, returnPos.getZ());
        buildPlatform(target, landing, returnDimension, returnPos);

        // 落地后给一段免传送冷却，避免将来落点附近出现别的传送手段时被立刻弹走
        player.timeUntilPortal = PORTAL_COOLDOWN;
        player.changeDimension(targetDimension, new Arrival(target, landing));

        player.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.arrived"), true);
    }

    /**
     * 把玩家送回返程传送器记录的坐标。
     *
     * @param targetDimension 记录的返程维度
     * @param targetPos 记录的返程坐标
     * @return 是否成功发起传送
     */
    public static boolean sendBack(EntityPlayer player, int targetDimension, BlockPos targetPos) {
        if (!(player instanceof EntityPlayerMP playerMP)) return false;

        MinecraftServer server = playerMP.getServer();
        if (server == null) return false;

        server.addScheduledTask(() -> teleportBack(server, playerMP, targetDimension, targetPos));
        return true;
    }

    private static void teleportBack(MinecraftServer server, EntityPlayerMP player, int targetDimension,
                                     BlockPos targetPos) {
        WorldServer target = server.getWorld(targetDimension);
        if (target == null) {
            player.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.return_dimension_missing",
                    targetDimension), true);
            return;
        }

        BlockPos safe = findSafe(target, targetPos);
        if (safe == null) {
            player.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.return_unsafe"), true);
            return;
        }

        player.timeUntilPortal = PORTAL_COOLDOWN;

        if (player.dimension == targetDimension) {
            // 同维度只需要挪位置，走 changeDimension 反而会多发一遍重生包
            player.connection.setPlayerLocation(safe.getX() + 0.5D, safe.getY(), safe.getZ() + 0.5D,
                    player.rotationYaw, player.rotationPitch);
            player.motionX = 0.0D;
            player.motionY = 0.0D;
            player.motionZ = 0.0D;
        } else {
            player.changeDimension(targetDimension, new Arrival(target, safe));
        }

        player.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.returned"), true);
    }

    /**
     * 现场生成落点平台：脚下 5×5 石台、中央石砖地标、返程传送器，以及净空。
     */
    private static void buildPlatform(WorldServer world, BlockPos center, int returnDimension, BlockPos returnPos) {
        // 先把要写方块的那个区块同步加载出来，否则刚落地时区块还没加载，玩家会直接往下掉
        world.getChunk(center.getX() >> 4, center.getZ() >> 4);

        IBlockState floor = Blocks.STONE.getDefaultState();
        IBlockState landmark = Blocks.STONEBRICK.getDefaultState();

        for (int dx = -PLATFORM_RADIUS; dx <= PLATFORM_RADIUS; dx++) {
            for (int dz = -PLATFORM_RADIUS; dz <= PLATFORM_RADIUS; dz++) {
                boolean centerColumn = dx == 0 && dz == 0;
                setBlock(world, center.add(dx, -1, dz), centerColumn ? landmark : floor);
                if (!centerColumn) {
                    setBlock(world, center.add(dx, 0, dz), Blocks.AIR.getDefaultState());
                    setBlock(world, center.add(dx, 1, dz), Blocks.AIR.getDefaultState());
                    setBlock(world, center.add(dx, 2, dz), Blocks.AIR.getDefaultState());
                }
            }
        }

        // 中央两格净空：返程传送器只占下面那一格
        setBlock(world, center, Blocks.AIR.getDefaultState());
        setBlock(world, center.up(), Blocks.AIR.getDefaultState());

        placeReturnPad(world, center, returnDimension, returnPos);
    }

    /** 在平台中央放下返程传送器，并把返程坐标写进它的 TileEntity。 */
    private static void placeReturnPad(WorldServer world, BlockPos pos, int returnDimension, BlockPos returnPos) {
        // 平台可能已经生成过，此时只刷新返程坐标
        if (!(world.getTileEntity(pos) instanceof TileEntityBeneathReturnPad)) {
            setBlock(world, pos, PollutionBlocksInit.BLOCK_BENEATH_RETURN.getDefaultState());
        }

        if (world.getTileEntity(pos) instanceof TileEntityBeneathReturnPad returnPad) {
            returnPad.setReturnTarget(returnDimension, returnPos);
        }
    }

    private static void setBlock(WorldServer world, BlockPos pos, IBlockState state) {
        // flag 2：同步给客户端；flag 16：不触发邻方块更新，避免平台边缘方块互相连锁更新
        world.setBlockState(pos, state, 2 | 16);
    }

    /** 在目标坐标附近找一个脚下有方块、身体两格都能站下的位置。 */
    @Nullable
    private static BlockPos findSafe(WorldServer world, BlockPos origin) {
        for (int radius = 0; radius <= SAFE_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    BlockPos candidate = origin.add(dx, 0, dz);
                    if (isSafe(world, candidate)) return candidate;
                }
            }
        }
        return null;
    }

    private static boolean isSafe(WorldServer world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) return false;
        if (!world.getBlockState(pos.down()).isFullCube()) return false;
        IBlockState feet = world.getBlockState(pos);
        IBlockState head = world.getBlockState(pos.up());
        return feet.getBlock().isReplaceable(world, pos) && head.getBlock().isReplaceable(world, pos.up());
    }

    /** 把玩家精确放到目标坐标的传送器，不查找也不生成传送门。 */
    private static final class Arrival extends Teleporter {

        private final BlockPos destination;

        private Arrival(WorldServer world, BlockPos destination) {
            super(world);
            this.destination = destination;
        }

        @Override
        public void placeInPortal(Entity entity, float rotationYaw) {
            entity.setLocationAndAngles(destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D,
                    entity.rotationYaw, entity.rotationPitch);
            entity.motionX = 0.0D;
            entity.motionY = 0.0D;
            entity.motionZ = 0.0D;
            entity.fallDistance = 0.0F;
        }

        @Override
        public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
            placeInPortal(entity, rotationYaw);
            return true;
        }

        @Override
        public boolean makePortal(Entity entity) {
            return true;
        }
    }
}
