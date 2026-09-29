package com.ombremoon.playingcards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.playingcards.entity.EntityPokerChip;
import com.ombremoon.playingcards.item.ItemPokerChip;
import com.ombremoon.playingcards.util.CardHelper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RenderEntityPokerChip extends EntityRenderer<EntityPokerChip, RenderEntityPokerChip.State> {

    private final ItemModelResolver itemModelResolver;

    public RenderEntityPokerChip(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EntityPokerChip entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.chips.clear();
        for (int i = 0; i < entity.getStackAmount(); i++) {
            ItemStackRenderState chipState = new ItemStackRenderState();
            ItemStack chip = new ItemStack(ItemPokerChip.getPokerChip(entity.getIDAt(i)));
            itemModelResolver.updateForNonLiving(chipState, chip, ItemDisplayContext.GROUND, entity);
            state.chips.add(chipState);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        poseStack.pushPose();
        poseStack.translate(0, 0.01D, 0.07D);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (int i = 0; i < state.chips.size(); i++) {
            poseStack.pushPose();
            Random randomX = new Random(i * 200000L);
            Random randomY = new Random(i * 100000L);
            poseStack.translate(randomX.nextDouble() * 0.05D - 0.025D, 0, randomY.nextDouble() * 0.05D - 0.025D);
            poseStack.mulPose(Axis.XN.rotationDegrees(90));
            CardHelper.renderItem(state.chips.get(i), 0, 0, i * 0.032D, poseStack, collector, state.lightCoords, i);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static class State extends EntityRenderState {
        private final List<ItemStackRenderState> chips = new ArrayList<>();
    }
}
