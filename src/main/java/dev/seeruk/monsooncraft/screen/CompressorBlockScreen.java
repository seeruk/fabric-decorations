package dev.seeruk.monsooncraft.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CompressorBlockScreen extends CottonInventoryScreen<CompressorBlockGuiDescription> {
    public CompressorBlockScreen(CompressorBlockGuiDescription gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }
}
