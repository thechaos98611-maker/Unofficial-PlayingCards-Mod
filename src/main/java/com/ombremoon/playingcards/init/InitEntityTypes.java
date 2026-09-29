package com.ombremoon.playingcards.init;

import com.ombremoon.playingcards.entity.*;
import com.ombremoon.playingcards.main.CommonClass;
import com.ombremoon.playingcards.main.PCReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class InitEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, PCReference.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<EntityCard>> CARD = ENTITY_TYPES.register("card", () -> EntityType.Builder.<EntityCard>of(EntityCard::new, MobCategory.MISC).sized(0.5F, 0.5F).build(ResourceKey.create(Registries.ENTITY_TYPE, CommonClass.customLocation("card"))));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCardDeck>> CARD_DECK = ENTITY_TYPES.register("card_deck", () -> EntityType.Builder.<EntityCardDeck>of(EntityCardDeck::new, MobCategory.MISC).sized(0.5F, 0.5F).build(ResourceKey.create(Registries.ENTITY_TYPE, CommonClass.customLocation("card_deck"))));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPokerChip>> POKER_CHIP = ENTITY_TYPES.register("poker_chip", () -> EntityType.Builder.<EntityPokerChip>of(EntityPokerChip::new, MobCategory.MISC).sized(0.3F, 0.3F).build(ResourceKey.create(Registries.ENTITY_TYPE, CommonClass.customLocation("poker_chip"))));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDice>> DICE = ENTITY_TYPES.register("dice", () -> EntityType.Builder.<EntityDice>of(EntityDice::new, MobCategory.MISC).sized(0.3F, 0.3F).build(ResourceKey.create(Registries.ENTITY_TYPE, CommonClass.customLocation("dice"))));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySeat>> SEAT = ENTITY_TYPES.register("seat", () -> EntityType.Builder.<EntitySeat>of(EntitySeat::new, MobCategory.MISC).sized(0, 0).build(ResourceKey.create(Registries.ENTITY_TYPE, CommonClass.customLocation("seat"))));

    public static void init(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }


}
