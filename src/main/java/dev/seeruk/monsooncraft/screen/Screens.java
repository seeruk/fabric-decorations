package dev.seeruk.monsooncraft.screen;

import dev.seeruk.monsooncraft.MonsoonCraftMod;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class Screens {
    public static ScreenHandlerType<CompressorBlockGuiDescription> COMPRESSOR_SCREEN_TYPE = new ScreenHandlerType<>((int syncId, PlayerInventory inventory) -> {
        return new CompressorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY);
    }, FeatureSet.of(FeatureFlags.VANILLA));


    public static void register() {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(MonsoonCraftMod.MOD_ID, "compressor_gui"), COMPRESSOR_SCREEN_TYPE);
    }
}
