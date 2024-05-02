package dev.magyul.blocks.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class SignBoardText {
    public static final Codec<SignBoardText> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TextCodecs.CODEC.fieldOf("message").forGetter(sbt -> sbt.message),
                    TextCodecs.CODEC.optionalFieldOf("filtered_message").forGetter(SignBoardText::getFilteredMessage),
                    DyeColor.CODEC.fieldOf("color").orElse(DyeColor.BLACK).forGetter(sbt -> sbt.color),
                    Codec.BOOL.fieldOf("has_glowing_text").orElse(false).forGetter(sbt -> sbt.glowing))
                    .apply(instance, SignBoardText::create)
    );
    private final Text message;
    private final Text filteredMessage;
    private final DyeColor color;
    private final boolean glowing;
    @Nullable
    private OrderedText orderedMessage;
    private boolean filtered;

    private static Text getDefaultText() {
        return ScreenTexts.EMPTY;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static SignBoardText create(Text message, Optional<Text> filteredMessage, DyeColor color, boolean glowing) {
        return new SignBoardText(message, filteredMessage.orElse(message), color, glowing);
    }

    public SignBoardText() {
        this(getDefaultText(), getDefaultText(), DyeColor.BLACK, false);
    }

    public SignBoardText(Text message, Text filteredMessage, DyeColor color, boolean glowing) {
        this.message = message;
        this.filteredMessage = filteredMessage;
        this.color = color;
        this.glowing = glowing;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public SignBoardText withGlowing(boolean glowing) {
        return glowing == this.glowing ? this : new SignBoardText(message, filteredMessage, color, glowing);
    }

    public DyeColor getColor() {
        return color;
    }

    public SignBoardText withColor(DyeColor color) {
        return color == this.color ? this : new SignBoardText(message, filteredMessage, color, glowing);
    }

    public Text getMessage(boolean filtered) {
        return filtered ? filteredMessage : message;
    }

    public SignBoardText withMessage(Text message) {
        return withMessage(message, message);
    }

    public SignBoardText withMessage(Text message, Text filteredMessage) {
        return new SignBoardText(message, filteredMessage, color, glowing);
    }

    public boolean hasText(PlayerEntity player) {
        return !getMessage(player.shouldFilterText()).getString().isEmpty();
    }

    public OrderedText getOrderedMessage(boolean filtered, Function<Text, OrderedText> messageOrderer) {
        if (this.orderedMessage == null || this.filtered != filtered) {
            this.filtered = filtered;
            this.orderedMessage = messageOrderer.apply(getMessage(filtered));
        }

        return orderedMessage;
    }

    private Optional<Text> getFilteredMessage() {
        if (!filteredMessage.equals(message)) {
            return Optional.of(filteredMessage);
        }

        return Optional.empty();
    }

    public boolean hasRunCommandClickEvent(PlayerEntity player) {
        Text text = getMessage(player.shouldFilterText());
        Style style = text.getStyle();
        ClickEvent clickEvent = style.getClickEvent();
        return clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND;
    }
}
