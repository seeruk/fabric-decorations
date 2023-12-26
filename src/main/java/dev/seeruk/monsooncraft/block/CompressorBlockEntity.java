package dev.seeruk.monsooncraft.block;

import dev.seeruk.monsooncraft.inventory.SimpleRecipeInputInventory;
import dev.seeruk.monsooncraft.screen.CompressorBlockGuiDescription;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CompressorBlockEntity extends BlockEntity implements InventoryProvider, NamedScreenHandlerFactory, PropertyDelegateHolder, SidedInventory {
    public static final int INPUT_SLOT = 0;

    public static final int OUTPUT_SLOT = 1;

    public static final int MODE_3x3 = 0;

    public static final int MODE_2x2 = 1;

    public static final int MODE_PROPERTY = 0;

    protected DefaultedList<ItemStack> inventory;

    protected long compressCooldown;

    protected int mode = 0;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int size() {
            // This is how many properties you have. We have two of them, so we'll return 2.
            return 1;
        }

        @Override
        public int get(int index) {
            // Each property has a unique index that you can choose.
            // Our properties will be 0 for the progress and 1 for the maximum.

            if (index == 0) {
                return mode;
            }

            // Unknown property IDs will fall back to -1
            return -1;
        }

        @Override
        public void set(int index, int value) {}
    };

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Blocks.COMPRESSOR_BLOCK_ENTITY, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{OUTPUT_SLOT};
        } else {
            return new int[]{INPUT_SLOT};
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir != Direction.DOWN;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.compressCooldown = nbt.getLong("CompressCooldown");
        this.mode = nbt.getInt("CompressMode");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putLong("CompressCooldown", this.compressCooldown);
        nbt.putInt("CompressMode", this.mode);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CompressorBlockEntity blockEntity) {
        --blockEntity.compressCooldown;
        if (!blockEntity.needsCooldown()) {
            blockEntity.setCompressCooldown(0); // Ensure it's not below 0 at this point.
            tryCraft(blockEntity);
        }
    }

    private static void tryCraft(CompressorBlockEntity blockEntity) {
        var inputStack = blockEntity.inventory.get(INPUT_SLOT);
        if (inputStack == null) {
            return; // Silently fail...
        }

        var maybeRecipe = tryGetRecipe(blockEntity);
        if (maybeRecipe.isPresent()) {
            var recipe = maybeRecipe.get().getLeft();
            var inventory = maybeRecipe.get().getRight();

            var result = recipe.craft(inventory, blockEntity.world.getRegistryManager());

            if (result.isEmpty()) {
                return; // Nothing to do...
            }

            var outputStack = blockEntity.inventory.get(OUTPUT_SLOT);

            // TODO: Refactor the below...

            if (outputStack.isEmpty()) {
                blockEntity.inventory.set(OUTPUT_SLOT, result);
                blockEntity.inventory.get(INPUT_SLOT).decrement(inventory.stacks.size());
            }

            if (ItemStack.canCombine(outputStack, result) && outputStack.getMaxCount() >= outputStack.getCount() + result.getCount()) {
                blockEntity.inventory.set(OUTPUT_SLOT, outputStack.copyWithCount(outputStack.getCount() + result.getCount()));
                blockEntity.inventory.get(INPUT_SLOT).decrement(inventory.stacks.size());
            }

            blockEntity.setCompressCooldown(8);
        }
    }

    private static Optional<Pair<CraftingRecipe, SimpleRecipeInputInventory>> tryGetRecipe(CompressorBlockEntity blockEntity) {
        var size = blockEntity.mode == MODE_2x2 ? 4 : 9;

        if (blockEntity.inventory.get(INPUT_SLOT).getCount() >= size) {
            var inventory = blockEntity.createFilledInventoryOf(blockEntity.inventory.get(INPUT_SLOT), size);
            var recipe = blockEntity.world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, blockEntity.world);
            if (!recipe.isEmpty()) {
                return Optional.of(new Pair(recipe.get(), inventory));
            }
        }

        return Optional.empty();
    }

    private SimpleRecipeInputInventory createFilledInventoryOf(ItemStack itemStack, int size) {
        var inventory = new SimpleRecipeInputInventory(size);
        for (int i = 0; i < size; i++) {
            inventory.setStack(i, itemStack.copyWithCount(1));
        }
        return inventory;
    }

    private void setCompressCooldown(int compressCooldown) {
        this.compressCooldown = compressCooldown;
    }

    private boolean needsCooldown() {
        return this.compressCooldown > 0;
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (this.mode < 0) {
            this.mode = 0;
        }
        if (this.mode > 1) {
            this.mode = 1;
        }
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return this;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new CompressorBlockGuiDescription(syncId, inventory, ScreenHandlerContext.create(world, pos));
    }
}

