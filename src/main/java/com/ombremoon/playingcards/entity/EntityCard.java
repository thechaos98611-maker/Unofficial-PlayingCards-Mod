package com.ombremoon.playingcards.entity;

import com.ombremoon.playingcards.entity.base.EntityStacked;
import com.ombremoon.playingcards.init.InitEntityTypes;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.item.ItemCardCovered;
import com.ombremoon.playingcards.util.ChatHelper;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class EntityCard extends EntityStacked {

    private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> SKIN_ID = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> DECK_UUID = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> COVERED = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.BOOLEAN);

    public EntityCard(EntityType<? extends EntityCard> type, Level world) {
        super(type, world);
    }

    public EntityCard(Level world, Vec3 position, float rotation, byte skinID, UUID deckUUID, boolean covered, byte firstCardID) {
        super(InitEntityTypes.CARD.get(), world, position);

        createStack();
        addToTop(firstCardID);
        this.entityData.set(ROTATION, rotation);
        this.entityData.set(SKIN_ID, skinID);
        this.entityData.set(DECK_UUID, deckUUID.toString());
        this.entityData.set(COVERED, covered);
    }

    public float getRotation() {
        return this.entityData.get(ROTATION);
    }

    public byte getSkinID() {
        return this.entityData.get(SKIN_ID);
    }

    public UUID getDeckUUID() {
        try {
            return UUID.fromString(this.entityData.get(DECK_UUID));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public boolean isCover() {
        return this.entityData.get(COVERED);
    }

    private void takeCard(Player player) {

        ItemStack card = new ItemStack(InitItems.CARD.get());
        if (this.entityData.get(COVERED)) card = new ItemStack(InitItems.CARD_COVERED.get());

        card.setDamageValue(getTopStackID());
        ItemHelper.updateNBT(card, nbt -> {
            ItemHelper.putUUID(nbt, "UUID", getDeckUUID());
            nbt.putByte("SkinID", this.entityData.get(SKIN_ID));
            nbt.putBoolean("Covered", this.entityData.get(COVERED));
            nbt.putInt("CardID", getTopStackID());
        });
        ItemHelper.setModelIndex(card, this.entityData.get(COVERED) ? this.entityData.get(SKIN_ID) : getTopStackID());

        if (!level().isClientSide()) {
            ItemHelper.spawnStackAtEntity(level(), player, card);
        }

        removeFromTop();

        if (getStackAmount() <= 0) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().getGameTime() % 20 == 0) {

            BlockPos pos = blockPosition();

            List<EntityCardDeck> closeDecks = level().getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20));

            boolean foundParentDeck = false;

            for (EntityCardDeck closeDeck : closeDecks) {

                if (getDeckUUID().equals(closeDeck.getUUID())) {
                    foundParentDeck = true;
                }
            }

            if (!foundParentDeck) discard();

        }
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand, Vec3 pLocation) {
        ItemStack stack = pPlayer.getItemInHand(pHand);

        if (stack.getItem() instanceof ItemCardCovered) {

            if (getStackAmount() < MAX_STACK_SIZE) {
                addToTop((byte) stack.getDamageValue());
                stack.shrink(1);
            }

            else {
                if (level().isClientSide()) ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_full"), pPlayer);
            }
        }

        else takeCard(pPlayer);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        this.entityData.set(COVERED, !this.entityData.get(COVERED));
        return true;
    }

    @Override
    protected void defineAdditionalSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ROTATION, 0F);
        builder.define(SKIN_ID, (byte) 0);
        builder.define(DECK_UUID, "");
        builder.define(COVERED, false);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(ROTATION, input.getFloatOr("Rotation", 0F));
        this.entityData.set(SKIN_ID, input.getByteOr("SkinID", (byte) 0));
        this.entityData.set(DECK_UUID, input.getStringOr("DeckID", ""));
        this.entityData.set(COVERED, input.getBooleanOr("Covered", false));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Rotation", this.entityData.get(ROTATION));
        output.putByte("SkinID", this.entityData.get(SKIN_ID));
        UUID deckUUID = getDeckUUID();
        if (deckUUID != null) output.putString("DeckID", deckUUID.toString());
        output.putBoolean("Covered", this.entityData.get(COVERED));
    }
}
