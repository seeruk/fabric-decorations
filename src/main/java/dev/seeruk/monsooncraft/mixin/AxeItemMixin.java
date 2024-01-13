package dev.seeruk.monsooncraft.mixin;

import dev.seeruk.monsooncraft.block.AxedFenceBlock;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin  {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var state = world.getBlockState(pos);

        if (state.getBlock() instanceof AxedFenceBlock fenceBlock) {
            fenceBlock.onAxeUse(state, world, pos, context.getPlayer(), context.getHand());
        }
    }
}
