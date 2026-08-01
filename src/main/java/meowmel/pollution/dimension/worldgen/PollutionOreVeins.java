package meowmel.pollution.dimension.worldgen;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Installs Pollution's Alfheim veins before GTCEu initializes its worldgen registry. */
public final class PollutionOreVeins {

    public static final int ALFHEIM_DIMENSION_ID = 43;

    private static final String[] VEIN_FILES = {
            "elementium_vein.json",
            "dragonstone_vein.json",
            "pixie_quartz_vein.json"
    };

    private PollutionOreVeins() {}

    public static void init(Path configDirectory) throws IOException {
        Path targetDirectory = configDirectory.resolve("gregtech/worldgen/vein/pollution/alfheim");
        Files.createDirectories(targetDirectory);

        for (String fileName : VEIN_FILES) {
            Path target = targetDirectory.resolve(fileName);
            if (Files.exists(target)) continue;

            String resource = "/assets/pollution/worldgen/vein/alfheim/" + fileName;
            try (InputStream input = PollutionOreVeins.class.getResourceAsStream(resource)) {
                if (input == null) throw new IOException("Missing bundled ore vein definition " + resource);
                Files.copy(input, target);
            }
        }
    }
}
