package dev.magyul.util;

import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Vec3dUtil {

    public static NbtList toNbtList(Vec3d vec3d) {
        var nbtList = new NbtList();
        nbtList.add(NbtDouble.of(vec3d.x));
        nbtList.add(NbtDouble.of(vec3d.y));
        nbtList.add(NbtDouble.of(vec3d.z));
        return nbtList;
    }

    public static Vec3d fromNbtList(NbtList nbtList) {
        var x = MathHelper.clamp(nbtList.getDouble(0), -3.0000512E7, 3.0000512E7);
        var y = MathHelper.clamp(nbtList.getDouble(1), -2.0E7, 2.0E7);
        var z = MathHelper.clamp(nbtList.getDouble(2), -3.0000512E7, 3.0000512E7);
        return new Vec3d(x, y, z);
    }
}
