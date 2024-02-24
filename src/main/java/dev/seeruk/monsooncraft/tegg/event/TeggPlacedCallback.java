package dev.seeruk.monsooncraft.tegg.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface TeggPlacedCallback {
    Event<TeggPlacedCallback> EVENT = EventFactory.createArrayBacked(TeggPlacedCallback.class,
        (listeners) -> (player) -> {
            for (TeggPlacedCallback listener : listeners) {
                var result = listener.interact(player);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.PASS;
        }
    );

    ActionResult interact(PlayerEntity player);
}
