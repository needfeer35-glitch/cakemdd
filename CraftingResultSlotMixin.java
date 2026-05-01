package com.cakeauto.mixin;

import com.cakeauto.client.CakeAutoClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Inject(method = "onTakeItem", at = @At("TAIL"))
    private void onCakeCrafted(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!stack.isOf(Items.CAKE)) return;
        if (!player.getWorld().isClient()) return;
        if (!CakeAutoClient.autoEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof CraftingScreen craftingScreen)) return;

        var handler = craftingScreen.getScreenHandler();
        var recipe = new net.minecraft.item.Item[]{
            Items.MILK_BUCKET, Items.MILK_BUCKET, Items.MILK_BUCKET,
            Items.SUGAR, Items.EGG, Items.SUGAR,
            Items.WHEAT, Items.WHEAT, Items.WHEAT
        };

        for (int i = 0; i < 9; i++) {
            if (!handler.slots.get(i + 1).getStack().isEmpty()) continue;
            var needed = recipe[i];
            for (int invIdx = 0; invIdx < player.getInventory().size(); invIdx++) {
                var invStack = player.getInventory().getStack(invIdx);
                if (!invStack.isEmpty() && invStack.isOf(needed)) {
                    int guiInvSlot = handler.slots.size() - player.getInventory().size() + invIdx;
                    if (guiInvSlot < 0) continue;
                    client.interactionManager.clickSlot(handler.syncId, guiInvSlot, 0, SlotActionType.PICKUP, client.player);
                    client.interactionManager.clickSlot(handler.syncId, i + 1, 0, SlotActionType.PICKUP, client.player);
                    break;
                }
            }
        }
    }
}
