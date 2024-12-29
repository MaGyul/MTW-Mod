package dev.magyul.blocks.entities;

import dev.magyul.registers.MTWBlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class StandardBlockEntity extends BlockEntity implements Clearable, SingleStackInventory {
    private ItemStack lanternStack;

    public StandardBlockEntity(BlockPos pos, BlockState state) {
        super(MTWBlockEntityType.STANDARD_BLOCK_ENTITY, pos, state);
        lanternStack = ItemStack.EMPTY;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("LanternItem", NbtElement.COMPOUND_TYPE)) {
            lanternStack = ItemStack.fromNbt(nbt.getCompound("LanternItem"));
        } else {
            lanternStack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!getStack().isEmpty()) {
            nbt.put("LanternItem", getStack().writeNbt(new NbtCompound()));
        }
    }

    @Override
    public ItemStack getStack(int slot) {
        return lanternStack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return lanternStack.split(amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.lanternStack = stack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return getStack(slot).isEmpty();
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return hopperInventory.containsAny(ItemStack::isEmpty);
    }

    public void drop() {
        if (world != null && !world.isClient) {
            var pos = getPos();
            var stack = getStack();
            if (!stack.isEmpty()) {
                var clone = stack.copy();
                lanternStack = ItemStack.EMPTY;
                var vec3d = Vec3d.add(pos, .5, 1.01, .5).addRandom(world.random, .7f);
                var entity = new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, clone);
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            }
        }
    }
}
