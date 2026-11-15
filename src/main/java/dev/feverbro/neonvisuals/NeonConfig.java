package dev.feverbro.neonvisuals;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class NeonConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("neonvisuals.properties");
    public static boolean hudEnabled=true, hitParticles=true, criticalEffects=true, hitmarker=true,
            comboCounter=true, damageNumbers=true, killEffects=true, targetHud=true, armorHud=true,
            potionHud=true, keystrokes=true, coordinates=true, cpsCounter=true, crosshairAccent=false,
            fullbright=false, extendedFog=false, playerTrail=false, jumpRings=true, ambientParticles=false;
    public static String hitParticleType="Crit", hitParticleShape="Burst";

    private NeonConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        Properties p=new Properties();
        try (Reader r=Files.newBufferedReader(FILE)) {
            p.load(r);
            hudEnabled=b(p,"hudEnabled",hudEnabled); hitParticles=b(p,"hitParticles",hitParticles);
            criticalEffects=b(p,"criticalEffects",criticalEffects); hitmarker=b(p,"hitmarker",hitmarker);
            comboCounter=b(p,"comboCounter",comboCounter); damageNumbers=b(p,"damageNumbers",damageNumbers);
            killEffects=b(p,"killEffects",killEffects); targetHud=b(p,"targetHud",targetHud);
            armorHud=b(p,"armorHud",armorHud); potionHud=b(p,"potionHud",potionHud);
            keystrokes=b(p,"keystrokes",keystrokes); coordinates=b(p,"coordinates",coordinates);
            cpsCounter=b(p,"cpsCounter",cpsCounter); crosshairAccent=b(p,"crosshairAccent",crosshairAccent);
            fullbright=b(p,"fullbright",fullbright); extendedFog=b(p,"extendedFog",extendedFog);
            playerTrail=b(p,"playerTrail",playerTrail); jumpRings=b(p,"jumpRings",jumpRings);
            ambientParticles=b(p,"ambientParticles",ambientParticles);
            hitParticleType=p.getProperty("hitParticleType","Crit");
            hitParticleShape=p.getProperty("hitParticleShape","Burst");
            if (!hitParticleType.matches("Crit|Stars|Hearts|Enchanted")) hitParticleType="Crit";
            if (!hitParticleShape.matches("Burst|Ring|Upward")) hitParticleShape="Burst";
        } catch (IOException e) { NeonVisualsClient.LOGGER.error("Cannot load NeonVisuals settings",e); }
    }

    private static boolean b(Properties p,String key,boolean d) { return Boolean.parseBoolean(p.getProperty(key,Boolean.toString(d))); }

    public static void save() {
        Properties p=new Properties();
        p.setProperty("hudEnabled",Boolean.toString(hudEnabled));
        p.setProperty("hitParticles",Boolean.toString(hitParticles));
        p.setProperty("criticalEffects",Boolean.toString(criticalEffects));
        p.setProperty("hitmarker",Boolean.toString(hitmarker));
        p.setProperty("comboCounter",Boolean.toString(comboCounter));
        p.setProperty("damageNumbers",Boolean.toString(damageNumbers));
        p.setProperty("killEffects",Boolean.toString(killEffects));
        p.setProperty("targetHud",Boolean.toString(targetHud));
        p.setProperty("armorHud",Boolean.toString(armorHud));
        p.setProperty("potionHud",Boolean.toString(potionHud));
        p.setProperty("keystrokes",Boolean.toString(keystrokes));
        p.setProperty("coordinates",Boolean.toString(coordinates));
        p.setProperty("cpsCounter",Boolean.toString(cpsCounter));
        p.setProperty("crosshairAccent",Boolean.toString(crosshairAccent));
        p.setProperty("fullbright",Boolean.toString(fullbright));
        p.setProperty("extendedFog",Boolean.toString(extendedFog));
        p.setProperty("playerTrail",Boolean.toString(playerTrail));
        p.setProperty("jumpRings",Boolean.toString(jumpRings));
        p.setProperty("ambientParticles",Boolean.toString(ambientParticles));
        p.setProperty("hitParticleType",hitParticleType);
        p.setProperty("hitParticleShape",hitParticleShape);
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w=Files.newBufferedWriter(FILE)) { p.store(w,"NeonVisuals client settings"); }
        } catch (IOException e) { NeonVisualsClient.LOGGER.error("Cannot save NeonVisuals settings",e); }
    }
}
