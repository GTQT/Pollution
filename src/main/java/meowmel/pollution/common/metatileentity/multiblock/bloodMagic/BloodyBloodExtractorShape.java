package meowmel.pollution.common.metatileentity.multiblock.bloodMagic;

import net.minecraft.util.math.BlockPos;

/** Coordinates are relative to the needle tip; the controller is (0, 3, -2). */
public final class BloodyBloodExtractorShape {
    public static final int WIDTH = 7, HEIGHT = 13;
    private static final String[] NEEDLE = {"#######", "#######", "#######", "###N###", "#######", "#######", "#######"};
    private static final String[] HUB = {"#######", "#######", "##HHH##", "##HNH##", "##HHH##", "#######", "#######"};
    private static final String[] BASE = {"#######", "##XSX##", "#XXXXX#", "#XXNXX#", "#XXXXX#", "##XXX##", "#######"};
    private static final String[] BARREL = {"#######", "##GGG##", "#G###G#", "DG#P#GD", "#G###G#", "##GGG##", "#######"};
    private static final String[] CAP = {"#######", "##XXX##", "#XXXXX#", "#XXPXX#", "#XXXXX#", "##XXX##", "#######"};
    private static final String[] SHAFT = {"#######", "#######", "#######", "###P###", "#######", "#######", "#######"};
    private static final String[] HANDLE = {"#######", "#######", "XXXXXXX", "XXXPXXX", "XXXXXXX", "#######", "#######"};

    private BloodyBloodExtractorShape() {}

    public static String[][] createAisles() {
        String[][] layers = {NEEDLE, NEEDLE, HUB, BASE, BARREL, BARREL, BARREL, BARREL, BARREL, CAP, SHAFT, SHAFT, HANDLE};
        String[][] result = new String[WIDTH][HEIGHT];
        for (int z = 0; z < WIDTH; z++) for (int y = 0; y < HEIGHT; y++) result[z][y] = layers[y][z];
        return result;
    }

    /** Slots 0..4 are left bottom to top, 5..9 are right bottom to top. */
    public static BlockPos slot(int slot) {
        if (slot < 0 || slot >= 10) throw new IllegalArgumentException("Invalid extractor slot: " + slot);
        return new BlockPos(slot < 5 ? -3 : 3, 4 + slot % 5, 0);
    }
}
