package com.ombremoon.playingcards.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CardHelper {

    public static final String[] CARD_SKIN_NAMES = {"card.skin.blue", "card.skin.red", "card.skin.black", "card.skin.pig"};

    public static void renderItem(ItemStackRenderState itemState, double offsetX, double offsetY, double offsetZ,
                                  PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int seed) {
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);
        itemState.submit(poseStack, collector, packedLight, 0, seed);
        poseStack.popPose();
    }

    public static MutableComponent getCardName(int id) {
        String type = "card.ace";
        int typeID = id / 4 + 1;
        if (typeID > 1 && typeID < 11) type = "" + typeID;
        if (typeID > 10) {
            type = "card.jack";
            if (typeID > 11) {
                type = "card.queen";
                if (typeID > 12) type = "card.king";
            }
        }

        String suit = switch (id % 4) {
            case 1 -> "card.clubs";
            case 2 -> "card.diamonds";
            case 3 -> "card.hearts";
            default -> "card.spades";
        };
        return Component.translatable(type).append(" ").append(Component.translatable("card.of").append(" ").append(Component.translatable(suit)));
    }
}
