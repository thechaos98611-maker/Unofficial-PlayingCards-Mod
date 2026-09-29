package com.ombremoon.playingcards.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class EntityStacked extends Entity {

    public static final byte MAX_STACK_SIZE = 52;
    protected static final EntityDataAccessor<String> STACK = SynchedEntityData.defineId(EntityStacked.class, EntityDataSerializers.STRING);

    protected EntityStacked(EntityType<? extends EntityStacked> type, Level level) {
        super(type, level);
    }

    protected EntityStacked(EntityType<? extends EntityStacked> type, Level level, Vec3 position) {
        this(type, level);
        setPos(position.x, position.y, position.z);
        setYRot(0);
        setXRot(0);
    }

    public int getStackAmount() {
        return entityData.get(STACK).length();
    }

    public byte getTopStackID() {
        return getIDAt(getStackAmount() - 1);
    }

    public byte getIDAt(int index) {
        String stack = entityData.get(STACK);
        return index >= 0 && index < stack.length() ? (byte) stack.charAt(index) : 0;
    }

    public void removeFromTop() {
        String stack = entityData.get(STACK);
        if (!stack.isEmpty()) {
            entityData.set(STACK, stack.substring(0, stack.length() - 1));
        }
    }

    public void addToTop(byte id) {
        entityData.set(STACK, entityData.get(STACK) + (char) (id & 0xFF));
    }

    public void createStack() {
        entityData.set(STACK, "");
    }

    public void shuffleStack() {
        char[] stack = entityData.get(STACK).toCharArray();
        for (int i = stack.length; i > 1; i--) {
            int j = getRandom().nextInt(i);
            char value = stack[i - 1];
            stack[i - 1] = stack[j];
            stack[j] = value;
        }
        entityData.set(STACK, new String(stack));
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            noPhysics = false;
        } else {
            noPhysics = !level().noCollision(this);
            setDeltaMovement(getDeltaMovement().add(0.0D, noPhysics ? 0.02D : -0.04D, 0.0D));
        }

        move(MoverType.SELF, getDeltaMovement());
        Vec3 pos = position();
        double size = 0.2D;
        double height = 0.03D + 0.0045D * getStackAmount();
        setBoundingBox(new AABB(pos.x - size, pos.y, pos.z - size, pos.x + size, pos.y + height, pos.z + size));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STACK, "");
        defineAdditionalSynchedData(builder);
    }

    protected abstract void defineAdditionalSynchedData(SynchedEntityData.Builder builder);

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        entityData.set(STACK, input.getStringOr("Stack", ""));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putString("Stack", entityData.get(STACK));
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
