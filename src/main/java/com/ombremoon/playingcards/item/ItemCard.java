package com.ombremoon.playingcards.item;

import com.ombremoon.playingcards.util.CardHelper;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ItemCard extends ItemCardCovered {

    public ItemCard(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pDisplay, Consumer<Component> pTooltip, TooltipFlag pIsAdvanced) {
        pTooltip.accept(CardHelper.getCardName(ItemHelper.getCardId(pStack)).withStyle(ChatFormatting.GOLD));
        super.appendHoverText(pStack, pContext, pDisplay, pTooltip, pIsAdvanced);
    }
}
