package keqing.pollution.dimension.dims;

import keqing.pollution.dimension.biome.POBiomeHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;

public class DimensionDemiplane extends WorldProvider {
	@Override
	protected void init() {
		this.hasSkyLight = true; // 如果你的维度应该有天空光照，设为true
		this.biomeProvider = new BiomeProviderSingle(POBiomeHandler.DEMIPLANE_BIOME);// 初始化你维度的生物群系提供器
		// 设置你维度的ID，通常通过DimensionManager.getNextFreeDimId()获取
	}

	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
		return new Vec3d(0.35, 0.0, 0.35);
	}

	@Override
	public DimensionType getDimensionType() {
		// 这里你需要返回一个DimensionType实例，它代表了你的维度类型
		// 可以是一个预先创建好的实例，或者在此处直接创建并返回
		return DimensionType.register("Demiplane", "_demiplane", 527, DimensionDemiplane.class, false);
	}

	@Override
	public boolean canRespawnHere() {
		// 控制玩家是否可以在这个维度重生
		// 如果返回false，玩家死后将在主世界重生
		return false;
	}

	@Override
	public boolean isSurfaceWorld() {
		// 如果你的维度被视为一个表面世界（如主世界），返回true
		// 这影响了一些游戏机制，比如天气
		return true;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return 0.5F; // 设置太阳高度角为固定值
	}

	@Override
	public boolean isDaytime() {
		return true; // 始终返回白天
	}

	@Override
	public float getSunBrightness(float partialTicks) {
		return 0.2F; // 设置太阳亮度为昏暗
	}

	@Override
	public float getSunBrightnessFactor(float celestialAngle) {
		return 1.0F; // 设置太阳亮度缩放因子为1.0
	}
	// 根据需要，你还可以重写更多的方法来定制你的维度

}