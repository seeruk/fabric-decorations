package dev.seeruk.monsooncraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PickaxedWallBlock {
    public void onPickaxeUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand);
}
