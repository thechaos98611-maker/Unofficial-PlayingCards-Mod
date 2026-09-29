package com.ombremoon.playingcards.network;

import com.ombremoon.playingcards.item.ItemCardCovered;
import com.ombremoon.playingcards.main.PCReference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketInteractCard(String command) implements CustomPacketPayload {

    public static final Type<PacketInteractCard> TYPE = new Type<>(Identifier.fromNamespaceAndPath(PCReference.MOD_ID, "main"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketInteractCard> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeUtf(payload.command(), 11),
            buffer -> new PacketInteractCard(buffer.readUtf(11).trim()));

    @Override
    public Type<PacketInteractCard> type() {
        return TYPE;
    }

    public static void handle(PacketInteractCard packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && packet.command().equalsIgnoreCase("flipinv")) {
                if (player.getMainHandItem().getItem() instanceof ItemCardCovered card) {
                    card.flipCard(player.getMainHandItem(), player);
                }
            }
        });
    }
}
