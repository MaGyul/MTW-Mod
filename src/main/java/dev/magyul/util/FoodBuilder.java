package dev.magyul.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerConstants;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class FoodBuilder {
    private int nutrition;
    private float saturationModifier;
    private boolean canAlwaysEat;
    private float eatSeconds = 1.6F;
    private Optional<ItemStack> usingConvertsTo = Optional.empty();
    private final ImmutableList.Builder<FoodComponent.StatusEffectEntry> effects = ImmutableList.builder();

    public FoodBuilder() {
    }

    public FoodBuilder nutrition(int nutrition) {
        this.nutrition = nutrition;
        return this;
    }

    public FoodBuilder saturationModifier(float saturationModifier) {
        this.saturationModifier = saturationModifier;
        return this;
    }

    public FoodBuilder alwaysEdible() {
        this.canAlwaysEat = true;
        return this;
    }

    public FoodBuilder eatSeconds(float eatSeconds) {
        this.eatSeconds = eatSeconds;
        return this;
    }

    public FoodBuilder snack() {
        this.eatSeconds = 0.8F;
        return this;
    }

    public FoodBuilder statusEffect(StatusEffectInstance effect, float chance) {
        this.effects.add(new FoodComponent.StatusEffectEntry(effect, chance));
        return this;
    }

    public FoodBuilder usingConvertsTo(ItemConvertible item) {
        this.usingConvertsTo = Optional.of(new ItemStack(item));
        return this;
    }

    public FoodComponent build() {
        float f = HungerConstants.calculateSaturation(this.nutrition, this.saturationModifier);
        return new FoodComponent(this.nutrition, f, this.canAlwaysEat, this.eatSeconds, this.usingConvertsTo, this.effects.build());
    }
}
