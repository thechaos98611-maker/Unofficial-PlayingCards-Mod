package com.ombremoon.playingcards.entity;

import com.ombremoon.playingcards.entity.base.EntityStacked;
import com.ombremoon.playingcards.init.InitEntityTypes;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.main.PCReference;
import com.ombremoon.playingcards.util.ChatHelper;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class EntityCardDeck extends EntityStacked {

    private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> SKIN_ID = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.BYTE);

    public EntityCardDeck(EntityType<? extends EntityCardDeck> type, Level world) {
        super(type, world);
    }

    public EntityCardDeck(Level world, Vec3 position, float rotation, byte skinID) {
        super(InitEntityTypes.CARD_DECK.get(), world, position);

        createAndFillDeck();
        shuffleStack();

        this.entityData.set(ROTATION, rotation);
        this.entityData.set(SKIN_ID, skinID);
    }

    public float getRotation() {
        return this.entityData.get(ROTATION);
    }

    public byte getSkinID() {
        return this.entityData.get(SKIN_ID);
    }

    private void createAndFillDeck() {

        StringBuilder newStack = new StringBuilder(52);
        for (int index = 0; index < 52; index++) {
            newStack.append((char) index);
        }
        this.entityData.set(STACK, newStack.toString());
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand, Vec3 pLocation) {
        if (pHand == InteractionHand.MAIN_HAND) {

            if (getStackAmount() > 0) {

                int cardID = getTopStackID();

                ItemStack card = new ItemStack(InitItems.CARD_COVERED.get());

                card.setDamageValue(cardID);
                ItemHelper.updateNBT(card, nbt -> {
                    ItemHelper.putUUID(nbt, "UUID", getUUID());
                    nbt.putByte("SkinID", this.entityData.get(SKIN_ID));
                    nbt.putBoolean("Covered", true);
                    nbt.putInt("CardID", cardID);
                });
                ItemHelper.setModelIndex(card, this.entityData.get(SKIN_ID));

                if (!level().isClientSide()) {
                    ItemHelper.spawnStackAtEntity(level(), pPlayer, card);
                }

                removeFromTop();

                return pPlayer.getMainHandItem().isEmpty() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }

            else if (level().isClientSide()) ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_empty"), pPlayer);
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource pSource, float pAmount) {
        if (pSource.getDirectEntity() instanceof Player player) {

            if (player.isCrouching()) {
                ItemStack deck = new ItemStack(InitItems.CARD_DECK.get());
                ItemHelper.updateNBT(deck, nbt -> nbt.putByte("SkinID", this.entityData.get(SKIN_ID)));
                ItemHelper.setModelIndex(deck, this.entityData.get(SKIN_ID));

                ItemHelper.spawnStackAtEntity(level(), player, deck);
                discard();
            } else {
                shuffleStack();
                if (level().isClientSide()) ChatHelper.printModMessage(ChatFormatting.GREEN, Component.translatable("message.stack_shuffled"), player);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void defineAdditionalSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ROTATION, 0F);
        builder.define(SKIN_ID, (byte) 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(ROTATION, input.getFloatOr("Rotation", 0F));
        this.entityData.set(SKIN_ID, input.getByteOr("SkinID", (byte) 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Rotation", this.entityData.get(ROTATION));
        output.putByte("SkinID", this.entityData.get(SKIN_ID));
    }
}
