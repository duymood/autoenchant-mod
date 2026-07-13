package com.example.autoenchant;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

/**
 * GUI: 1 nut bat/tat + 1 nut doi phim tat ngay trong game,
 * khong can vao menu Controls cua Minecraft.
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
                    button.setMessage(Text.literal("> Bam phim moi <"));
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningForKey) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                listeningForKey = false;
                rebindButton.setMessage(rebindLabel());
                return true;
            }
            bindKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (listeningForKey) {
            bindKey(InputUtil.Type.MOUSE.createFromCode(button));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
