package dev.magyul.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class LivingEntityEvents {
    public static final Event<EquipmentChange> EQUIPMENT_CHANGE = EventFactory.createArrayBacked(EquipmentChange.class,
            (listeners) -> (entity, slot, from, to) -> {
                for (var listener : listeners) {
                    listener.change(entity, slot, from, to);
                }
            });

    public interface EquipmentChange {
        void change(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to);
    }
}
