package dev.seeruk.monsooncraft.tegg.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface TeggRetrievedCallback {
    Event<TeggRetrievedCallback> EVENT = EventFactory.createArrayBacked(TeggRetrievedCallback.class,
        (listeners) -> (player) -> {
            for (TeggRetrievedCallback listener : listeners) {
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
