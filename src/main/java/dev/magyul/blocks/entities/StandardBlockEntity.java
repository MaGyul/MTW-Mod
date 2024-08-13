package dev.magyul.blocks.entities;

import dev.magyul.registers.MTWBlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class StandardBlockEntity extends BlockEntity implements Clearable, SingleStackInventory.SingleStackBlockEntityInventory {
    private ItemStack lanternStack;

    public StandardBlockEntity(BlockPos pos, BlockState state) {
        super(MTWBlockEntityType.STANDARD_BLOCK_ENTITY, pos, state);
        lanternStack = ItemStack.EMPTY;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("LanternItem", NbtElement.COMPOUND_TYPE)) {
            lanternStack = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound("LanternItem"));
        } else {
            lanternStack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!getStack().isEmpty()) {
            nbt.put("LanternItem", getStack().encode(registryLookup));
        }
    }

    @Override
    public ItemStack getStack() {
        return lanternStack;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.lanternStack = stack;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
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
                emptyStack();
                var vec3d = Vec3d.add(pos, .5, 1.01, .5).addRandom(world.random, .7f);
                var entity = new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, clone);
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            }
        }
    }
}
