package com.ombremoon.playingcards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.playingcards.entity.EntityCardDeck;
import com.ombremoon.playingcards.init.InitItems;
import com.ombremoon.playingcards.util.CardHelper;
import com.ombremoon.playingcards.util.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderEntityCardDeck extends EntityRenderer<EntityCardDeck, RenderEntityCardDeck.State> {

    private final ItemModelResolver itemModelResolver;

    public RenderEntityCardDeck(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EntityCardDeck entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        ItemStack card = new ItemStack(InitItems.CARD_COVERED.get());
        ItemHelper.updateNBT(card, nbt -> nbt.putByte("SkinID", entity.getSkinID()));
        ItemHelper.setModelIndex(card, entity.getSkinID());
        state.stackAmount = entity.getStackAmount();
        state.rotation = entity.getRotation();
        itemModelResolver.updateForNonLiving(state.itemState, card, ItemDisplayContext.GROUND, entity);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.rotation + 180));
        poseStack.scale(1.5F, 1.5F, 1.5F);
        for (int i = 0; i < state.stackAmount + 2; i++) {
            CardHelper.renderItem(state.itemState, 0, i * 0.003D, 0, poseStack, collector, state.lightCoords, i);
        }
        poseStack.popPose();
    }

    public static class State extends EntityRenderState {
        private final ItemStackRenderState itemState = new ItemStackRenderState();
        private int stackAmount;
        private float rotation;
    }
}
