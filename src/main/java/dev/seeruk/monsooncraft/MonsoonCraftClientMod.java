package dev.seeruk.monsooncraft;

import dev.seeruk.monsooncraft.screen.CompressorBlockGuiDescription;
import dev.seeruk.monsooncraft.screen.CompressorBlockScreen;
import dev.seeruk.monsooncraft.screen.Screens;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MonsoonCraftClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.<CompressorBlockGuiDescription, CompressorBlockScreen>register(Screens.COMPRESSOR_SCREEN_TYPE, (gui, inventory, title) -> {
            return new CompressorBlockScreen(gui, inventory.player, title);
        });
    }
}
