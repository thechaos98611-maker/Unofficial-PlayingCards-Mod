package com.ombremoon.playingcards.main;

import com.ombremoon.playingcards.init.InitEntityTypes;
import com.ombremoon.playingcards.render.RenderEntityCard;
import com.ombremoon.playingcards.render.RenderEntityCardDeck;
import com.ombremoon.playingcards.render.RenderEntityDice;
import com.ombremoon.playingcards.render.RenderEntityPokerChip;
import com.ombremoon.playingcards.render.RenderEntitySeat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class PlayingCardsClient {

    private PlayingCardsClient() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(PlayingCardsClient::registerRenderers);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(InitEntityTypes.CARD.get(), RenderEntityCard::new);
        event.registerEntityRenderer(InitEntityTypes.CARD_DECK.get(), RenderEntityCardDeck::new);
        event.registerEntityRenderer(InitEntityTypes.POKER_CHIP.get(), RenderEntityPokerChip::new);
        event.registerEntityRenderer(InitEntityTypes.DICE.get(), RenderEntityDice::new);
        event.registerEntityRenderer(InitEntityTypes.SEAT.get(), RenderEntitySeat::new);
    }
}
