package dev.magyul;

import de.maxhenkel.voicechat.voice.client.ClientManager;
import dev.magyul.blocks.ErrorBlock;
import dev.magyul.events.PlayerInteractEvents;
import dev.magyul.network.*;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.registers.MTWItems;
import dev.magyul.util.ClientUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

import static dev.magyul.util.ClientUtil.cs;

public class MTWModClient implements ClientModInitializer {
	public static MTWModClient instance;
	private boolean last100 = false;

	private MinecraftClient client;

	@Override
	public void onInitializeClient() {
		instance = this;
		client = MinecraftClient.getInstance();

//		MTWBlockEntityRenderer.register();
		// net/minecraft/client/gui/EditBox -> MultilineTextField

		ScreenEvents.BEFORE_INIT.register(this::onScreenInit);
		PlayerInteractEvents.LEFT_CLICK_EMPTY.register((player, hand, pos) -> {
			ClientPlayNetworking.send(new AttackAirC2SPacket(pos));
			onClickEmpty(player, hand, false);
		});
		PlayerInteractEvents.RIGHT_CLICK_EMPTY.register((player, hand, pos) -> onClickEmpty(player, hand, true));
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> onClickBlock(player, hand, null));
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onClickBlock(player, hand, hitResult));
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
		ErrorBlock.clientCallback = (stack, context, tooltip, options) -> {
			if (ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
				tooltip.add(Text.literal(""));
				tooltip.add(Text.translatable("item.mtwmod.error_block_light+",
						ClientUtil.getKeyName(GLFW.GLFW_KEY_LEFT_CONTROL),
						ClientUtil.getMouseName(GLFW.GLFW_MOUSE_BUTTON_1)));
				tooltip.add(Text.translatable("item.mtwmod.error_block_light-",
						ClientUtil.getKeyName(GLFW.GLFW_KEY_LEFT_CONTROL),
						ClientUtil.getMouseName(GLFW.GLFW_MOUSE_BUTTON_2)));
			} else {
				tooltip.add(Text.translatable("item.mtwmod.more_info", ClientUtil.getKeyName(GLFW.GLFW_KEY_LEFT_SHIFT)));
			}
			return null;
		};
		CNetwork.register();
	}

	public void onChangeScreen(Screen screen) {
		if (!CocoaInput.initialized) {
			CocoaInput.init(null);
		}
		CocoaInput.distributeScreen(screen);
	}

	private void onTick(MinecraftClient client) {
		ServerPingPong.tick();
		if (client.getNetworkHandler() != null) {
			if (client.options.sneakKey.isPressed()) {
				if (!last100) {
					ClientPlayNetworking.send(new KeyInputC2SPacket(-100, false));
					last100 = true;
				}
			} else {
				if (last100) {
					ClientPlayNetworking.send(new KeyInputC2SPacket(-100, true));
					last100 = false;
				}
			}
		}
		var clientVoicechat = ClientManager.getClient();
		if (clientVoicechat != null) {
			var component = Text.literal("");
			if (ClientUtil.isAllMic()) {
				component.append(Text.translatable("mtwmod.enabled.allmic"));
				if (!ClientUtil.receivedAllMic.isEmpty()) {
					component.append(" | ");
				} else {
					client.inGameHud.setOverlayMessage(component, false);
					return;
				}
			}
			if (ClientUtil.receivedAllMic.isEmpty()) return;
			for (var key : new HashSet<>(ClientUtil.receivedAllMic.keySet())) {
				if (!clientVoicechat.getTalkCache().isTalking(key)) {
					ClientUtil.receivedAllMic.remove(key);
				}
			}
			if (!ClientUtil.receivedAllMic.isEmpty()) {
				var sb = new StringBuilder();
				for (var value : ClientUtil.receivedAllMic.values()) {
					if (!sb.isEmpty()) sb.append(", ");
					sb.append(value);
				}
				component.append(Text.translatable("mtwmod.received.allmic", sb.toString()));
				client.inGameHud.setOverlayMessage(component, false);
			} else {
				client.inGameHud.setOverlayMessage(Text.literal(""), false);
			}
		}
	}

	private void onScreenInit(MinecraftClient client, Screen screen, int width, int height) {
		ScreenEvents.afterTick(screen).register((s) -> {
			if (cs != null) {
				if (cs.cancel) {
					cs = null;
				} else {
					cs.tick();
				}
			}
		});
		ScreenEvents.afterRender(screen).register((s, drawContext, mouseX, mouseY, tickDelta) -> {
			if (cs != null) {
				cs.render();
			}
		});
	}

	private ActionResult onClickBlock(PlayerEntity player, Hand hand, BlockHitResult blockHit) {
		var right = blockHit != null;
		var stack = player.getStackInHand(hand);

		if (player instanceof ClientPlayerEntity) {
			if (ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
				if (stack.isOf(MTWItems.ERROR_BLOCK)) {
					var level = ErrorBlock.nextLightOnStack(player, stack, right);
					ClientPlayNetworking.send(new ErrorBlockUpdateC2SPacket(hand, level));
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}

	private void onClickEmpty(PlayerEntity player, Hand hand, boolean right) {
		if (right) {
			if (hand == Hand.OFF_HAND) {
				hand = Hand.MAIN_HAND;
			} else {
				hand = Hand.OFF_HAND;
			}
			ClientPlayNetworking.send(new UseAirC2SPacket());
		}

		var stack = player.getStackInHand(hand);

		if (ClientUtil.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			if (stack.isOf(MTWItems.ERROR_BLOCK)) {
				var level = ErrorBlock.nextLightOnStack(player, stack, right);
				ClientPlayNetworking.send(new ErrorBlockUpdateC2SPacket(hand, level));
			}
		}
	}
}