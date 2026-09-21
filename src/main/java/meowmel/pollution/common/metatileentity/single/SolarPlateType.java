package meowmel.pollution.common.metatileentity.single;

import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

/**
 * 太阳能板的种类，写法参考 {@code LargeTurbineType}：
 * 把原来散落在机器里的 {@code int kind} 与一串 if 分支收拢成类型对象，
 * 工作条件、增产条件、贴图与文案都由类型自己提供。
 */
public enum SolarPlateType {

    /** 1：风（灵气）——高空气流更强 */
    WIND(1, "wind", () -> POTextures.AIR,
            (world, pos, facing) -> pos.getY() > 160,
            SolarPlateType::daytimeAndSkyVisible),

    /** 2：混沌——只能在夜晚工作 */
    CHAOS(2, "chaos", () -> POTextures.DARK,
            (world, pos, facing) -> !world.isDaytime(),
            SolarPlateType::always),

    /** 3：地——地下深处 */
    EARTH(3, "earth", () -> POTextures.EARTH,
            (world, pos, facing) -> pos.getY() < 10,
            SolarPlateType::daytime),

    /** 4：火——只能在地狱工作 */
    FIRE(4, "fire", () -> POTextures.FIRE,
            (world, pos, facing) -> world.provider.getDimension() == -1,
            SolarPlateType::always),

    /** 5：秩序——只在白天工作 */
    ORDER(5, "order", () -> POTextures.ORDER,
            (world, pos, facing) -> world.isDaytime(),
            SolarPlateType::daytimeAndSkyVisible),

    /** 6：水——正下方有水 */
    WATER(6, "water", () -> POTextures.WATER,
            (world, pos, facing) -> world.getBlockState(pos.down()) == Blocks.WATER.getDefaultState(),
            SolarPlateType::daytimeAndSkyVisible);

    /** 太阳能板的工作/增产条件 */
    @FunctionalInterface
    public interface Condition {

        boolean test(World world, BlockPos pos, EnumFacing frontFacing);
    }

    /** 种类的编号，用于注册名与物品顺序 */
    private final int kind;
    private final String name;
    /** 贴图在客户端初始化时才创建，因此延迟获取 */
    private final Supplier<SimpleOverlayRenderer> overlay;
    /** 满足后按高一档电压发电 */
    private final Condition boostCondition;
    /** 机器的基础工作条件 */
    private final Condition workCondition;

    SolarPlateType(int kind, String name, Supplier<SimpleOverlayRenderer> overlay,
                   Condition boostCondition, Condition workCondition) {
        this.kind = kind;
        this.name = name;
        this.overlay = overlay;
        this.boostCondition = boostCondition;
        this.workCondition = workCondition;
    }

    public int getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public SimpleOverlayRenderer getOverlay() {
        return overlay.get();
    }

    /**
     * @return 是否满足增产条件
     */
    public boolean isBoosted(World world, BlockPos pos, EnumFacing frontFacing) {
        return boostCondition.test(world, pos, frontFacing);
    }

    /**
     * @return 机器当前是否可以工作
     */
    public boolean meetsWorkCondition(World world, BlockPos pos, EnumFacing frontFacing) {
        return workCondition.test(world, pos, frontFacing);
    }

    /**
     * @return 种类名称的 lang key
     */
    public String getNameKey() {
        return "pollution.machine.solar_plate.type." + name;
    }

    /**
     * @return 增产条件说明的 lang key
     */
    public String getBoostTooltipKey() {
        return "pollution.machine.solar_plate.boost." + name;
    }

    private static boolean always(World world, BlockPos pos, EnumFacing frontFacing) {
        return true;
    }

    private static boolean daytime(World world, BlockPos pos, EnumFacing frontFacing) {
        return world.isDaytime();
    }

    /**
     * 白天且机器正面 7x7 范围内可以看见天空
     */
    private static boolean daytimeAndSkyVisible(World world, BlockPos pos, EnumFacing frontFacing) {
        if (!world.isDaytime()) return false;

        BlockPos from = pos.up(8).offset(frontFacing.rotateY(), 3);
        BlockPos to = pos.up(8).offset(frontFacing.rotateYCCW(), 3).offset(frontFacing.getOpposite(), 6);
        for (BlockPos check : BlockPos.getAllInBox(from, to)) {
            if (!world.canSeeSky(check.up())) return false;
        }
        return true;
    }
}
