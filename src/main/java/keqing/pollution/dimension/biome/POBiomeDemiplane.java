package keqing.pollution.dimension.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;

import java.util.Random;

public class POBiomeDemiplane extends Biome {
	public POBiomeDemiplane() {
		super(new Biome.BiomeProperties("Demiplane_Biome").setWaterColor(0xADD8E6).setTemperature(0.5F));
		// 清除所有自然生成的生物列表
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();

	}

	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
		return new WorldGenTrees(true);
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {

	}

	/**
	 * Check if rain can occur in biome
	 */
	@Override
	public boolean canRain() {
		return true;
	}
}