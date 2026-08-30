package meowmel.pollution.dimension.worldgen.mapgen;

import com.google.common.collect.Lists;
import meowmel.pollution.dimension.worldgen.structure.StructureUndergroundBridgePieces;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.List;
import java.util.Random;

/**
 * 地下堡垒结构生成器（结构注册名 "UndergroundFortress"）。
 */
public class MapGenUndergroundBridge extends MapGenStructure {

    private static final int SEARCH_RADIUS_CHUNKS = 1000;

    private final List<Biome.SpawnListEntry> spawnList = Lists.newArrayList();

    public MapGenUndergroundBridge() {
        this.spawnList.add(new Biome.SpawnListEntry(EntityZombie.class, 8, 5, 5));
        this.spawnList.add(new Biome.SpawnListEntry(EntityShulker.class, 2, 5, 5));
        this.spawnList.add(new Biome.SpawnListEntry(EntityCreeper.class, 3, 4, 4));
    }

    @Override
    public String getStructureName() {
        return "UndergroundFortress";
    }

    // 供生物生成逻辑查询的结构刷怪列表（父类无此方法，非重写）
    public List<Biome.SpawnListEntry> getSpawnList() {
        return this.spawnList;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int regionX = chunkX >> 4;
        int regionZ = chunkZ >> 4;
        this.rand.setSeed((long) (regionX ^ regionZ << 4) ^ this.world.getSeed());
        this.rand.nextInt();

        if (this.rand.nextInt(3) != 0) {
            return false;
        } else if (chunkX != (regionX << 4) + 4 + this.rand.nextInt(8)) {
            return false;
        } else {
            return chunkZ == (regionZ << 4) + 4 + this.rand.nextInt(8);
        }
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MapGenUndergroundBridge.Start(this.world, this.rand, chunkX, chunkZ);
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        // 以当前位置为中心按螺旋环向外搜索
        for (int ring = 0; ring <= SEARCH_RADIUS_CHUNKS; ++ring) {
            for (int dx = -ring; dx <= ring; ++dx) {
                boolean onRingEdgeX = dx == -ring || dx == ring;

                for (int dz = -ring; dz <= ring; ++dz) {
                    boolean onRingEdgeZ = dz == -ring || dz == ring;

                    if (onRingEdgeX || onRingEdgeZ) {
                        int candidateX = chunkX + dx;
                        int candidateZ = chunkZ + dz;

                        if (this.canSpawnStructureAtCoords(candidateX, candidateZ) && (!findUnexplored || !worldIn.isChunkGeneratedAt(candidateX, candidateZ))) {
                            return new BlockPos((candidateX << 4) + 8, 64, (candidateZ << 4) + 8);
                        }
                    }
                }
            }
        }

        return null;
    }

    public static class Start extends StructureStart {
        public Start() {
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            StructureUndergroundBridgePieces.Start fortressStart = new StructureUndergroundBridgePieces.Start(random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.components.add(fortressStart);
            fortressStart.buildComponent(fortressStart, this.components, random);
            List<StructureComponent> pending = fortressStart.pendingChildren;

            while (!pending.isEmpty()) {
                int i = random.nextInt(pending.size());
                StructureComponent component = pending.remove(i);
                component.buildComponent(fortressStart, this.components, random);
            }

            this.updateBoundingBox();
            this.setRandomHeight(worldIn, random, 80, 160);
        }
    }
}
