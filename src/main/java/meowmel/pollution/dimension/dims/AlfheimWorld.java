package meowmel.pollution.dimension.dims;

import meowmel.pollution.POConfig;
import meowmel.pollution.dimension.biome.BiomeProviderAlfheim;
import meowmel.pollution.dimension.worldgen.ChunkGenerator.ChunkGeneratorAlfheim;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static meowmel.pollution.dimension.worldgen.PODimensionType.ALFHEIM_WORLD;

/** Base terrain-only Alfheim dimension provider. */
public class AlfheimWorld extends WorldProvider {

    @SideOnly(Side.CLIENT)
    private IRenderHandler gardenOfGlassSky;

    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new BiomeProviderAlfheim(this.world.getSeed());
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorAlfheim(this.world, this.world.getSeed());
    }

    @Override
    public DimensionType getDimensionType() {
        return ALFHEIM_WORLD;
    }

    @Override
    public boolean canRespawnHere() {
        return POConfig.WorldSettingSwitch.enableAlfheimRespawn;
    }

    @Override
    public boolean isSurfaceWorld() {
        return true;
    }

    public float getCloudHeight() {
        return 164.0F;
    }

    public boolean getWorldHasVoidParticles() {
        return false;
    }

    /** Uses the Botania renderer identified by PersonalSpace as the Garden of Glass sky. */
    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getSkyRenderer() {
        if (gardenOfGlassSky == null) {
            try {
                gardenOfGlassSky = (IRenderHandler) Class
                        .forName("vazkii.botania.client.render.world.SkyblockSkyRenderer")
                        .newInstance();
            } catch (ReflectiveOperationException exception) {
                return super.getSkyRenderer();
            }
        }
        return gardenOfGlassSky;
    }
}
