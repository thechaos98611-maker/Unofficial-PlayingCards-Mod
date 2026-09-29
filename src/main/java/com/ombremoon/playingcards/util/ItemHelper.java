package com.ombremoon.playingcards.util;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.List;

public class ItemHelper {

    public static CompoundTag getNBT(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void updateNBT(ItemStack stack, Consumer<CompoundTag> update) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, update);
    }

    public static void setNBT(ItemStack stack, CompoundTag nbt) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static void setModelIndex(ItemStack stack, int index) {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) index), List.of(), List.of(), List.of()));
    }

    public static int getCardId(ItemStack stack) {
        CompoundTag data = getNBT(stack);
        return data.getIntOr("CardID", stack.getDamageValue());
    }

    public static void setCardId(ItemStack stack, int cardId) {
        updateNBT(stack, data -> data.putInt("CardID", cardId));
        stack.setDamageValue(cardId);
        setModelIndex(stack, cardId);
    }

    public static byte getByte(CompoundTag nbt, String key) {
        return nbt.getByteOr(key, (byte) 0);
    }

    public static boolean getBoolean(CompoundTag nbt, String key) {
        return nbt.getBooleanOr(key, false);
    }

    public static String getString(CompoundTag nbt, String key) {
        return nbt.getStringOr(key, "");
    }

    public static boolean hasUUID(CompoundTag nbt, String key) {
        return nbt.getString(key).map(value -> {
            try {
                UUID.fromString(value);
                return true;
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }).orElse(false);
    }

    public static UUID getUUID(CompoundTag nbt, String key) {
        return nbt.getString(key).map(UUID::fromString).orElse(null);
    }

    public static void putUUID(CompoundTag nbt, String key, UUID uuid) {
        if (uuid != null) {
            nbt.putString(key, uuid.toString());
        }
    }

    public static void spawnStackAtEntity(Level world, Entity entity, ItemStack stack) {
        spawnStack(world, entity.position().x, entity.position().y, entity.position().z, stack);
    }

    private static void spawnStack(Level world, double x, double y, double z, ItemStack stack) {
        ItemEntity item = new ItemEntity(world, x, y, z, stack);
        item.setNoPickUpDelay();
        item.setDeltaMovement(0, 0, 0);
        world.addFreshEntity(item);
    }
}
