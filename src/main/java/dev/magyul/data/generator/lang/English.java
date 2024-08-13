package dev.magyul.data.generator.lang;

import dev.magyul.registers.MTWBlocks;
import dev.magyul.registers.MTWEntityType;
import dev.magyul.registers.MTWItems;
import dev.magyul.registers.MTWOther;
import dev.magyul.util.LangUtil;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

import java.util.concurrent.CompletableFuture;

import static dev.magyul.registers.MTWBlocks.*;

public class English extends FabricLanguageProvider {
    public English(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder builder) {
        LangUtil.addItemGroup(builder, MTWOther.ITEM_GROUP, "Building materials");

        // Others
        builder.add("game.title", "Cheonnyeon Story");
        builder.add("disconnect.versionNotMatched", "The MTW mod versions on the server and client don't match. (Server: %s / Client: %s)");
        builder.add("item.mtwmod.more_info", "§7Press %s §7to see the hidden content.");
        builder.add("subtitle.mtwmod.shoji", "Open Shoji Door");
        builder.add("subtitle.mtwmod.garage", "Open Garage Door");
        var key = "item.mtwmod.error_block_light";
        builder.add(key, "Light Level (%d)");
        builder.add(key + "+", "Light value +1: %s + %s");
        builder.add(key + "-", "Light value -1: %s + %s");
        builder.add("disconnected.gui.reconnect", "Reconnect");
        builder.add("disconnected.gui.close", "Close");
        builder.add("reloading.resource", "Reloading resources...");
        builder.add("mtwclient.menu.play", "Play MTW");
        builder.add("mtwmod.menu.game", "Make The World | Version %s");
        builder.add("mtwmod.enabled.allmic", "Overall Mic on");
        builder.add("mtwmod.received.allmic", "Listening to %s");

        // Commands
        builder.add("mtwmod.command.allmic", "Overall Mic is now set to: %s");
        builder.add("mtwmod.command.allmic.target", "%s Overall Mic is now set to: %s");
        builder.add("mtwmod.command.move", "Moved to %s dimension.");
        builder.add("mtwmod.command.move.here", "The move position for dimension %s was set to [%.2f, %.2f, %.2f].");
        builder.add("mtwmod.command.move.here.remove", "The move position for dimension %s was removed.");
        builder.add("mtwmod.command.move.single", "Moved %s to %s dimension.");
        builder.add("mtwmod.command.move.multiple", "Moved %d players to %s dimension.");
        builder.add("mtwmod.command.customname.current", "The current custom name for %s is %s.");
        builder.add("mtwmod.command.customname.current.single", "The current custom name is %s.");
        builder.add("mtwmod.command.customname.removed.single", "The custom name has been removed.");
        builder.add("mtwmod.command.customname.removed", "The custom name of %s was removed.");
        builder.add("mtwmod.command.customname.changed.single", "The custom name was changed to %s.");
        builder.add("mtwmod.command.customname.changed", "The custom name of %s was changed to %s.");

        // GameRules
        builder.add("gamerule.doPickupMode", "Carry or pick up items");
        builder.add("gamerule.showRegionInfo", "Show titles when moving regions");
        builder.add("gamerule.pickupReach", "Item Carry Pickup Reach");

        // Items
        builder.add(MTWItems.MTW_ICON, "[MTW] Icon");
        builder.add(MTWItems.MTW_REGION_VIEWER, "[MTW] Region Viewer");

        // Blocks
        for (Block block : MTW_BLOCKS) {
            addBlock(builder, block);
        }

        // Entities
        builder.add(MTWEntityType.SIT, "Sit Entity");
    }

    private void addBlock(TranslationBuilder builder, Block block) {
        builder.add(block, LangUtil.block2lang(block));
    }
}
