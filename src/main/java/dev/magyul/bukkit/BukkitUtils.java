package dev.magyul.bukkit;

import dev.magyul.util.ClassUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BukkitUtils {

    public static boolean hasBukkit() {
        return ClassUtils.hasClass("org.bukkit.Bukkit");
    }

    public static boolean callEvent(@NotNull Event event) {
        if (hasBukkit()) {
            Bukkit.getPluginManager().callEvent(event);
            if (event instanceof Cancellable cancellable) {
                return cancellable.isCancelled();
            }
        }

        return false;
    }

    public static Player convertBukkitPlayer(@NotNull PlayerEntity player) {
        if (ClassUtils.hasClass("net.minecraft.world.entity.player.EntityHuman")) {
            EntityHuman human = EntityHuman.class.cast(player);
            return (Player) human.getBukkitEntity();
        }
        try {
            @SuppressWarnings("JavaReflectionMemberAccess")
            Method method = PlayerEntity.class.getDeclaredMethod("getBukkitEntity");
            method.setAccessible(true);
            try {
                return (Player) method.invoke(player);
            } finally {
                method.setAccessible(false);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static World convertBukkitWorld(@NotNull net.minecraft.world.World world) {
        net.minecraft.world.level.World bukkitWorld = net.minecraft.world.level.World.class.cast(world);
        return bukkitWorld.getWorld();
    }

    public static ItemStack convertBukkitItemStack(@NotNull net.minecraft.item.ItemStack itemStack) {
        if (ClassUtils.hasClass("net.minecraft.world.item.ItemStack")) {
            net.minecraft.world.item.ItemStack bukkitItemStack = net.minecraft.world.item.ItemStack.class.cast(itemStack);
            return CraftItemStack.asBukkitCopy(bukkitItemStack);
        }
        throw new RuntimeException("Please run it in a bukkit environment.");
    }

    public static net.minecraft.item.ItemStack restoreBukkitItemStack(@NotNull ItemStack itemStack) {
        if (ClassUtils.hasClass("net.minecraft.world.item.ItemStack")) {
            return net.minecraft.item.ItemStack.class.cast(CraftItemStack.asNMSCopy(itemStack));
        }
        throw new RuntimeException("Please run it in a bukkit environment.");
    }

    public static org.bukkit.entity.Entity convertBukkitEntity(@NotNull Entity entity) {
        if (ClassUtils.hasClass("net.minecraft.world.entity.Entity")) {
            net.minecraft.world.entity.Entity bukkitEntity = net.minecraft.world.entity.Entity.class.cast(entity);
            return bukkitEntity.getBukkitEntity();
        }
        try {
            @SuppressWarnings("JavaReflectionMemberAccess")
            Method method = Entity.class.getDeclaredMethod("getBukkitEntity");
            method.setAccessible(true);
            return (org.bukkit.entity.Entity) method.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static org.bukkit.entity.Item convertBukkitItem(@NotNull ItemEntity itemEntity) {
        return (org.bukkit.entity.Item) convertBukkitEntity(itemEntity);
    }
}
