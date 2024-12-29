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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FoodTableBlockEntity extends BlockEntity implements Clearable, SingleStackInventory {
    private ItemStack foodStack;
    private float placeRotation;
    private final FoodTableAnimationController controller;

    public FoodTableBlockEntity(BlockPos pos, BlockState state) {
        super(MTWBlockEntityType.FOOD_TABLE_BLOCK_ENTITY, pos, state);
        foodStack = ItemStack.EMPTY;
        controller = new FoodTableAnimationController(pos);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("FoodItem", NbtElement.COMPOUND_TYPE)) {
            foodStack = ItemStack.fromNbt(nbt.getCompound("FoodItem"));
        } else {
            foodStack = ItemStack.EMPTY;
        }
        placeRotation = nbt.getFloat("PlaceRotation");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!getStack().isEmpty()) {
            nbt.put("FoodItem", getStack().writeNbt(new NbtCompound()));
        }
        nbt.putFloat("PlaceRotation", placeRotation);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var nbt = super.toInitialChunkDataNbt();
        writeNbt(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public float getPlaceRotation() {
        return placeRotation;
    }

    public void setPlaceRotation(float placeRotation) {
        this.placeRotation = placeRotation;
    }

    public FoodTableAnimationController getController() {
        return controller;
    }

    public boolean hasFood() {
        return !foodStack.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return foodStack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return foodStack.split(amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        foodStack = stack;
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

    public void dropFood() {
        dropFood(getStack());
        foodStack = ItemStack.EMPTY;
    }

    public void dropFood(ItemStack stack) {
        if (world != null && !world.isClient) {
            var pos = getPos();
            if (!stack.isEmpty()) {
                var vec3d = Vec3d.add(pos, .5, 1.01, .5).addRandom(world.random, .7f);
                var entity = new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, stack.copy());
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            }
        }
    }
}
