package dev.magyul.mixin.client.accessor;

import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;
import java.util.List;

@Mixin(ToastManager.class)
public interface ToastManagerAccessor {
    @Accessor
    List<ToastManager.Entry<?>> getVisibleEntries();

    @Accessor
    Deque<Toast> getToastQueue();
}
