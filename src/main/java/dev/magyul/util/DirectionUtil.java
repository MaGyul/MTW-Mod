package dev.magyul.util;

import net.minecraft.util.math.Direction;

public class DirectionUtil {

    public static Direction getLeft(Direction facing) {
        var dir = getLeftNull(facing);
        if (dir != null) {
            return dir;
        }

        return facing;
    }

    public static Direction getLeftNull(Direction facing) {
        if (facing == Direction.NORTH)  {
            return Direction.WEST;
        } else if (facing == Direction.WEST) {
            return Direction.SOUTH;
        } else if (facing == Direction.SOUTH) {
            return Direction.EAST;
        } else if (facing == Direction.EAST) {
            return Direction.NORTH;
        }

        return null;
    }

    public static Direction getRight(Direction facing) {
        var dir = getRightNull(facing);
        if (dir != null) {
            return dir;
        }

        return facing;
    }

    public static Direction getRightNull(Direction facing) {
        if (facing == Direction.NORTH)  {
            return Direction.EAST;
        } else if (facing == Direction.WEST) {
            return Direction.NORTH;
        } else if (facing == Direction.SOUTH) {
            return Direction.WEST;
        } else if (facing == Direction.EAST) {
            return Direction.SOUTH;
        }

        return null;
    }
}
