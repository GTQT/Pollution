package meowmel.pollution.dimension.dims;

import meowmel.pollution.dimension.biome.BiomeProviderUnderground;
import meowmel.pollution.dimension.worldgen.ChunkGenerator.ChunkGeneratorUndergroundWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import static meowmel.pollution.dimension.worldgen.PODimensionType.UNDER_WORLD;

/**
 * 地下世界维度提供器 (WorldProvider)
 */
public class UndergroundWorlds extends WorldProvider {

    @Override
    public void init() {
        // 群系驱动：GenLayer 分布 8 群系（深窟兜底 + 7 风格），表面/装饰按群系，地形骨架不变
        this.biomeProvider = new BiomeProviderUnderground(this.world.getSeed(), this.world.getWorldType());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public @NotNull Vec3d getFogColor(float celestialAngle, float partialTicks) {
        // Dark green/gray fog color (深绿色/暗灰色雾气)
        return new Vec3d(0.1D, 0.1D, 0.1D);
    }

    @Override
    protected void generateLightBrightnessTable() {
        float ambientLight = 0.1F; // 基础环境光亮度

        for (int lightLevel = 0; lightLevel <= 15; ++lightLevel) {
            float darknessFactor = 1.0F - (float) lightLevel / 15.0F;
            // 计算光照亮度表，使用语义化变量替代原有的 f, f1
            this.lightBrightnessTable[lightLevel] = (1.0F - darknessFactor) / (darknessFactor * 3.0F + 1.0F) * 0.9F + ambientLight;
        }
    }

    @Override
    public @NotNull IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorUndergroundWorld(this.world, this.world.getWorldInfo().isMapFeaturesEnabled(), this.world.getSeed());
    }

    @Override
    public boolean isSurfaceWorld() {
        return false; // 非地表世界
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return false; // 不允许作为初始出生点坐标
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.5F; // 固定天体角度 (通常用于保持永恒黑夜或特定天空盒状态)
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean doesXZShowFog(int x, int z) {
        return true; // 始终显示雾气
    }

    @Override
    public @NotNull DimensionType getDimensionType() {
        return UNDER_WORLD;
    }

    @Override
    public boolean canRespawnHere() {
        return true; // 允许玩家死亡后在此维度复活 (通常配合特定床或机制使用)
    }
}