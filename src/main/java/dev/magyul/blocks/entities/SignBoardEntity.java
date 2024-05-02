package dev.magyul.blocks.entities;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import dev.magyul.registers.MTWBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class SignBoardEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TEXT_LINE_HEIGHT = 1;
    @Nullable
    private UUID editor;
    private SignBoardText text;
    private boolean waxed;

    public SignBoardEntity(BlockPos pos, BlockState state) {
        this(MTWBlockEntities.SIGN_BOARD, pos, state);
    }

    public SignBoardEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.text = createText();
    }

    protected SignBoardText createText() {
        return new SignBoardText();
    }

    public SignBoardText getText() {
        return text;
    }

    public int getTextLineHeight() {
        return TEXT_LINE_HEIGHT;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var textResult = SignBoardText.CODEC.encodeStart(NbtOps.INSTANCE, text);
        textResult.resultOrPartial(LOGGER::error).ifPresent(text -> nbt.put("text", text));
        nbt.putBoolean("is_waxed", waxed);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("text")) {
            var textResult = SignBoardText.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("text"));
            textResult.resultOrPartial(LOGGER::error).ifPresent(sbt -> text = parseText(sbt));
        }

        waxed = nbt.getBoolean("is_waxed");
    }

    private SignBoardText parseText(SignBoardText sbt) {
        var message = parseText(sbt.getMessage(false));
        var filteredMessage = parseText(sbt.getMessage(true));
        return sbt.withMessage(message, filteredMessage);
    }

    private Text parseText(Text text) {
        if (world instanceof ServerWorld serverWorld) {
            try {
                return Texts.parse(createCommandSource(null, serverWorld, pos), text, null, 0);
            } catch (CommandSyntaxException ignored) {}
        }

        return text;
    }

    public void tryChangeText(PlayerEntity player, FilteredMessage message) {
        if (!isWaxed() && player.getUuid().equals(getEditor()) && world != null) {
            changeText(text -> getTextWithMessage(player, message, text));
            setEditor(null);
            world.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        } else {
            LOGGER.warn("Player {} just tried to change non-editable sign", player.getName().getString());
        }
    }

    public boolean changeText(UnaryOperator<SignBoardText> textChanger) {
        return setText(textChanger.apply(getText()));
    }

    private SignBoardText getTextWithMessage(PlayerEntity player, FilteredMessage filteredMessage, SignBoardText text) {
        var style = text.getMessage(player.shouldFilterText()).getStyle();
        if (player.shouldFilterText()) {
            text = text.withMessage(Text.literal(filteredMessage.getString()).setStyle(style));
        } else {
            text = text.withMessage(Text.literal(filteredMessage.raw()).setStyle(style), Text.literal(filteredMessage.getString()).setStyle(style));
        }

        return text;
    }

    public boolean setText(SignBoardText text) {
        if (text != this.text) {
            this.text = text;
            updateListeners();
            return true;
        } else {
            return false;
        }
    }

    public boolean canRunCommandClickEvent(PlayerEntity player) {
        return isWaxed() && getText().hasRunCommandClickEvent(player);
    }

    public boolean runCommandClickEvent(PlayerEntity player, World world, BlockPos pos) {
        boolean result = false;
        var text = getText().getMessage(player.shouldFilterText());
        var style = text.getStyle();
        var clickEvent = style.getClickEvent();
        if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            Objects.requireNonNull(player.getServer())
                    .getCommandManager().executeWithPrefix(createCommandSource(player, world, pos), clickEvent.getValue());
            result = true;
        }

        return result;
    }

    private static ServerCommandSource createCommandSource(@Nullable PlayerEntity player, World world, BlockPos pos) {
        var string = player == null ? "Sign" : player.getName().getString();
        var text = player == null ? Text.literal("Sign") : player.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(pos), Vec2f.ZERO, (ServerWorld) world, 2, string, text, world.getServer(), player);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public void setEditor(@Nullable UUID editor) {
        this.editor = editor;
    }

    @Nullable
    public UUID getEditor() {
        return editor;
    }

    private void updateListeners() {
        markDirty();
        if (world != null) {
            world.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    public boolean isWaxed() {
        return waxed;
    }

    public boolean setWaxed(boolean waxed) {
        if (this.waxed != waxed) {
            this.waxed = waxed;
            updateListeners();
            return true;
        } else {
            return false;
        }
    }

    public boolean isPlayerTooFarToEdit(World world, UUID uuid) {
        var playerEntity = world.getPlayerByUuid(uuid);
        return playerEntity == null || playerEntity.squaredDistanceTo(getPos().getX(), getPos().getY(), getPos().getZ()) > 64;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SignBoardEntity boardEntity) {
        var uuid = boardEntity.getEditor();
        if (uuid != null) {
            boardEntity.tryClearInvalidEditor(boardEntity, world, uuid);
        }
    }

    private void tryClearInvalidEditor(SignBoardEntity boardEntity, World world, UUID uuid) {
        if (boardEntity.isPlayerTooFarToEdit(world, uuid)) {
            boardEntity.setEditor(null);
        }
    }

    public SoundEvent getInteractionFailSound() {
        return SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL;
    }
}
