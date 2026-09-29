package com.ombremoon.playingcards.item;

import com.ombremoon.playingcards.entity.EntityCardDeck;
import com.ombremoon.playingcards.item.base.ItemBase;
import com.ombremoon.playingcards.main.PCReference;
import com.ombremoon.playingcards.util.CardHelper;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ItemCardDeck extends ItemBase {

    public ItemCardDeck(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pDisplay, Consumer<Component> pTooltip, TooltipFlag pIsAdvanced) {
        CompoundTag nbt = ItemHelper.getNBT(pStack);
        pTooltip.accept(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY).append(Component.translatable(CardHelper.CARD_SKIN_NAMES[ItemHelper.getByte(nbt, "SkinID")]).withStyle(ChatFormatting.AQUA)));
    }

    public void fillItemGroup(CreativeModeTab.Output output) {
        for (byte colorID = 0; colorID < CardHelper.CARD_SKIN_NAMES.length; colorID++) {

            byte skinID = colorID;
            ItemStack stack = new ItemStack(this);
            ItemHelper.updateNBT(stack, nbt -> nbt.putByte("SkinID", skinID));
            ItemHelper.setModelIndex(stack, skinID);
            output.accept(stack);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        if (!world.isClientSide()) {
            CompoundTag nbt = ItemHelper.getNBT(pContext.getItemInHand());
            EntityCardDeck cardDeck = new EntityCardDeck(world, pContext.getClickLocation(), pContext.getRotation(), ItemHelper.getByte(nbt, "SkinID"));
            world.addFreshEntity(cardDeck);
            pContext.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
