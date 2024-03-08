package xyz.bluspring.debugcullboxes.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class DebugCullBoxesClient implements ClientModInitializer {
    public static boolean renderCullboxes = false;
    public static final KeyMapping RENDER_CULLBOX = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.debugcullboxes.toggle_cullbox", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F6, "category.debugcullboxes"));

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (RENDER_CULLBOX.consumeClick() && client.level != null && client.player != null) {
                renderCullboxes = !renderCullboxes;
                client.gui.getChat().addMessage(
                    Component.empty()
                        .append(Component.translatable("debug.prefix").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                        .append(CommonComponents.SPACE)
                        .append("Cullboxes: " + (renderCullboxes ? "shown" : "hidden"))
                );
            }
        });
    }
}
