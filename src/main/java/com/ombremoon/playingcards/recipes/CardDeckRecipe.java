package com.ombremoon.playingcards.recipes;

import com.mojang.serialization.MapCodec;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CardDeckRecipe extends CustomRecipe {

    public static final MapCodec<CardDeckRecipe> CODEC = MapCodec.unit(CardDeckRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CardDeckRecipe> STREAM_CODEC = StreamCodec.unit(new CardDeckRecipe());
    public static final RecipeSerializer<CardDeckRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public CardDeckRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3 || input.size() != 9) {
            return false;
        }

        for (int index = 0; index < input.size(); index++) {
            ItemStack ingredient = input.getItem(index);
            if (index == 4) {
                if (!isSkinDye(ingredient)) return false;
            } else if (!ingredient.is(Items.PAPER)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSkinDye(ItemStack stack) {
        return stack.is(Items.BLUE_DYE) || stack.is(Items.RED_DYE) || stack.is(Items.BLACK_DYE) || stack.is(Items.PINK_DYE);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = new ItemStack(InitItems.CARD_DECK.get());
        ItemStack dye = input.getItem(4);
        byte skinId = dye.is(Items.RED_DYE) ? (byte) 1
                : dye.is(Items.BLACK_DYE) ? (byte) 2
                : dye.is(Items.PINK_DYE) ? (byte) 3 : (byte) 0;
        ItemHelper.updateNBT(result, nbt -> nbt.putByte("SkinID", skinId));
        ItemHelper.setModelIndex(result, skinId);
        return result;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}
