package dev.seeruk.monsooncraft.block;

import dev.seeruk.monsooncraft.MonsoonCraftMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.seeruk.monsooncraft.MonsoonCraftMod.MOD_ID;

public class Blocks {
    public static final Block COMPRESSOR_BLOCK = new CompressorBlock(FabricBlockSettings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f).nonOpaque());
    public static final Block DRIED_HAY_BLOCK = new HayBlock(FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.GRASS));
    public static final Block DRIED_HAY_SLAB = new SlabBlock(FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.GRASS));
    public static final Block DRIED_HAY_STAIRS = new StairsBlock(DRIED_HAY_BLOCK.getDefaultState(), AbstractBlock.Settings.copy(DRIED_HAY_BLOCK));

    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR_BLOCK_ENTITY = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        new Identifier(MonsoonCraftMod.MOD_ID, "compressor"),
        FabricBlockEntityTypeBuilder.create(CompressorBlockEntity::new, COMPRESSOR_BLOCK).build()
    );

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "compressor"), COMPRESSOR_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "compressor"), new BlockItem(COMPRESSOR_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dried_hay_block"), DRIED_HAY_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dried_hay_block"), new BlockItem(DRIED_HAY_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dried_hay_slab"), DRIED_HAY_SLAB);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dried_hay_slab"), new BlockItem(DRIED_HAY_SLAB, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dried_hay_stairs"), DRIED_HAY_STAIRS);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dried_hay_stairs"), new BlockItem(DRIED_HAY_STAIRS, new FabricItemSettings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(COMPRESSOR_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            content.add(DRIED_HAY_BLOCK);
            content.add(DRIED_HAY_SLAB);
            content.add(DRIED_HAY_STAIRS);
        });
    }
}
