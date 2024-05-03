package dev.magyul.util;

import net.minecraft.util.math.Vec3i;

public class MathHelper {

    public static long hashCode(Vec3i vec) {
        return hashCode(vec.getX(), vec.getY(), vec.getZ());
    }

    public static long hashCode(int x, int y, int z) {
        long l = (x * 3129871L) ^ (long)z * 116129781L ^ (long)y;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }
}
