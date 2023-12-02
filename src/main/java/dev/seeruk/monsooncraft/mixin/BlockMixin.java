package dev.seeruk.monsooncraft.mixin;

import dev.seeruk.monsooncraft.enchant.AttractionEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(
        method = "afterBreak(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/item/ItemStack;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V", shift = At.Shift.AFTER),
        cancellable = true
    )
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (world instanceof ServerWorld serverWorld && AttractionEnchantment.isHoldingAttractionTool(player)) {
            Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player, tool).forEach(itemStack -> {
                if (player.getInventory().insertStack(itemStack)) {
                    state.onStacksDropped(serverWorld, pos, tool, true);
                    ci.cancel();
                }
            });
        }
    }
}
