package com.example.autoenchant;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AutoEnchantMod implements ClientModInitializer {

    public static boolean enabled = false;
    public static KeyBinding openGuiKey;
    public static KeyBinding toggleKey;

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("autoenchant", "main"));

    private enum State { IDLE, WAIT_BEFORE_CLICK, WAIT_GUI_OPEN }
    private State state = State.IDLE;
    private int tickCounter = 0;

    private static final int TICKS_BEFORE_CLICK = 22;
    private static final int TICKS_WAIT_GUI = 10;
    private static final int TARGET_SLOT = 13;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoenchant.open_gui",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY
        ));

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoenchant.toggle",
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        while (openGuiKey.wasPressed()) {
            client.setScreen(new AutoEnchantScreen());
        }

        while (toggleKey.wasPressed()) {
            enabled = !enabled;
            if (client.player != null) {
                client.player.sendMessage(
                        Text.literal("[AutoEnchant] " + (enabled ? "Da BAT" : "Da TAT")),
                        true
                );
            }
        }

        if (!enabled || client.player == null) {
            state = State.IDLE;
            return;
        }

        switch (state) {
            case IDLE -> {
                if (client.player.experienceLevel >= 60) {
                    state = State.WAIT_BEFORE_CLICK;
                    tickCounter = TICKS_BEFORE_CLICK;
                }
            }
            case WAIT_BEFORE_CLICK -> {
                tickCounter--;
                if (tickCounter <= 0) {
                    simulateLeftClick();
                    client.player.networkHandler.sendChatCommand("enchant");

                    state = State.WAIT_GUI_OPEN;
                    tickCounter = TICKS_WAIT_GUI;
                }
            }
            case WAIT_GUI_OPEN -> {
                tickCounter--;
                if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
                    clickTargetSlotAndClose(client, handledScreen);
                    state = State.IDLE;
                } else if (tickCounter <= 0) {
                    state = State.IDLE;
                }
            }
        }
    }

    /**
     * Gia lap 1 lan bam chuot trai that su (khong chi vung tay).
     * Dua vao co che noi bo cua Minecraft: setKeyPressed danh dau nut duoc
     * bam, roi client tu xu ly y het nhu khi nguoi choi tu bam chuot
     * (tan cong / dao / tuong tac tuy vao thu dang nhin vao).
     */
    private void simulateLeftClick() {
        InputUtil.Key leftMouse = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        KeyBinding.setKeyPressed(leftMouse, true);
        KeyBinding.setKeyPressed(leftMouse, false);
    }

    private void clickTargetSlotAndClose(MinecraftClient client, HandledScreen<?> screen) {
        ScreenHandler handler = screen.getScreenHandler();
        if (client.interactionManager != null && client.player != null) {
            client.interactionManager.clickSlot(
                    handler.syncId,
                    TARGET_SLOT,
                    0,
                    SlotActionType.PICKUP,
                    client.player
            );
        }
        client.player.closeHandledScreen();
        client.setScreen(null);
    }
}
