package com.ombremoon.playingcards.event;

import com.ombremoon.playingcards.item.ItemCardCovered;
import com.ombremoon.playingcards.network.PacketInteractCard;
import com.ombremoon.playingcards.main.PCReference;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = PCReference.MOD_ID, value = Dist.CLIENT)
public final class CardInteractEvent {

    private CardInteractEvent() {}

    @SubscribeEvent
    public static void onLeftClick(InputEvent.MouseButton.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null && event.getAction() == GLFW.GLFW_PRESS && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && minecraft.player != null && minecraft.player.getMainHandItem().getItem() instanceof ItemCardCovered) {
            ClientPacketDistributor.sendToServer(new PacketInteractCard("flipinv"));
            event.setCanceled(true);
        }
    }
}
