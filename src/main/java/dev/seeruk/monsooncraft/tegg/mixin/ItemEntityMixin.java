package dev.seeruk.monsooncraft.tegg.mixin;

import dev.seeruk.monsooncraft.tegg.TeggGame;
import dev.seeruk.monsooncraft.tegg.event.TeggRetrievedCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "onPlayerCollision", at = @At(value = "HEAD"))
	public void onEntityItemPickup(PlayerEntity player, CallbackInfo ci) {
		var ie = (ItemEntity)(Object)this;
		if (ie.getWorld().isClient) {
			return;
		}

		ItemStack itemStack = ie.getStack();

		if (!ie.cannotPickup() && (ie.getOwner() == null || ie.getOwner().equals(player.getUuid()))) {
			if (TeggGame.isItemStackTegg(itemStack)) {
				TeggRetrievedCallback.EVENT.invoker().interact(player);
			}
		}
	}
}
