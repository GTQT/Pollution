package meowmel.pollution.dimension.worldgen.mapgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import java.util.Random;

/**
 * 地下世界洞穴生成器：在区块中开凿隧道与洞室（原版洞穴算法的地下世界变体）。
 */
public class MapGenCavesUnderground extends MapGenBase {

    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();

    protected void addRoom(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double x, double y, double z) {
        this.addTunnel(seed, chunkX, chunkZ, primer, x, y, z, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void addTunnel(long seed, int chunkX, int chunkZ, ChunkPrimer primer,
                             double x, double y, double z,
                             float tunnelSize, float yaw, float pitch,
                             int currentStep, int stepCount, double yScale) {
        double chunkCenterX = chunkX * 16 + 8;
        double chunkCenterZ = chunkZ * 16 + 8;
        float yawDrift = 0.0F;
        float pitchDrift = 0.0F;
        Random random = new Random(seed);

        if (stepCount <= 0) {
            int maxRange = this.range * 16 - 16;
            stepCount = maxRange - random.nextInt(maxRange / 4);
        }

        boolean isRoomBranch = false;

        if (currentStep == -1) {
            currentStep = stepCount / 2;
            isRoomBranch = true;
        }

        int branchStep = random.nextInt(stepCount / 2) + stepCount / 4;

        for (boolean smoothCurve = random.nextInt(6) == 0; currentStep < stepCount; ++currentStep) {
            double tunnelRadius = 1.5D + (double) (MathHelper.sin((float) currentStep * (float) Math.PI / (float) stepCount) * tunnelSize);
            double scaledRadius = tunnelRadius * yScale;
            float horizontalFactor = MathHelper.cos(pitch);
            float verticalFactor = MathHelper.sin(pitch);
            x += MathHelper.cos(yaw) * horizontalFactor;
            y += verticalFactor;
            z += MathHelper.sin(yaw) * horizontalFactor;

            if (smoothCurve) {
                pitch = pitch * 0.92F;
            } else {
                pitch = pitch * 0.7F;
            }

            pitch = pitch + pitchDrift * 0.1F;
            yaw += yawDrift * 0.1F;
            pitchDrift = pitchDrift * 0.9F;
            yawDrift = yawDrift * 0.75F;
            pitchDrift = pitchDrift + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawDrift = yawDrift + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            // 在分叉点向左右各分出一条支隧道
            if (!isRoomBranch && currentStep == branchStep && tunnelSize > 1.0F) {
                this.addTunnel(random.nextLong(), chunkX, chunkZ, primer, x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw - ((float) Math.PI / 2F), pitch / 3.0F, currentStep, stepCount, 1.0D);
                this.addTunnel(random.nextLong(), chunkX, chunkZ, primer, x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw + ((float) Math.PI / 2F), pitch / 3.0F, currentStep, stepCount, 1.0D);
                return;
            }

            if (isRoomBranch || random.nextInt(4) != 0) {
                double distToCenterX = x - chunkCenterX;
                double distToCenterZ = z - chunkCenterZ;
                double stepsLeft = stepCount - currentStep;
                double cutoffDist = tunnelSize + 2.0F + 16.0F;

                // 隧道已远离本区块影响范围，提前结束
                if (distToCenterX * distToCenterX + distToCenterZ * distToCenterZ - stepsLeft * stepsLeft > cutoffDist * cutoffDist) {
                    return;
                }

                if (x >= chunkCenterX - 16.0D - tunnelRadius * 2.0D && z >= chunkCenterZ - 16.0D - tunnelRadius * 2.0D && x <= chunkCenterX + 16.0D + tunnelRadius * 2.0D && z <= chunkCenterZ + 16.0D + tunnelRadius * 2.0D) {
                    int minX = MathHelper.floor(x - tunnelRadius) - chunkX * 16 - 1;
                    int maxX = MathHelper.floor(x + tunnelRadius) - chunkX * 16 + 1;
                    int minY = MathHelper.floor(y - scaledRadius) - 1;
                    int maxY = MathHelper.floor(y + scaledRadius) + 1;
                    int minZ = MathHelper.floor(z - tunnelRadius) - chunkZ * 16 - 1;
                    int maxZ = MathHelper.floor(z + tunnelRadius) - chunkZ * 16 + 1;

                    if (minX < 0) {
                        minX = 0;
                    }

                    if (maxX > 16) {
                        maxX = 16;
                    }

                    if (minY < 1) {
                        minY = 1;
                    }

                    if (maxY > 120) {
                        maxY = 120;
                    }

                    if (minZ < 0) {
                        minZ = 0;
                    }

                    if (maxZ > 16) {
                        maxZ = 16;
                    }

                    // 检查开凿范围内是否接触液体（接触则放弃本段，避免凿穿水面/岩浆）
                    boolean hitLiquid = false;

                    for (int scanX = minX; !hitLiquid && scanX < maxX; ++scanX) {
                        for (int scanZ = minZ; !hitLiquid && scanZ < maxZ; ++scanZ) {
                            for (int scanY = maxY + 1; !hitLiquid && scanY >= minY - 1; --scanY) {
                                if (scanY >= 0 && scanY < 128) {
                                    IBlockState scanState = primer.getBlockState(scanX, scanY, scanZ);

                                    if (scanState.getBlock() == Blocks.WATER || scanState.getBlock() == Blocks.LAVA) {
                                        hitLiquid = true;
                                    }

                                    if (scanY != minY - 1 && scanX != minX && scanX != maxX - 1 && scanZ != minZ && scanZ != maxZ - 1) {
                                        scanY = minY;
                                    }
                                }
                            }
                        }
                    }

                    if (!hitLiquid) {
                        for (int carveX = minX; carveX < maxX; ++carveX) {
                            double normX = ((double) (carveX + chunkX * 16) + 0.5D - x) / tunnelRadius;

                            for (int carveZ = minZ; carveZ < maxZ; ++carveZ) {
                                double normZ = ((double) (carveZ + chunkZ * 16) + 0.5D - z) / tunnelRadius;

                                for (int carveY = maxY; carveY > minY; --carveY) {
                                    double normY = ((double) (carveY - 1) + 0.5D - y) / scaledRadius;

                                    // 椭球截面内的可挖掘方块替换为空气
                                    if (normY > -0.7D && normX * normX + normY * normY + normZ * normZ < 1.0D) {
                                        IBlockState targetState = primer.getBlockState(carveX, carveY, carveZ);

                                        if (targetState.getBlock() == Blocks.STONE || targetState.getBlock() == Blocks.DIRT || targetState.getBlock() == Blocks.COBBLESTONE) {
                                            primer.setBlockState(carveX, carveY, carveZ, AIR);
                                        }
                                    }
                                }
                            }
                        }

                        if (isRoomBranch) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively called by generate()
     */
    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn) {
        int tunnelGroups = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);

        if (this.rand.nextInt(5) != 0) {
            tunnelGroups = 0;
        }

        for (int group = 0; group < tunnelGroups; ++group) {
            double startX = chunkX * 16 + this.rand.nextInt(16);
            double startY = this.rand.nextInt(128);
            double startZ = chunkZ * 16 + this.rand.nextInt(16);
            int tunnelCount = 1;

            if (this.rand.nextInt(4) == 0) {
                this.addRoom(this.rand.nextLong(), originalX, originalZ, chunkPrimerIn, startX, startY, startZ);
                tunnelCount += this.rand.nextInt(4);
            }

            for (int tunnel = 0; tunnel < tunnelCount; ++tunnel) {
                float yaw = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float tunnelSize = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                this.addTunnel(this.rand.nextLong(), originalX, originalZ, chunkPrimerIn, startX, startY, startZ, tunnelSize * 2.0F, yaw, pitch, 0, 0, 0.5D);
            }
        }
    }
}
