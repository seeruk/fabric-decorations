package dev.seeruk.monsooncraft.tegg.mixin;

import dev.seeruk.monsooncraft.tegg.TeggGame;
import dev.seeruk.monsooncraft.tegg.event.TeggPlacedCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DragonEggBlock.class)
public class DragonEggMixin extends FallingBlock {
    public DragonEggMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient || placer == null) {
            return;
        }

        if (TeggGame.isItemStackTegg(itemStack) && placer instanceof PlayerEntity player) {
            TeggPlacedCallback.EVENT.invoker().interact(player);
        }
    }
}
