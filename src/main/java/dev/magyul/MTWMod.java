package dev.magyul;

import dev.magyul.blocks.ErrorBlock;
import dev.magyul.data.PlayerData;
import dev.magyul.data.RegionRoot;
import dev.magyul.data.WorldData;
import dev.magyul.events.LivingEntityEvents;
import dev.magyul.events.PlayerInteractEvents;
import dev.magyul.network.NetworkCodecs;
import dev.magyul.network.SNetwork;
import dev.magyul.registers.*;
import dev.magyul.util.ServerUtil;
import dev.magyul.world.DevelopDimensions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoel.pehkui.api.ScaleTypes;

import static java.lang.String.format;
import static net.minecraft.text.Text.literal;

public class MTWMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(MTWMod.class);
	public static final String ID = "mtwmod";
	public static String VERSION = "1.0.0";

	@Override
	public void onInitialize() {
		for (final var mod : FabricLoader.getInstance().getAllMods()) {
			var metadata = mod.getMetadata();
			if (metadata.getId().equals(MTWMod.ID)) {
				var version = metadata.getVersion();
				LOGGER.info("Found version: {}", version.getFriendlyString());
				VERSION = version.getFriendlyString();
			}
		}

		MTWBlocks.init();
		MTWDataComponentTypes.init();
		MTWItems.init();
		MTWSounds.init();
		MTWTags.init();
		Other.init();
		NetworkCodecs.register();
		SNetwork.register();
		CommandRegistrationCallback.EVENT.register(MTWCommands::register);
		DevelopDimensions.register();

		LivingEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, from, to) -> {
			if (entity instanceof PlayerEntity player) {
				if  (to.isOf(MTWItems.ERROR_BLOCK)) {
					var level = ErrorBlock.getLightOnStack(to);
					player.sendMessage(Text.translatable("item.mtwmod.error_block_light", level), true);
				}
			}
		});
		PlayerInteractEvents.ATTACK_AIR_EVENT.register(this::onAttack);
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> onClickBlock(player, hand, null));
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onClickBlock(player, hand, hitResult));
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof ItemEntity itemEntity) {
				var owner = itemEntity.getOwner();
				var scale = 3f;
				if (owner != null) {
//					var ownerBase = ScaleTypes.BASE.getScaleData(owner);
//					if (ownerBase.getScale() != 1f) {
//						scale *= ownerBase.getScale();
//					}
					var ownerDrops = ScaleTypes.DROPS.getScaleData(owner);
					if (ownerDrops.getScale() != 1f) {
						scale *= ownerDrops.getScale();
					}
				}
				var data = ScaleTypes.BASE.getScaleData(entity);
				data.setScaleTickDelay(0);
				data.setScale(scale);
				data.setScaleTickDelay(data.getScaleType().getDefaultTickDelay());
			}
		});
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> newPlayer.setCustomName(oldPlayer.getCustomName()));
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
	}

	private void onServerStarted(MinecraftServer server) {
	}

	private void onServerStopping(MinecraftServer server) {
	}

	public static ActionResult onCarryUse(ServerPlayerEntity player) {
		var inventory = player.getInventory();
		var stack = PlayerData.getCarryItem(player);
		if (stack.getItem() instanceof BlockItem) {
			if (player.age == PlayerData.lastTick(player)) return ActionResult.PASS;
			player.dropItem(stack, false);
			player.swingHand(Hand.MAIN_HAND, true);

			PlayerData.setCarryItem(player, ItemStack.EMPTY);
			inventory.armor.set(3, ItemStack.EMPTY);
			player.currentScreenHandler.sendContentUpdates();
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	private boolean onAttack(ServerPlayerEntity player) {
		if (ServerUtil.isRegionRootMod(player)) {
			var world = player.getServerWorld();
			var worldData = WorldData.get(world);
			var chunkData = worldData.getChunkData(player.getBlockPos());
			var first = chunkData.getFirst();
			var second = chunkData.getSecond();
			MutableText text = literal("월드(").append(worldData.toString()).append(") ");
			if (first != null && second != null) {
				worldData.setRegionRoot(RegionRoot.fromPlayer(player), first, second);
				text.append("현재 청크");
				text.append(chunkData.toString());
				text.append("의 ");
				text.append(literal(format("%s %s", first, second)).styled(style -> style.withUnderline(true)));
				text.append(literal("의 대표 위치가 현재 위치로 지정되었습니다.").styled(style -> style.withUnderline(false)));
				player.sendMessage(text);
			} else {
				text.append("현재 청크");
				text.append(chunkData.toString());
				text.append("는 지역이 정해져 있지 않습니다.");
				player.sendMessage(text);
			}
			return true;
		}
		return false;
	}

	private ActionResult onClickBlock(PlayerEntity player, Hand hand, @Nullable BlockHitResult blockHit) {
		var right = blockHit != null;

		if (player instanceof ServerPlayerEntity serverPlayer) {
			// FallingBlockEntity.spawnFromBlock(world, pos, carry)
			if (right && serverPlayer.interactionManager.getGameMode() == GameMode.ADVENTURE) {
				return onCarryUse(serverPlayer);
			}
			if (!right && onAttack(serverPlayer)) {
				return ActionResult.SUCCESS;
			}
			if (ServerUtil.isKeyDown(player, GLFW.GLFW_KEY_LEFT_CONTROL)) {
				if (player.getStackInHand(hand).isOf(MTWItems.ERROR_BLOCK)) {
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}