package com.example.autoenchant;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * GUI: 1 nut bat/tat + 1 nut doi phim tat ngay trong game.
 * Viec bat phim/chuot moi dung GLFW polling trong tick(), tranh phai
 * phu thuoc vao chu ky keyPressed/mouseClicked hay thay doi giua cac
 * ban Minecraft.
 */
public class AutoEnchantScreen extends Screen {

    private ButtonWidget toggleButton;
    private ButtonWidget rebindButton;
    private boolean listeningForKey = false;

    protected AutoEnchantScreen() {
        super(Text.literal("Auto Enchant"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        toggleButton = ButtonWidget.builder(
                toggleLabel(),
                button -> {
                    AutoEnchantMod.enabled = !AutoEnchantMod.enabled;
                    button.setMessage(toggleLabel());
                }
        ).dimensions(centerX - 75, centerY - 30, 150, 20).build();

        rebindButton = ButtonWidget.builder(
                rebindLabel(),
                button -> {
                    listeningForKey = true;
                    button.setMessage(Text.literal("> Bam phim moi (ESC de huy) <"));
                }
        ).dimensions(centerX - 75, centerY, 150, 20).build();

        this.addDrawableChild(toggleButton);
        this.addDrawableChild(rebindButton);
    }

    private Text toggleLabel() {
        return Text.literal("Auto Enchant: " + (AutoEnchantMod.enabled ? "BAT" : "TAT"));
    }

    private Text rebindLabel() {
        String keyName = AutoEnchantMod.toggleKey.getBoundKeyLocalizedText().getString();
        return Text.literal("Phim bat/tat: " + keyName);
    }

    @Override
    public void tick() {
        super.tick();
        if (!listeningForKey) {
            return;
        }

        long handle = MinecraftClient.getInstance().getWindow().getHandle();

        if (GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            listeningForKey = false;
            rebindButton.setMessage(rebindLabel());
            return;
        }

        for (int button = 0; button <= 7; button++) {
            if (GLFW.glfwGetMouseButton(handle, button) == GLFW.GLFW_PRESS) {
                bindKey(InputUtil.Type.MOUSE.createFromCode(button));
                return;
            }
        }

        for (int key = 32; key <= 348; key++) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                continue;
            }
            if (GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS) {
                bindKey(InputUtil.Type.KEYSYM.createFromCode(key));
                return;
            }
        }
    }

    private void bindKey(InputUtil.Key key) {
        AutoEnchantMod.toggleKey.setBoundKey(key);
        MinecraftClient.getInstance().options.write();
        listeningForKey = false;
        rebindButton.setMessage(rebindLabel());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                this.height / 2 - 55,
                0xFFFFFF
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
