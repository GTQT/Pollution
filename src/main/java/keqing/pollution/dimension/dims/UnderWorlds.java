package keqing.pollution.dimension.dims;

import keqing.pollution.dimension.biome.POBiomeHandler;
import keqing.pollution.dimension.worldgen.ChunkGenerator.ChunkGeneratorUnderWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static keqing.pollution.dimension.worldgen.PODimensionType.UNDER_WORLD;

public class UnderWorlds extends WorldProvider {
    public void init() {
        this.biomeProvider = new BiomeProviderSingle(POBiomeHandler.UnderWorld_BIOME);// 初始化你维度的生物群系提供器
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3d(0.1D, 0.1D, 0.1D); // 深绿色
    }

    protected void generateLightBrightnessTable() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
        }
    }


    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorUnderWorld(this.world, this.world.getWorldInfo().isMapFeaturesEnabled(), this.world.getSeed());
    }

    public boolean isSurfaceWorld() {
        return false;
    }

    public boolean canCoordinateBeSpawn(int x, int z) {
        return false;
    }

    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.5F;
    }

    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z) {
        return true;
    }

    @Override
    public DimensionType getDimensionType() {
        return UNDER_WORLD;
    }

    @Override
    public boolean canRespawnHere() {
        return true;
    }
}
