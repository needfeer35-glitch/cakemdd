package com.cakeauto;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CakeAutoPackets {

    public record OpenGuiPayload() implements CustomPayload {
        public static final Id<OpenGuiPayload> ID = new Id<>(Identifier.of("cakeauto", "open_gui"));
        public static final PacketCodec<RegistryByteBuf, OpenGuiPayload> CODEC =
            PacketCodec.unit(new OpenGuiPayload());
        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public record ToggleAutoPayload(boolean enabled) implements CustomPayload {
        public static final Id<ToggleAutoPayload> ID = new Id<>(Identifier.of("cakeauto", "toggle_auto"));
        public static final PacketCodec<RegistryByteBuf, ToggleAutoPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, ToggleAutoPayload::enabled, ToggleAutoPayload::new);
        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public static void sendOpenGuiPacket(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new OpenGuiPayload());
    }
}
