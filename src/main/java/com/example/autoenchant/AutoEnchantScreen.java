package com.example.autoenchant;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * GUI: 1 nut bat/tat + doi phim bat/tat.
 * Doi phim ban phim dung dung API chinh thuc keyPressed(KeyInput) cua
 * Minecraft (an toan tren moi nen tang, ke ca Android/Zalith Launcher).
 * Doi sang chuot thi bam thang nut "Gan Chuot Trai/Phai", khong can
 * "nghe" su kien click (tranh dung API Click moi hay thay doi).
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
        ).dimensions(centerX - 75, centerY - 50, 150, 20).build();

        rebindButton = ButtonWidget.builder(
                rebindLabel(),
                button -> {
                    listeningForKey = true;
                    button.setMessage(Text.literal("> Bam phim moi (ESC de huy) <"));
                }
        ).dimensions(centerX - 75, centerY - 20, 150, 20).build();

        ButtonWidget leftMouseButton = ButtonWidget.builder(
                Text.literal("Gan: Chuot Trai"),
                button -> bindKey(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        ).dimensions(centerX - 75, centerY + 10, 150, 20).build();

        ButtonWidget rightMouseButton = ButtonWidget.builder(
                Text.literal("Gan: Chuot Phai"),
                button -> bindKey(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
        ).dimensions(centerX - 75, centerY + 40, 150, 20).build();

        this.addDrawableChild(toggleButton);
        this.addDrawableChild(rebindButton);
        this.addDrawableChild(leftMouseButton);
        this.addDrawableChild(rightMouseButton);
    }

    private Text toggleLabel() {
        return Text.literal("Auto Enchant: " + (AutoEnchantMod.enabled ? "BAT" : "TAT"));
    }

    private Text rebindLabel() {
        String keyName = AutoEnchantMod.toggleKey.getBoundKeyLocalizedText().getString();
        return Text.literal("Phim bat/tat: " + keyName);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (listeningForKey) {
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
                listeningForKey = false;
                rebindButton.setMessage(rebindLabel());
                return true;
            }
            bindKey(InputUtil.Type.KEYSYM.createFromCode(input.key()));
            return true;
        }
        return super.keyPressed(input);
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
                this.height / 2 - 75,
                0xFFFFFF
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
