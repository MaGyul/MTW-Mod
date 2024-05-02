package dev.magyul.blocks.enums;

import net.minecraft.util.StringIdentifiable;

public enum BlockLR implements StringIdentifiable {
    Single,
    Left,
    Middle,
    Right;

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return name().toLowerCase();
    }
}
