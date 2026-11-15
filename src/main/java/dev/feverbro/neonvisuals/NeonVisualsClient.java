package dev.feverbro.neonvisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NeonVisualsClient implements ClientModInitializer {
    public static final String MOD_ID="neonvisuals";
    public static final Logger LOGGER=LoggerFactory.getLogger(MOD_ID);

    @Override public void onInitializeClient() {
        NeonConfig.load();
        KeyMapping menu=KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.neonvisuals.open", GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menu.consumeClick()) if (client.screen==null) client.setScreen(new NeonSettingsScreen(null));
            VisualsManager.tick(client);
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(VisualsManager::restoreOptions);
        AttackEntityCallback.EVENT.register((player,world,hand,target,hit) -> {
            if (world.isClientSide() && player instanceof LocalPlayer local) VisualsManager.onAttack(local,target);
            return InteractionResult.PASS;
        });
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID,"visual_hud"), NeonHud::render);
        LOGGER.info("NeonVisuals 1.21.11 initialized");
    }
}
