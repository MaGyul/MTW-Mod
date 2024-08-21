package dev.magyul.blocks.enums;

import net.minecraft.util.StringIdentifiable;

public enum TripleBlockHalf implements StringIdentifiable {
    TOP,
    UPPER,
    LOWER;

    public String toString() {
        return this.asString();
    }

    public String asString() {
        if (this == TOP) return "top";
        return this == UPPER ? "upper" : "lower";
    }
}
