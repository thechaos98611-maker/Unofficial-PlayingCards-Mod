package com.ombremoon.playingcards.tileentity;

import com.ombremoon.playingcards.init.InitTileEntityTypes;
import com.ombremoon.playingcards.tileentity.base.TileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class TileEntityPokerTable extends TileEntityBase {

    private UUID ownerID;
    private String ownerName = "";

    public TileEntityPokerTable(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public TileEntityPokerTable(BlockPos pos, BlockState blockState) {
        this(InitTileEntityTypes.POKER_TABLE.get(), pos, blockState);
    }

    public void setOwner(Player player) {
        this.ownerID = player.getUUID();
        this.ownerName = player.getDisplayName().getString();
        setChanged();
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ownerID = input.getString("OwnerID").map(UUID::fromString).orElse(null);
        ownerName = input.getStringOr("OwnerName", "");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (ownerID != null) {
            output.putString("OwnerID", ownerID.toString());
        }
        output.putString("OwnerName", ownerName);
    }
}
