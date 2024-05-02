package dev.magyul.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class AdventureInventory extends PlayerInventory {
    public static final int ITEM_USAGE_COOLDOWN = 5;
    public static final int MAIN_SIZE = 36;
    private static final int HOTBAR_SIZE = 1;
    public static final int OFF_HAND_SLOT = 40;
    public static final int NOT_FOUND = -1;
    public final DefaultedList<ItemStack> main;
    public final DefaultedList<ItemStack> armor;
    public final DefaultedList<ItemStack> offHand;
    private final List<DefaultedList<ItemStack>> combinedInventory;
    public int selectedSlot;
    public final PlayerEntity player;
    private int changeCount;

    public AdventureInventory(PlayerEntity player) {
        super(player);
        this.main = DefaultedList.ofSize(MAIN_SIZE, ItemStack.EMPTY);
        this.armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
        this.player = player;
    }

    @Override
    public ItemStack getMainHandStack() {
        return isValidHotbarIndex(this.selectedSlot) ? this.main.get(this.selectedSlot) : ItemStack.EMPTY;
    }

    public static int getHotbarSize() {
        return HOTBAR_SIZE;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && ItemStack.canCombine(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    @Override
    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) {
                return i;
            }
        }

        return NOT_FOUND;
    }

    @Override
    public void addPickBlock(ItemStack stack) {
        int i = this.getSlotWithStack(stack);
        if (isValidHotbarIndex(i)) {
            this.selectedSlot = i;
        } else {
            if (i == NOT_FOUND) {
                this.selectedSlot = this.getSwappableHotbarSlot();
                if (!this.main.get(this.selectedSlot).isEmpty()) {
                    int j = this.getEmptySlot();
                    if (j != NOT_FOUND) {
                        this.main.set(j, this.main.get(this.selectedSlot));
                    }
                }

                this.main.set(this.selectedSlot, stack);
            } else {
                this.swapSlotWithHotbar(i);
            }

        }
    }

    @Override
    public void swapSlotWithHotbar(int slot) {
        this.selectedSlot = this.getSwappableHotbarSlot();
        ItemStack itemStack = this.main.get(this.selectedSlot);
        this.main.set(this.selectedSlot, this.main.get(slot));
        this.main.set(slot, itemStack);
    }

    public static boolean isValidHotbarIndex(int slot) {
        return slot >= 0 && slot < HOTBAR_SIZE;
    }

    @Override
    public int getSlotWithStack(ItemStack stack) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty() && ItemStack.canCombine(stack, this.main.get(i))) {
                return i;
            }
        }

        return NOT_FOUND;
    }

    @Override
    public int indexOf(ItemStack stack) {
        for (int i = 0; i < this.main.size(); ++i) {
            ItemStack itemStack = this.main.get(i);
            if (!this.main.get(i).isEmpty() && ItemStack.canCombine(stack, this.main.get(i)) && !this.main.get(i).isDamaged() && !itemStack.hasEnchantments() && !itemStack.hasCustomName()) {
                return i;
            }
        }

        return NOT_FOUND;
    }

    @Override
    public int getSwappableHotbarSlot() {
        int i;
        int j = 0;
        for (i = 0; i < HOTBAR_SIZE; ++i) {
            if (this.main.get(j).isEmpty()) {
                return j;
            }
        }

        for (i = 0; i < HOTBAR_SIZE; ++i) {
            if (!this.main.get(j).hasEnchantments()) {
                return j;
            }
        }

        return this.selectedSlot;
    }

    @Override
    public void scrollInHotbar(double scrollAmount) {
        int i = (int) Math.signum(scrollAmount);

        for (this.selectedSlot -= i; this.selectedSlot < 0; this.selectedSlot += HOTBAR_SIZE) {
        }

        while (this.selectedSlot >= HOTBAR_SIZE) {
            this.selectedSlot -= HOTBAR_SIZE;
        }

    }

    @Override
    public int remove(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory) {
        int i = 0;
        boolean bl = maxCount == 0;
        i += Inventories.remove(this, shouldRemove, maxCount - i, bl);
        i += Inventories.remove(craftingInventory, shouldRemove, maxCount - i, bl);
        ItemStack itemStack = this.player.currentScreenHandler.getCursorStack();
        i += Inventories.remove(itemStack, shouldRemove, maxCount - i, bl);
        if (itemStack.isEmpty()) {
            this.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }

        return i;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == NOT_FOUND) {
            i = this.getEmptySlot();
        }

        return i == NOT_FOUND ? stack.getCount() : this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasNbt()) {
                itemStack.setNbt(stack.getNbt().copy());
            }

            this.setStack(slot, itemStack);
        }

        int j = Math.min(i, itemStack.getMaxCount() - itemStack.getCount());

        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }

        if (j != 0) {
            i -= j;
            itemStack.increment(j);
            itemStack.setBobbingAnimationTime(ITEM_USAGE_COOLDOWN);
        }
        return i;
    }

    @Override
    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(this.selectedSlot), stack)) {
            return this.selectedSlot;
        } else if (this.canStackAddMore(this.getStack(OFF_HAND_SLOT), stack)) {
            return OFF_HAND_SLOT;
        } else {
            for (int i = 0; i < this.main.size(); ++i) {
                if (this.canStackAddMore(this.main.get(i), stack)) {
                    return i;
                }
            }

            return NOT_FOUND;
        }
    }

    @Override
    public void updateItems() {
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (!defaultedList.get(i).isEmpty()) {
                    defaultedList.get(i).inventoryTick(this.player.getWorld(), this.player, i, this.selectedSlot == i);
                }
            }
        }

    }

    @Override
    public boolean insertStack(ItemStack stack) {
        return this.insertStack(NOT_FOUND, stack);
    }

    @Override
    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == NOT_FOUND) {
                        slot = this.getEmptySlot();
                    }

                    if (slot >= 0) {
                        this.main.set(slot, stack.copyAndEmpty());
                        this.main.get(slot).setBobbingAnimationTime(ITEM_USAGE_COOLDOWN);
                        return true;
                    } else if (this.player.getAbilities().creativeMode) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int i;
                    do {
                        i = stack.getCount();
                        if (slot == NOT_FOUND) {
                            stack.setCount(this.addStack(stack));
                        } else {
                            stack.setCount(this.addStack(slot, stack));
                        }
                    } while (!stack.isEmpty() && stack.getCount() < i);

                    if (stack.getCount() == i && this.player.getAbilities().creativeMode) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return stack.getCount() < i;
                    }
                }
            } catch (Throwable var6) {
                CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
                CrashReportSection crashReportSection = crashReport.addElement("Item being added");
                crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
                crashReportSection.add("Item data", stack.getDamage());
                crashReportSection.add("Item name", () -> {
                    return stack.getName().getString();
                });
                throw new CrashException(crashReport);
            }
        }
    }

    @Override
    public void offerOrDrop(ItemStack stack) {
        this.offer(stack, true);
    }

    @Override
    public void offer(ItemStack stack, boolean notifiesClient) {
        while (true) {
            if (!stack.isEmpty()) {
                int i = this.getOccupiedSlotWithRoomForStack(stack);
                if (i == NOT_FOUND) {
                    i = this.getEmptySlot();
                }

                if (i != NOT_FOUND) {
                    int j = stack.getMaxCount() - this.getStack(i).getCount();
                    if (this.insertStack(i, stack.split(j)) && notifiesClient && this.player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) this.player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, i, this.getStack(i)));
                    }
                    continue;
                }

                this.player.dropItem(stack, false);
            }

            return;
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        List<ItemStack> list = null;

        DefaultedList<ItemStack> defaultedList;
        for (Iterator<DefaultedList<ItemStack>> var4 = this.combinedInventory.iterator(); var4.hasNext(); slot -= defaultedList.size()) {
            defaultedList = var4.next();
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
        }

        return list != null && !list.get(slot).isEmpty() ? Inventories.splitStack(list, slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public void removeOne(ItemStack stack) {
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i) == stack) {
                    defaultedList.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack removeStack(int slot) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList<ItemStack> defaultedList2;
        for (Iterator<DefaultedList<ItemStack>> var3 = this.combinedInventory.iterator(); var3.hasNext(); slot -= defaultedList2.size()) {
            defaultedList2 = var3.next();
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }

        if (defaultedList != null && !defaultedList.get(slot).isEmpty()) {
            ItemStack itemStack = defaultedList.get(slot);
            defaultedList.set(slot, ItemStack.EMPTY);
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList<ItemStack> defaultedList2;
        for (Iterator<DefaultedList<ItemStack>> var4 = this.combinedInventory.iterator(); var4.hasNext(); slot -= defaultedList2.size()) {
            defaultedList2 = var4.next();
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }

        if (defaultedList != null) {
            defaultedList.set(slot, stack);
        }

    }

    @Override
    public float getBlockBreakingSpeed(BlockState block) {
        return this.main.get(this.selectedSlot).getMiningSpeedMultiplier(block);
    }

    @Override
    public NbtList writeNbt(NbtList nbtList) {
        int i;
        NbtCompound nbtCompound;
        for (i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty()) {
                nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte) i);
                this.main.get(i).writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        for (i = 0; i < this.armor.size(); ++i) {
            if (!this.armor.get(i).isEmpty()) {
                nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte) (i + 100));
                this.armor.get(i).writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        for (i = 0; i < this.offHand.size(); ++i) {
            if (!this.offHand.get(i).isEmpty()) {
                nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte) (i + 150));
                this.offHand.get(i).writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        return nbtList;
    }

    @Override
    public void readNbt(NbtList nbtList) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();

        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                if (j < this.main.size()) {
                    this.main.set(j, itemStack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemStack);
                } else if (j >= 150 && j < this.offHand.size() + 150) {
                    this.offHand.set(j - 150, itemStack);
                }
            }
        }

    }

    @Override
    public int size() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.main.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                var1 = this.armor.iterator();

                do {
                    if (!var1.hasNext()) {
                        var1 = this.offHand.iterator();

                        do {
                            if (!var1.hasNext()) {
                                return true;
                            }

                            itemStack = var1.next();
                        } while (itemStack.isEmpty());

                        return false;
                    }

                    itemStack = var1.next();
                } while (itemStack.isEmpty());

                return false;
            }

            itemStack = var1.next();
        } while (itemStack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        List<ItemStack> list = null;

        DefaultedList<ItemStack> defaultedList;
        for (Iterator<DefaultedList<ItemStack>> var3 = this.combinedInventory.iterator(); var3.hasNext(); slot -= defaultedList.size()) {
            defaultedList = var3.next();
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
        }

        return list == null ? ItemStack.EMPTY : list.get(slot);
    }

    @Override
    public Text getName() {
        return Text.translatable("container.inventory");
    }

    @Override
    public ItemStack getArmorStack(int slot) {
        return this.armor.get(slot);
    }

    @Override
    public void damageArmor(DamageSource damageSource, float amount, int[] slots) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F) {
                amount = 1.0F;
            }

            for (int i : slots) {
                ItemStack itemStack = this.armor.get(i);
                if ((!damageSource.isIn(DamageTypeTags.IS_FIRE) || !itemStack.getItem().isFireproof()) && itemStack.getItem() instanceof ArmorItem) {
                    itemStack.damage((int) amount, this.player, (player) -> {
                        player.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i));
                    });
                }
            }

        }
    }

    @Override
    public void dropAll() {
        for (DefaultedList<ItemStack> list : this.combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);
                if (!itemStack.isEmpty()) {
                    this.player.dropItem(itemStack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void markDirty() {
        ++this.changeCount;
    }

    @Override
    public int getChangeCount() {
        return this.changeCount;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.player.isRemoved()) {
            return false;
        } else {
            return !(player.squaredDistanceTo(this.player) > 64.0);
        }
    }

    @Override
    public boolean contains(ItemStack stack) {
        for (DefaultedList<ItemStack> list : this.combinedInventory) {
            for (ItemStack itemStack : list) {
                if (!itemStack.isEmpty() && ItemStack.canCombine(itemStack, stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean contains(TagKey<Item> tag) {
        for (DefaultedList<ItemStack> list : this.combinedInventory) {
            for (ItemStack itemStack : list) {
                if (!itemStack.isEmpty() && itemStack.isIn(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void clone(net.minecraft.entity.player.PlayerInventory other) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, other.getStack(i));
        }

        this.selectedSlot = other.selectedSlot;
    }

    @Override
    public void clear() {
        for (DefaultedList<ItemStack> list : this.combinedInventory) {
            list.clear();
        }
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        for (ItemStack itemStack : this.main) {
            finder.addUnenchantedInput(itemStack);
        }
    }

    @Override
    public ItemStack dropSelectedItem(boolean entireStack) {
        ItemStack itemStack = this.getMainHandStack();
        return itemStack.isEmpty() ? ItemStack.EMPTY : this.removeStack(this.selectedSlot, entireStack ? itemStack.getCount() : 1);
    }
}
