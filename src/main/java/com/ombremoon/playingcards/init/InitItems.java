package com.ombremoon.playingcards.init;

import com.ombremoon.playingcards.block.BlockBarStool;
import com.ombremoon.playingcards.block.BlockPokerTable;
import com.ombremoon.playingcards.item.ItemCard;
import com.ombremoon.playingcards.item.ItemCardCovered;
import com.ombremoon.playingcards.item.ItemCardDeck;
import com.ombremoon.playingcards.item.ItemPokerChip;
import com.ombremoon.playingcards.main.PCReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class InitItems {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PCReference.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PCReference.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PCReference.MOD_ID);

    //----- BLOCKS ------\\

    public static final DeferredBlock<Block> POKER_TABLE = BLOCKS.register("poker_table", identifier -> new BlockPokerTable(identifier));
    public static final DeferredItem<BlockItem> POKER_TABLE_ITEM = ITEMS.registerSimpleBlockItem("poker_table", POKER_TABLE);

    public static final DeferredBlock<Block> BAR_STOOL = BLOCKS.register("bar_stool", identifier -> new BlockBarStool(identifier));
    public static final DeferredItem<BlockItem> BAR_STOOL_ITEM = ITEMS.registerSimpleBlockItem("bar_stool", BAR_STOOL);

    //public static final RegistryObject<Block> CASINO_CARPET_SPACE = BLOCKS.register("casino_carpet_space", BlockCasinoCarpet::new);
    //public static final RegistryObject<Item> CASINO_CARPET_SPACE_ITEM = ITEMS.register("casino_carpet_space", () -> new BlockItemBase(CASINO_CARPET_SPACE.get()));

    //----- ITEMS ------\\

    public static final DeferredItem<Item> CARD_DECK = ITEMS.registerItem("card_deck", ItemCardDeck::new);
    public static final DeferredItem<Item> CARD_COVERED = ITEMS.registerItem("card_covered", ItemCardCovered::new);
    public static final DeferredItem<Item> CARD = ITEMS.registerItem("card", ItemCard::new);

    public static final DeferredItem<Item> POKER_CHIP_WHITE = ITEMS.registerItem("poker_chip_white", properties -> new ItemPokerChip((byte) 0, 1, properties));
    public static final DeferredItem<Item> POKER_CHIP_RED = ITEMS.registerItem("poker_chip_red", properties -> new ItemPokerChip((byte) 1, 5, properties));
    public static final DeferredItem<Item> POKER_CHIP_BLUE = ITEMS.registerItem("poker_chip_blue", properties -> new ItemPokerChip((byte) 2, 10, properties));
    public static final DeferredItem<Item> POKER_CHIP_GREEN = ITEMS.registerItem("poker_chip_green", properties -> new ItemPokerChip((byte) 3, 25, properties));
    public static final DeferredItem<Item> POKER_CHIP_BLACK = ITEMS.registerItem("poker_chip_black", properties -> new ItemPokerChip((byte) 4, 100, properties));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(PCReference.MOD_ID, () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(CARD.get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        ITEMS.getEntries().stream().filter(object -> !(object.get() instanceof ItemCardCovered)).forEach((registryObject) -> {
                            if (registryObject.get() instanceof ItemCardDeck deck) {
                                deck.fillItemGroup(output);
                            } else {
                                output.accept(new ItemStack(registryObject.get()));
                            }
                        });
                    }).title(Component.translatable("itemGroup." + PCReference.MOD_ID + ".tab"))
            .build());

    //public static final RegistryObject<Item> DICE_WHITE = ITEMS.register("dice_white", ItemDice::new);

    public static void init (IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);
    }
}
