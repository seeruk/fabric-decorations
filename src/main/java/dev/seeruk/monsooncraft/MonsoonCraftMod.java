package dev.seeruk.monsooncraft;

import dev.seeruk.monsooncraft.block.Blocks;
import dev.seeruk.monsooncraft.disposal.DisposalCommand;
import dev.seeruk.monsooncraft.enchant.Enchantments;
import dev.seeruk.monsooncraft.screen.Screens;
import dev.seeruk.monsooncraft.tegg.TeggGame;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonsoonCraftMod implements ModInitializer {
	public static final String MOD_ID = "monsooncraft";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello MonsoonCraft!");

		Blocks.register();
		DisposalCommand.register();
		Enchantments.register();
		Screens.register();

		TeggGame.initialise();
	}
}
