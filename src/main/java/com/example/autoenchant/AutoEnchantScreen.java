package com.example.autoenchant;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AutoEnchantScreen extends Screen {

    private ButtonWidget toggleButton;

    protected AutoEnchantScreen() {
        super(Text.literal("Auto Enchant"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        toggleButton = ButtonWidget.builder(
                buttonLabel(),
                button -> {
                    AutoEnchantMod.enabled = !AutoEnchantMod.enabled;
                    button.setMessage(buttonLabel());
                }
        ).dimensions(centerX - 75, centerY - 10, 150, 20).build();

        this.addDrawableChild(toggleButton);
    }

    private Text buttonLabel() {
        return Text.literal("Auto Enchant: " + (AutoEnchantMod.enabled ? "BAT" : "TAT"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                this.height / 2 - 30,
                0xFFFFFF
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
