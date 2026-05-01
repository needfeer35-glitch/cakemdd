package com.cakeauto.client;

import com.cakeauto.CakeAutoPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class CakeAutoClient implements ClientModInitializer {
    public static boolean autoEnabled = false;

    @Override
    public void onInitializeClient() {
        // Register client-side receiver for open GUI packet
        ClientPlayNetworking.registerGlobalReceiver(CakeAutoPackets.OpenGuiPayload.ID,
            (payload, context) -> context.client().execute(() ->
                context.client().setScreen(new CakeAutoScreen())
            )
        );
    }

    public static void sendToggle(boolean enabled) {
        autoEnabled = enabled;
        ClientPlayNetworking.send(new CakeAutoPackets.ToggleAutoPayload(enabled));
    }
}
