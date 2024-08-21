package dev.magyul.registers;

import dev.magyul.MTWMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class MTWSounds {
    public static SoundEvent GARAGE = register("block.garage");
    public static SoundEvent SHOJI = register("block.shoji");

    private static SoundEvent register(String name) {
        var id = Identifier.of(MTWMod.ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init() {
    }
}
