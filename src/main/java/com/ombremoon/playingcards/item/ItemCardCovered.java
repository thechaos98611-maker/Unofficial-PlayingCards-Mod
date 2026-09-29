package com.ombremoon.playingcards.item;

import com.ombremoon.playingcards.entity.EntityCard;
import com.ombremoon.playingcards.entity.EntityCardDeck;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.item.base.ItemBase;
import com.ombremoon.playingcards.util.CardHelper;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.world.entity.EquipmentSlot;

public class ItemCardCovered extends ItemBase {

    public ItemCardCovered(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pDisplay, Consumer<Component> pTooltip, TooltipFlag pIsAdvanced) {
        CompoundTag nbt = ItemHelper.getNBT(pStack);
        pTooltip.accept(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY).append(Component.translatable(CardHelper.CARD_SKIN_NAMES[ItemHelper.getByte(nbt, "SkinID")]).withStyle(ChatFormatting.AQUA)));
    }

    public void flipCard(ItemStack heldItem, LivingEntity entity) {

        if (entity instanceof Player player) {

            if (heldItem.getItem() instanceof ItemCardCovered) {
                CompoundTag heldNBT = ItemHelper.getNBT(heldItem);

                Item nextCard = InitItems.CARD.get();
                if (!ItemHelper.getBoolean(heldNBT, "Covered")) nextCard = InitItems.CARD_COVERED.get();

                ItemStack newCard = new ItemStack(nextCard);
                int cardId = ItemHelper.getCardId(heldItem);
                newCard.setDamageValue(cardId);

                ItemHelper.updateNBT(newCard, nbt -> {
                    ItemHelper.putUUID(nbt, "UUID", ItemHelper.getUUID(heldNBT, "UUID"));
                    nbt.putByte("SkinID", ItemHelper.getByte(heldNBT, "SkinID"));
                    nbt.putBoolean("Covered", !ItemHelper.getBoolean(heldNBT, "Covered"));
                    nbt.putInt("CardID", cardId);
                });
                int modelIndex = newCard.getItem() == InitItems.CARD_COVERED.get()
                        ? ItemHelper.getByte(heldNBT, "SkinID")
                        : cardId;
                ItemHelper.setModelIndex(newCard, modelIndex);

                player.setItemInHand(InteractionHand.MAIN_HAND, newCard);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack pStack, ServerLevel pLevel, Entity pEntity, EquipmentSlot pSlot) {
        if (pLevel.getGameTime() % 60 == 0) {

            if (pEntity instanceof Player player) {
                BlockPos pos = player.blockPosition();

                CompoundTag nbt = ItemHelper.getNBT(pStack);

                if (ItemHelper.hasUUID(nbt, "UUID")) {
                    UUID id = ItemHelper.getUUID(nbt, "UUID");

                    if (id.getLeastSignificantBits() == 0) {
                        return;
                    }

                    List<EntityCardDeck> closeDecks = pLevel.getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20));

                    boolean found = false;

                    for (EntityCardDeck closeDeck : closeDecks) {

                        if (closeDeck.getUUID().equals(id)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        pStack.shrink(1);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();

        if (player != null) {

            if (!player.isCrouching()) {

                BlockPos pos = pContext.getClickedPos();
                List<EntityCardDeck> closeDecks = pContext.getLevel().getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 8, pos.getY() - 8, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 8, pos.getZ() + 8));

                CompoundTag nbt = ItemHelper.getNBT(pContext.getItemInHand());

                UUID deckID = ItemHelper.getUUID(nbt, "UUID");

                for (EntityCardDeck closeDeck : closeDecks) {

                    if (closeDeck.getUUID().equals(deckID)) {

                        Level world = pContext.getLevel();
                        EntityCard cardDeck = new EntityCard(world, pContext.getClickLocation(), pContext.getRotation(), ItemHelper.getByte(nbt, "SkinID"), deckID, ItemHelper.getBoolean(nbt, "Covered"), (byte) ItemHelper.getCardId(pContext.getItemInHand()));
                        world.addFreshEntity(cardDeck);
                        pContext.getItemInHand().shrink(1);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && !level.isClientSide()) {
            flipCard(player.getMainHandItem(), player);
        }
        return InteractionResult.SUCCESS;
    }
}
