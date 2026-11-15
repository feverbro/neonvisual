package dev.feverbro.neonvisuals;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;

public final class NeonVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "neonvisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        NeonConfig.load();
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.neonvisuals.open", GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen == null) client.setScreen(new NeonSettingsScreen(null));
            }
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "status_hud"),
                NeonVisualsClient::renderHud);
        LOGGER.info("NeonVisuals initialized");
    }

    private static void renderHud(GuiGraphics graphics, DeltaTracker tickCounter) {
        if (!NeonConfig.hudEnabled) return;
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null || client.level == null) return;
        int x = 8, y = 8;
        graphics.fill(x - 4, y - 4, x + 116, y + 39, 0x99090E18);
        graphics.fill(x - 4, y - 4, x - 2, y + 39, 0xFF55E7FF);
        graphics.drawString(client.font, "NEON VISUALS", x + 3, y, 0xFF55E7FF, true);
        graphics.drawString(client.font, "XYZ  " + (int) player.getX() + " / " + (int) player.getY() + " / " + (int) player.getZ(), x + 3, y + 12, 0xFFE7F5FF, true);
        graphics.drawString(client.font, "FPS  " + client.getFps(), x + 3, y + 24, 0xFFB6C8D8, true);
    }
}
