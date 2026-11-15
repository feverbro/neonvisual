package dev.feverbro.neonvisuals;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class NeonConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("neonvisuals.properties");
    public static boolean hudEnabled = true;
    public static boolean fogEnabled = false;
    public static boolean particlesEnabled = true;
    public static boolean glintProtection = true;

    private NeonConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE)) {
            p.load(reader);
            hudEnabled = Boolean.parseBoolean(p.getProperty("hud", "true"));
            fogEnabled = Boolean.parseBoolean(p.getProperty("fog", "false"));
            particlesEnabled = Boolean.parseBoolean(p.getProperty("particles", "true"));
            glintProtection = Boolean.parseBoolean(p.getProperty("glintProtection", "true"));
        } catch (IOException e) {
            NeonVisualsClient.LOGGER.error("Could not read NeonVisuals config", e);
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("hud", Boolean.toString(hudEnabled));
        p.setProperty("fog", Boolean.toString(fogEnabled));
        p.setProperty("particles", Boolean.toString(particlesEnabled));
        p.setProperty("glintProtection", Boolean.toString(glintProtection));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) { p.store(writer, "NeonVisuals client settings"); }
        } catch (IOException e) {
            NeonVisualsClient.LOGGER.error("Could not save NeonVisuals config", e);
        }
    }
}
