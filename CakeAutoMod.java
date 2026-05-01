package com.cakeauto;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CakeAutoMod implements ModInitializer {
    public static final String MOD_ID = "cakeauto";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CakeAuto loaded!");

        // Register payload types
        PayloadTypeRegistry.playS2C().register(CakeAutoPackets.OpenGuiPayload.ID, CakeAutoPackets.OpenGuiPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CakeAutoPackets.ToggleAutoPayload.ID, CakeAutoPackets.ToggleAutoPayload.CODEC);

        // Register server-side packet receiver
        ServerPlayNetworking.registerGlobalReceiver(CakeAutoPackets.ToggleAutoPayload.ID,
            (payload, context) -> {
                context.server().execute(() -> {
                    CakeAutoState state = CakeAutoState.get(context.player());
                    state.setAutoEnabled(payload.enabled());
                    context.player().sendMessage(
                        net.minecraft.text.Text.literal(
                            payload.enabled()
                                ? "§a[CakeAuto] §fАвто-торт §aвключён!"
                                : "§a[CakeAuto] §fАвто-торт §cвыключен."
                        ), false
                    );
                });
            });

        // Register /tort command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            CakeCommand.register(dispatcher));
    }
}
