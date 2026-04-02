package meowmel.pollution.dimension.biome.biomes;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class POBiomeBlood extends Biome
{
    public POBiomeBlood()
    {
        super(new Biome.BiomeProperties("Blood Basic").setWaterColor(0xDC143C).setTemperature(0.5F));
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityGhast.class, 10, 1, 1));
        this.topBlock = Blocks.DIRT.getDefaultState();
        this.fillerBlock = Blocks.DIRT.getDefaultState();
        //this.decorator = new BiomeEndDecorator();
    }

    /**
     * takes temperature, returns color
     */
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature)
    {
        return 0;
    }
}