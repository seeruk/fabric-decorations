package dev.seeruk.monsooncraft.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CompressorBlock extends BlockWithEntity implements InventoryProvider {
    public static final BooleanProperty POWERED;

    protected CompressorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CompressorBlockEntity(pos, state);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SidedInventory inventory) {
            return inventory;
        }
        return null; // Is this allowed / possible?
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Blocks.COMPRESSOR_BLOCK_ENTITY, (tickWorld, pos, tickState, be) -> {
            CompressorBlockEntity.tick(tickWorld, pos, tickState, be);
        });
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        var bp = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        var sp = state.get(POWERED);
        var entity = world.getBlockEntity(pos, Blocks.COMPRESSOR_BLOCK_ENTITY);

        if (entity.isPresent()) {
            var compressor = entity.get();
            if (sp != bp && bp) {
                if (compressor.mode == CompressorBlockEntity.MODE_2x2) {
                    compressor.setMode(CompressorBlockEntity.MODE_3x3);
                } else if (compressor.mode == CompressorBlockEntity.MODE_3x3) {
                    compressor.setMode(CompressorBlockEntity.MODE_2x2);
                }
            }
        }

        world.setBlockState(pos, state.with(POWERED, bp), 2);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // You need a Block.createScreenHandlerFactory implementation that delegates to the block entity,
        // such as the one from BlockWithEntity
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    static {
        POWERED = Properties.POWERED;
    }
}
