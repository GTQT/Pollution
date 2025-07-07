package keqing.pollution.dimension.worldgen.mapGen;

import com.google.common.collect.Lists;
import keqing.pollution.dimension.worldgen.structure.StructureBTNBridgePieces;
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

public class MapGenBTNBridge extends MapGenStructure {
    private final List<Biome.SpawnListEntry> spawnList = Lists.newArrayList();

    public MapGenBTNBridge() {
        this.spawnList.add(new Biome.SpawnListEntry(EntityZombie.class, 8, 5, 5));
        this.spawnList.add(new Biome.SpawnListEntry(EntityShulker.class, 2, 5, 5));
        this.spawnList.add(new Biome.SpawnListEntry(EntityCreeper.class, 3, 4, 4));
    }

    public String getStructureName() {
        return "BTNFortress";
    }

    public List<Biome.SpawnListEntry> getSpawnList() {
        return this.spawnList;
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i = chunkX >> 4;
        int j = chunkZ >> 4;
        this.rand.setSeed((long) (i ^ j << 4) ^ this.world.getSeed());
        this.rand.nextInt();

        if (this.rand.nextInt(3) != 0) {
            return false;
        } else if (chunkX != (i << 4) + 4 + this.rand.nextInt(8)) {
            return false;
        } else {
            return chunkZ == (j << 4) + 4 + this.rand.nextInt(8);
        }
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MapGenBTNBridge.Start(this.world, this.rand, chunkX, chunkZ);
    }

    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        int i = 1000;
        int j = pos.getX() >> 4;
        int k = pos.getZ() >> 4;

        for (int l = 0; l <= 1000; ++l) {
            for (int i1 = -l; i1 <= l; ++i1) {
                boolean flag = i1 == -l || i1 == l;

                for (int j1 = -l; j1 <= l; ++j1) {
                    boolean flag1 = j1 == -l || j1 == l;

                    if (flag || flag1) {
                        int k1 = j + i1;
                        int l1 = k + j1;

                        if (this.canSpawnStructureAtCoords(k1, l1) && (!findUnexplored || !worldIn.isChunkGeneratedAt(k1, l1))) {
                            return new BlockPos((k1 << 4) + 8, 64, (l1 << 4) + 8);
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
            StructureBTNBridgePieces.Start structureBTNbridgepieces$start = new StructureBTNBridgePieces.Start(random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.components.add(structureBTNbridgepieces$start);
            structureBTNbridgepieces$start.buildComponent(structureBTNbridgepieces$start, this.components, random);
            List<StructureComponent> list = structureBTNbridgepieces$start.pendingChildren;

            while (!list.isEmpty()) {
                int i = random.nextInt(list.size());
                StructureComponent structurecomponent = list.remove(i);
                structurecomponent.buildComponent(structureBTNbridgepieces$start, this.components, random);
            }

            this.updateBoundingBox();
            this.setRandomHeight(worldIn, random, 80, 160);
        }
    }
}