package com.cakeauto.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.item.Items;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class CakeAutoScreen extends Screen {
    private boolean autoEnabled = CakeAutoClient.autoEnabled;

    public CakeAutoScreen() {
        super(Text.literal("CakeAuto"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        CheckboxWidget checkbox = CheckboxWidget.builder(
                Text.literal("Автоматически заполнять рецепт торта"),
                this.textRenderer)
            .pos(cx - 120, cy - 10)
            .checked(autoEnabled)
            .callback((cb, checked) -> {
                autoEnabled = checked;
                CakeAutoClient.sendToggle(checked);
            })
            .build();
        this.addDrawableChild(checkbox);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Открыть верстак с тортом"),
                btn -> {
                    this.close();
                    if (this.client != null && this.client.player != null) {
                        this.client.player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory(
                                (syncId, inv, player) -> new CraftingScreenHandler(syncId, inv),
                                Text.literal("Создание торта")
                            )
                        );
                        if (autoEnabled) {
                            new Thread(() -> {
                                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                                net.minecraft.client.MinecraftClient.getInstance().execute(this::fillCakeRecipe);
                            }).start();
                        }
                    }
                })
            .dimensions(cx - 100, cy + 30, 200, 20)
            .build()
        );

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"), btn -> this.close())
            .dimensions(cx - 40, cy + 60, 80, 20)
            .build()
        );
    }

    private void fillCakeRecipe() {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null || !(client.currentScreen instanceof CraftingScreen craftingScreen)) return;

        var handler = craftingScreen.getScreenHandler();
        var recipe = new net.minecraft.item.Item[]{
            Items.MILK_BUCKET, Items.MILK_BUCKET, Items.MILK_BUCKET,
            Items.SUGAR, Items.EGG, Items.SUGAR,
            Items.WHEAT, Items.WHEAT, Items.WHEAT
        };

        for (int i = 0; i < 9; i++) {
            if (!handler.slots.get(i + 1).getStack().isEmpty()) continue;
            var needed = recipe[i];
            for (int invIdx = 0; invIdx < client.player.getInventory().size(); invIdx++) {
                var stack = client.player.getInventory().getStack(invIdx);
                if (!stack.isEmpty() && stack.isOf(needed)) {
                    int guiInvSlot = handler.slots.size() - client.player.getInventory().size() + invIdx;
                    if (guiInvSlot < 0) continue;
                    client.interactionManager.clickSlot(handler.syncId, guiInvSlot, 0, SlotActionType.PICKUP, client.player);
                    client.interactionManager.clickSlot(handler.syncId, i + 1, 0, SlotActionType.PICKUP, client.player);
                    break;
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int cx = this.width / 2, cy = this.height / 2;
        context.fill(cx - 130, cy - 40, cx + 130, cy + 90, 0xCC1a1a2e);
        context.drawBorder(cx - 130, cy - 40, 260, 130, 0xFFf0a500);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§6🎂 CakeAuto"), cx, cy - 30, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7молоко×3, сахар×2, яйцо×1, пшеница×3"), cx, cy - 18, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
