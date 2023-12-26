package dev.seeruk.monsooncraft.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

import static dev.seeruk.monsooncraft.MonsoonCraftMod.MOD_ID;

public class Enchantments {
    public static final Enchantment ATTRACTION = new AttractionEnchantment();

    public static void register() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "attraction"), ATTRACTION);
    }
}
