package com.ombremoon.playingcards.main;

import com.ombremoon.playingcards.init.InitEntityTypes;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.init.InitRecipes;
import com.ombremoon.playingcards.init.InitTileEntityTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;

public class CommonClass {

    public static void init(IEventBus modEventBus) {
        InitItems.init(modEventBus);
        InitEntityTypes.init(modEventBus);
        InitTileEntityTypes.init(modEventBus);
        InitRecipes.init(modEventBus);
    }

    public static Identifier customLocation(String name) {
        return Identifier.fromNamespaceAndPath(PCReference.MOD_ID, name);
    }
}
