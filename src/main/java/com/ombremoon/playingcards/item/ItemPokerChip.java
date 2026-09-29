package com.ombremoon.playingcards.item;

import com.ombremoon.playingcards.entity.EntityPokerChip;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.item.base.ItemBase;
import com.ombremoon.playingcards.tileentity.TileEntityPokerTable;
import com.ombremoon.playingcards.util.ItemHelper;
import com.ombremoon.playingcards.util.Location;
import com.ombremoon.playingcards.util.StringHelper;
import com.ombremoon.playingcards.util.UnitChatMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemPokerChip extends ItemBase {

    private final byte chipID;
    private final int value;

    public ItemPokerChip(byte chipID, int value, Item.Properties properties) {
        super(properties);
        this.chipID = chipID;
        this.value = value;
    }

    private UnitChatMessage getUnitMessage(Player... players) {
        return new UnitChatMessage("poker_chip", players);
    }

    public byte getChipID() {
        return this.chipID;
    }

    public static Item getPokerChip(byte pokerChipID) {

        switch (pokerChipID) {
            case 1:
                return InitItems.POKER_CHIP_RED.get();
            case 2:
                return InitItems.POKER_CHIP_BLUE.get();
            case 3:
                return InitItems.POKER_CHIP_GREEN.get();
            case 4:
                return InitItems.POKER_CHIP_BLACK.get();
        }

        return InitItems.POKER_CHIP_WHITE.get();
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pDisplay, Consumer<Component> pTooltip, TooltipFlag pIsAdvanced) {
        CompoundTag nbt = ItemHelper.getNBT(pStack);

        if (ItemHelper.hasUUID(nbt, "OwnerID")) {
            pTooltip.accept(Component.literal(ChatFormatting.GRAY + "Owner: " + ChatFormatting.GOLD + ItemHelper.getString(nbt, "OwnerName")));
        }

        else pTooltip.accept(Component.literal(ChatFormatting.GRAY + "Owner: " + ChatFormatting.GOLD + "Not set"));

        pTooltip.accept(Component.literal(ChatFormatting.GRAY + "Value (1): " + ChatFormatting.GOLD + value));

        if (pStack.getCount() > 1) {
            pTooltip.accept(Component.literal(ChatFormatting.GRAY + "Value (" + pStack.getCount() + "): " + ChatFormatting.GOLD + StringHelper.printCommas(value * pStack.getCount())));
        }
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer.isCrouching()) {

            UnitChatMessage unitMessage = getUnitMessage(pPlayer);
            CompoundTag nbt = ItemHelper.getNBT(heldItem);

            if (!ItemHelper.hasUUID(nbt, "OwnerID")) {
                if (!pLevel.isClientSide()) {
                    ItemHelper.updateNBT(heldItem, tag -> {
                        ItemHelper.putUUID(tag, "OwnerID", pPlayer.getUUID());
                        tag.putString("OwnerName", pPlayer.getDisplayName().getString());
                    });
                    unitMessage.printMessage(ChatFormatting.GREEN, Component.translatable("message.poker_chip_owner_set"));
                }
            }

            else if (!pLevel.isClientSide()) unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_owner_error"));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {

        Player player = pContext.getPlayer();

        if (player != null) {

            if (!player.isCrouching()) {

                Level world = pContext.getLevel();
                Location location = new Location(world, pContext.getClickedPos());

                UnitChatMessage unitMessage = getUnitMessage(player);
                CompoundTag nbt = ItemHelper.getNBT(pContext.getItemInHand());

                if (ItemHelper.hasUUID(nbt, "OwnerID")) {

                    UUID ownerID = ItemHelper.getUUID(nbt, "OwnerID");
                    String ownerName = ItemHelper.getString(nbt, "OwnerName");

                    if (location.getBlockState().hasBlockEntity()) {

                        BlockEntity tileEntity = location.getTileEntity();

                        if (tileEntity instanceof TileEntityPokerTable pokerTable) {

                            if (!ownerID.equals(pokerTable.getOwnerID())) {

                                if (!world.isClientSide()) unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_table_error"));
                                return InteractionResult.PASS;
                            }
                        }
                    }

                    if (!world.isClientSide()) {
                        EntityPokerChip chip = new EntityPokerChip(world, pContext.getClickLocation(), ownerID, ownerName, chipID);
                        world.addFreshEntity(chip);
                        pContext.getItemInHand().shrink(1);
                    }
                }

                else if (!world.isClientSide()) unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_owner_missing"));

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
