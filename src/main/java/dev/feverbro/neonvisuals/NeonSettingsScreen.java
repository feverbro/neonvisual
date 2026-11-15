package dev.feverbro.neonvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class NeonSettingsScreen extends Screen {
    private final Screen parent;

    public NeonSettingsScreen(Screen parent) {
        super(Component.literal("NeonVisuals"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int top = height / 4;
        addRenderableWidget(Button.builder(toggleText("HUD", NeonConfig.hudEnabled), b -> {
            NeonConfig.hudEnabled = !NeonConfig.hudEnabled; NeonConfig.save();
            b.setMessage(toggleText("HUD", NeonConfig.hudEnabled));
        }).bounds(center - 100, top, 200, 20).build());
        addRenderableWidget(Button.builder(toggleText("Fog profile", NeonConfig.fogEnabled), b -> {
            NeonConfig.fogEnabled = !NeonConfig.fogEnabled; NeonConfig.save();
            b.setMessage(toggleText("Fog profile", NeonConfig.fogEnabled));
        }).bounds(center - 100, top + 26, 200, 20).build());
        addRenderableWidget(Button.builder(toggleText("Particles", NeonConfig.particlesEnabled), b -> {
            NeonConfig.particlesEnabled = !NeonConfig.particlesEnabled; NeonConfig.save();
            b.setMessage(toggleText("Particles", NeonConfig.particlesEnabled));
        }).bounds(center - 100, top + 52, 200, 20).build());
        addRenderableWidget(Button.builder(toggleText("Preserve enchantment glint", NeonConfig.glintProtection), b -> {
            NeonConfig.glintProtection = !NeonConfig.glintProtection; NeonConfig.save();
            b.setMessage(toggleText("Preserve enchantment glint", NeonConfig.glintProtection));
        }).bounds(center - 100, top + 78, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(center - 100, top + 112, 200, 20).build());
    }

    private static Component toggleText(String name, boolean enabled) {
        return Component.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, "NEON VISUALS", width / 2, height / 4 - 28, 0xFF55E7FF);
        graphics.drawCenteredString(font, "Right Shift - open settings", width / 2, height - 28, 0xFF9AAFC0);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
