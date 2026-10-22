package meowmel.pollution.common.metatileentity.multiblock.bot;

/** Horizontal layers, bottom to top. Kept separate so geometry can be checked without mod bootstrap. */
public final class DragonIncubatorShape {
    public static final int WIDTH = 11;
    public static final int HEIGHT = 7;
    public static final int CONTROLLER_X = 5;
    public static final int CONTROLLER_Y = 0;
    public static final int CONTROLLER_Z = 9;
    public static final int EGG_X = 5;
    public static final int EGG_Y = 4;
    public static final int EGG_Z = 5;

    private static final String[][] LAYERS = {
        {
            "           ",
            "   CCHCC   ",
            "  CCCCCCC  ",
            " CCCCCCCCC ",
            " CCCCCCCCC ",
            " CCCCCCCCC ",
            " CCCCCCCCC ",
            " CCCCCCCCC ",
            "  CCCCCCC  ",
            "   CCSCC   ",
            "           "
        },
        {
            "           ",
            "           ",
            "     L     ",
            "     L     ",
            "    WWW    ",
            "  LLWWWLL  ",
            "    WWW    ",
            "     L     ",
            "     L     ",
            "           ",
            "           "
        },
        {
            "           ",
            "           ",
            "           ",
            "           ",
            "    WWW    ",
            "    WWW    ",
            "    WWW    ",
            "           ",
            "           ",
            "           ",
            "           "
        },
        {
            "           ",
            "           ",
            "           ",
            "   PCPCP   ",
            "   CCCCC   ",
            "   PCKCP   ",
            "   CCCCC   ",
            "   PCPCP   ",
            "           ",
            "           ",
            "           "
        },
        {
            "           ",
            "           ",
            "    CPC    ",
            "   PC CP   ",
            "  CC###CC  ",
            "  P #E# P  ",
            "  CC###CC  ",
            "   PC CP   ",
            "    CPC    ",
            "           ",
            "           "
        },
        {
            "           ",
            "    CPC    ",
            "  PC   CP  ",
            "  C     C  ",
            " C  ###  C ",
            " P  ###  P ",
            " C  ###  C ",
            "  C     C  ",
            "  PC   CP  ",
            "    CPC    ",
            "           "
        },
        {
            "    CPC    ",
            " PC     CP ",
            " C       C ",
            "           ",
            "C   ###   C",
            "P   ###   P",
            "C   ###   C",
            "           ",
            " C       C ",
            " PC     CP ",
            "    CPC    "
        }
    };

    private DragonIncubatorShape() {
    }

    /** Converts horizontal layers to the builder's [aisle][row][char] order. */
    public static String[][] createAisles() {
        String[][] aisles = new String[WIDTH][HEIGHT];
        for (int z = 0; z < WIDTH; z++) {
            for (int y = 0; y < HEIGHT; y++) {
                aisles[z][y] = LAYERS[y][z];
            }
        }
        return aisles;
    }
}

