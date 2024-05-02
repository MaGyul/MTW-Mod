package dev.magyul.registers;

import net.minecraft.world.GameRules;

public class MTWGameRules {
    public static GameRules.Key<GameRules.BooleanRule> DO_PICKUP_MODE;

    public static void register(RegisterMethod rm) {
        DO_PICKUP_MODE = rm.register("doPickupMode", GameRules.Category.MISC, GameRules.BooleanRule.create(false));
    }

    public static interface RegisterMethod {
        <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type);
    }
}
