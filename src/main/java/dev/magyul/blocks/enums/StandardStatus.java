package dev.magyul.blocks.enums;

import net.minecraft.util.StringIdentifiable;

public enum StandardStatus implements StringIdentifiable {
    NOTTING,
    OFF,
    ON;

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return name().toLowerCase();
    }
}
