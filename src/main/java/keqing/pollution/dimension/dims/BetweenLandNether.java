package keqing.pollution.dimension.dims;

import keqing.pollution.dimension.biome.BiomeProviderBetweenLandNether;
import keqing.pollution.dimension.biome.POBiomeHandler;
import keqing.pollution.dimension.worldgen.ChunkGenerator.ChunkGeneratorBNT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.sky.BLRainRenderer;
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import static keqing.pollution.dimension.worldgen.PODimensionType.BTM;

public class BetweenLandNether extends WorldProvider {
    public void init() {
        //this.biomeProvider = new BiomeProviderBetweenLandNether();// 初始化你维度的生物群系提供器
        this.biomeProvider = new BiomeProviderSingle(POBiomeHandler.BNTNether_BIOME);// 初始化你维度的生物群系提供器
        this.doesWaterVaporize = true;
    }

    protected BetweenlandsWorldStorage getWorldData() {
        return BetweenlandsWorldStorage.forWorld(this.world);
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return new Vec3d(0.05D, 0.15D, 0.05D); // 深绿色
    }

    protected void generateLightBrightnessTable() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
        }
    }


    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorBNT(this.world, this.world.getWorldInfo().isMapFeaturesEnabled(), this.world.getSeed());
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
        return BTM;
    }

    @Override
    public boolean canRespawnHere() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IRenderHandler getWeatherRenderer() {
        if (this.getEnvironmentEventRegistry().snowfall.isSnowing()) {
            return BLSnowRenderer.INSTANCE;
        } else {
            return this.world.getRainStrength(1.0F) > 0.001F ? BLRainRenderer.INSTANCE : null;
        }
    }

    public BLEnvironmentEventRegistry getEnvironmentEventRegistry() {
        return this.getWorldData().getEnvironmentEventRegistry();
    }
}
