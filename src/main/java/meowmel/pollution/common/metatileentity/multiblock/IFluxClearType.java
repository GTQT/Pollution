package meowmel.pollution.common.metatileentity.multiblock;

import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 大型空气过滤机的等级类型，写法参考 {@code ILargeTurbineType}
 */
public interface IFluxClearType {

    /**
     * @return 等级名称，用于结构定义与注册名
     */
    @NotNull
    String getName();

    /**
     * @return 机器电压等级
     */
    int getTier();

    /**
     * @return 清理半径，单位区块
     */
    int getRadius();

    /**
     * @return 外壳方块
     */
    @NotNull
    IBlockState getCasingState();

    /**
     * @return 进气口方块
     */
    @NotNull
    IBlockState getIntakeState();

    /**
     * @return 外壳渲染
     */
    @NotNull
    ICubeRenderer getCasingRenderer();

    /**
     * @return 正面覆盖图
     */
    @NotNull
    ICubeRenderer getFrontOverlay();
}
