package com.ombremoon.playingcards.entity;

import com.ombremoon.playingcards.init.InitEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EntitySeat extends Entity {

    private BlockPos sourceBlock;

    public EntitySeat(EntityType<? extends EntitySeat> type, Level world) {
        super(type, world);
    }

    public EntitySeat(Level world, BlockPos sourceBlock) {
        this(InitEntityTypes.SEAT.get(), world);
        this.sourceBlock = sourceBlock;
        setPos(sourceBlock.getX() + 0.5F, sourceBlock.getY() + 0.3F, sourceBlock.getZ() + 0.5F);
    }

    private BlockPos getSourceBlock() {
        return sourceBlock;
    }

    public static void createSeat(Level world, BlockPos pos, Player player) {

        if (!world.isClientSide()) {

            List<EntitySeat> seats = world.getEntitiesOfClass(EntitySeat.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));

            if (seats.isEmpty()) {

                EntitySeat seat = new EntitySeat(world, pos);
                world.addFreshEntity(seat);
                player.startRiding(seat);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        if (sourceBlock == null) {
            sourceBlock = this.blockPosition();
        }

        if (!level().isClientSide()) {

            if (getPassengers().isEmpty() || this.level().getBlockState(sourceBlock).isAir()) {
                discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        int x = input.getIntOr("SourceX", blockPosition().getX());
        int y = input.getIntOr("SourceY", blockPosition().getY());
        int z = input.getIntOr("SourceZ", blockPosition().getZ());
        sourceBlock = new BlockPos(x, y, z);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (sourceBlock != null) {
            output.putInt("SourceX", sourceBlock.getX());
            output.putInt("SourceY", sourceBlock.getY());
            output.putInt("SourceZ", sourceBlock.getZ());
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
