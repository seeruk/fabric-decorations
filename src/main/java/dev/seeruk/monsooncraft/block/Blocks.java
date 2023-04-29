package dev.seeruk.monsooncraft.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.seeruk.monsooncraft.MonsooncraftMod.MOD_ID;

public class Blocks {
    public static final Block DRIED_HAY_BLOCK = new HayBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).strength(0.5f).sounds(BlockSoundGroup.GRASS));
    public static final Block DRIED_HAY_SLAB = new SlabBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).strength(0.5f).sounds(BlockSoundGroup.GRASS));
    public static final Block DRIED_HAY_STAIRS = new StairsBlock(DRIED_HAY_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(DRIED_HAY_BLOCK));

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dried_hay_block"), DRIED_HAY_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dried_hay_block"), new BlockItem(DRIED_HAY_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dried_hay_slab"), DRIED_HAY_SLAB);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dried_hay_slab"), new BlockItem(DRIED_HAY_SLAB, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dried_hay_stairs"), DRIED_HAY_STAIRS);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dried_hay_stairs"), new BlockItem(DRIED_HAY_STAIRS, new FabricItemSettings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            content.add(DRIED_HAY_BLOCK);
            content.add(DRIED_HAY_SLAB);
            content.add(DRIED_HAY_STAIRS);
        });
    }
}
