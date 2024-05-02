package dev.magyul.blocks.enums;

import net.minecraft.util.StringIdentifiable;

public enum JuchoShape implements StringIdentifiable {
    STRAIGHT("straight"),
    MIDDLE("middle"),
    INNER_LEFT("inner_left"),
    INNER_RIGHT("inner_right"),
    OUTER_LEFT("outer_left"),
    OUTER_RIGHT("outer_right"),
    CLOSED("closed");

    private final String name;

    JuchoShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
