package com.ombremoon.playingcards.main;

import com.ombremoon.playingcards.network.ModNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(PCReference.MOD_ID)
public class PlayingCards {

    public PlayingCards(IEventBus modEventBus, ModContainer modContainer) {
        CommonClass.init(modEventBus);
        ModNetworking.register(modEventBus);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            PlayingCardsClient.register(modEventBus);
        }
    }
}
