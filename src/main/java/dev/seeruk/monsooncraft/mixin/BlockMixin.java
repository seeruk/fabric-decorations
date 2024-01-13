package dev.seeruk.monsooncraft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.seeruk.monsooncraft.enchant.AttractionEnchantment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;


@Mixin(Block.class)
public class BlockMixin {
    @ModifyArg(
        method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static Consumer<ItemStack> forEachDroppedStack(Consumer<ItemStack> original, @Nullable @Local(argsOnly = true) Entity entity) {
        return (itemStack -> {
            if (entity instanceof PlayerEntity player && AttractionEnchantment.isHoldingAttractionTool(player)) {
                if (player.getInventory().insertStack(itemStack)) {
                    return;
                }
            }
            original.accept(itemStack);
        });
    }
}