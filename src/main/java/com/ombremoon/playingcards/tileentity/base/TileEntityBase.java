package com.ombremoon.playingcards.tileentity.base;

import com.ombremoon.playingcards.util.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityBase extends BlockEntity {

    protected TileEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Location getLocation() {
        return new Location(level, worldPosition);
    }
}
