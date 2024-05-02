package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow protected abstract int getLineHeight();

    @Shadow private int scrolledLines;
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Unique
    private final List<Long> messageTimestamps = new ArrayList<>();

    @Unique private final float fadeOffsetYScale = 0.8f; // scale * lineHeight
    @Unique private final float fadeTime = 150;

    @Unique private int chatLineIndex;
    @Unique private int chatDisplacementY = 0;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;addedTime()I"))
    private void getChatLineIndex(CallbackInfo ci, @Local(ordinal = 13) int chatLineIndex) {
        this.chatLineIndex = chatLineIndex;
    }

    @Unique
    private void calculateYOffset() {
        try {
            var lineHeight = getLineHeight();
            var maxDisplacement = (float) lineHeight * fadeOffsetYScale;
            var timestamp = messageTimestamps.get(chatLineIndex);
            var timeAlive = System.currentTimeMillis() - timestamp;
            if (chatLineIndex == 0 && timeAlive < fadeTime && scrolledLines == 0) {
                chatDisplacementY = (int)(maxDisplacement - ((timeAlive / fadeTime) * maxDisplacement));
            }
        } catch (Exception ignored) {}
    }

    @ModifyArg(method = "render", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 1))
    private float applyYOffset(float y) {
        calculateYOffset();

        var objectShare = FabricLoader.getInstance().getObjectShare();
        if (objectShare.get("raised:hud") instanceof Integer distance) {
            y -= distance;
        } else if (objectShare.get("raised:distance") instanceof Integer distance) {
            y -= distance;
        }

        return y + chatDisplacementY;
    }

    @ModifyVariable(method = "render", ordinal = 3, at = @At("STORE"))
    private double modifyOpacity(double originalOpacity) {
        double opacity = originalOpacity;
        // 페이드 인 효과를 얻기 위해 현재 렌더링된 선에 필요한 현재 불투명도를 계산합니다.
        try {
            long timestamp = messageTimestamps.get(chatLineIndex);
            long timeAlive = System.currentTimeMillis() - timestamp;
            if (timeAlive < fadeTime && this.scrolledLines == 0) {
                opacity = opacity * (0.5 + MathHelper.clamp(timeAlive/fadeTime, 0, 1)/2);
            }
        } catch (Exception ignored) {}
        return opacity;
    }

    @ModifyVariable(method = "render", at = @At("STORE"))
    private MessageIndicator removeMessageIndicator(MessageIndicator messageIndicator) {
        // 채팅 표시줄이 렌더링되도록 허용하지 않기
        return null;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("TAIL"))
    private void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        messageTimestamps.add(0, System.currentTimeMillis());
        while (messageTimestamps.size() > visibleMessages.size()) {
            messageTimestamps.remove(messageTimestamps.size() - 1);
        }
    }
}
