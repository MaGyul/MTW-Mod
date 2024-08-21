package dev.magyul.data.generator.lang;

import dev.magyul.registers.MTWEntityType;
import dev.magyul.registers.MTWItems;
import dev.magyul.registers.MTWOther;
import dev.magyul.util.LangUtil;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static dev.magyul.registers.MTWBlocks.*;

public class Korean extends FabricLanguageProvider {
    public Korean(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "ko_kr", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder builder) {
        LangUtil.addItemGroup(builder, MTWOther.ITEM_GROUP_BLOCKS, "건축 자제");
        LangUtil.addItemGroup(builder, MTWOther.ITEM_GROUP_ITEMS, "MTW 아이템");

        // Others
        builder.add("game.title", "천년의 이야기");
        builder.add("disconnect.versionNotMatched", "서버와 클라이언트의 MTW 모드 버전이 일치하지 않습니다. (서버: %s / 클라이언트: %s)");
        builder.add("item.mtwmod.more_info", "§7숨겨진 내용을 보려면 %s§7를 누르세요.");
        builder.add("subtitle.mtwmod.shoji", "쇼지 문 열림");
        builder.add("subtitle.mtwmod.garage", "차고 문 열림");
        var key = "item.mtwmod.error_block_light";
        builder.add(key, "빛 레벨 (%d)");
        builder.add(key + "+", "빛 레벨 +1: %s + %s");
        builder.add(key + "-", "빛 레벨 -1: %s + %s");
        builder.add("disconnected.gui.reconnect", "다시 접속하기");
        builder.add("disconnected.gui.close", "닫기");
        builder.add("reloading.resource", "리소스 리로드 중...");
        builder.add("mtwclient.menu.play", "멕더월 플레이 하기");
        builder.add("mtwmod.enabled.allmic", "전체 마이크 켜짐");
        builder.add("mtwmod.received.allmic", "%s으로 부터 듣는중");

        // Commands
        builder.add("mtwmod.command.allmic", "전체 마이크의 값은 이제 다음과 같습니다: %s");
        builder.add("mtwmod.command.allmic.target", "%s 전체 마이크의 값은 이제 다음과 같습니다: %s");
        builder.add("mtwmod.command.move", "%s 차원으로 이동했습니다.");
        builder.add("mtwmod.command.move.here", "%s 차원의 이동 위치가 [%.2f, %.2f, %.2f]으로 설정되었습니다.");
        builder.add("mtwmod.command.move.here.remove", "%s 차원의 이동 위치가 제거되었습니다.");
        builder.add("mtwmod.command.move.single", "%s을(를) %s 차원으로 이동했습니다.");
        builder.add("mtwmod.command.move.multiple", "%d명의 플레이어를 %s 차원으로 이동했습니다.");
        builder.add("mtwmod.command.customname.current", "%s의 현재 커스텀 이름은 %s입니다.");
        builder.add("mtwmod.command.customname.current.single", "현재 커스텀 이름은 %s입니다.");
        builder.add("mtwmod.command.customname.removed.single", "커스텀 이름이 제거되었습니다.");
        builder.add("mtwmod.command.customname.removed", "%s의 커스텀 이름이 제거되었습니다.");
        builder.add("mtwmod.command.customname.changed.single", "커스텀 이름이 %s으로 변경되었습니다.");
        builder.add("mtwmod.command.customname.changed", "%s의 커스텀 이름이 %s으로 변경되었습니다.");

        // GameRules
        builder.add("gamerule.doPickupMode", "아이템 Carry 혹은 픽업");
        builder.add("gamerule.showRegionInfo", "지역 이동 시 제목 표시");
        builder.add("gamerule.pickupReach", "아이템 Carry 픽업 리치");

        // Items
        builder.add(MTWItems.MTW_ICON, "[MTW] 아이콘");
        builder.add(MTWItems.MTW_REGION_VIEWER, "[MTW] 지역 뷰어");
        builder.add(MTWItems.RING_SWORD, "환도");
        builder.add(MTWItems.INDEPENDENCE_DECLARATION, "독립선언서");

        // Blocks
        builder.add(ERROR_BLOCK, "오류 블록");
        builder.add(SPRUCE_PLATE_DOOR, "판문 §7| 강송");
        builder.add(OAK_KOREAN_PAPER_DOOR, "분합 문 §7| 육송");
        builder.add(OAK_KOREAN_PAPER_WINDOW, "사분합 문 §7| 육송");
        builder.add(EARTH_WALL, "토벽 §7| 강송");
        builder.add(SPRUCE_PLATE_WALL, "판벽 §7| 강송");
        builder.add(SPRUCE_EARTH_WALL_FRAME, "토벽 틀 §7| 강송");
        builder.add(SPRUCE_PLATE_WALL_FRAME, "판벽 틀 §7| 강송");
        builder.add(SPRUCE_LARGE_HALL, "대청 §7| 강송");
        builder.add(SPRUCE_JUCHO_STONE, "추조석 §7| 강송");
        builder.add(SPRUCE_RAFTERS, "서까래 §7| 강송");
        builder.add(SPRUCE_LARGE_HALL_LINOLEUM, "대청 장판 §7| 강송");
        builder.add(SPRUCE_LINOLEUM, "장판 §7| 강송");
        builder.add(SPRUCE_SIGNBOARD, "현판 §7| 강송");
        builder.add(SPRUCE_SHELF, "선반 §7| 강송");
        builder.add(SPRUCE_LATTICE_WINDOW, "살창 §7| 강송");
        builder.add(LINOLEUM_BRICKS, "석재 장판");
        builder.add(ANVIL, "모루");
        builder.add(BLAST_FURNACE, "용광로");
        builder.add(SANDSTONE_LANTERN, "사암 랜턴");
        builder.add(SPRUCE_EARTH_WALL, "토벽 청 §7| 강송");
        builder.add(SPRUCE_WALL_PILLAR, "기둥 §7| 강송");
        builder.add(SPRUCE_PLANKS_CHAIR, "의자 §7| 강송");
        builder.add(OAK_KOREAN_PAPER_SLIDING_DOOR, "사분합 여닫이 문 §7| 육송");
        builder.add(STANDARD, "등잔대 §7| 적송");
        builder.add(FOOD_TABLE, "소반 §7| 적송");
        builder.add(SPRUCE_TILE_CENTER, "기와  중심 §7| 강송");
        builder.add(SPRUCE_HIGH_SLOPE_TILE, "경높 기와 §7| 강송");
        builder.add(SPRUCE_LOW_SLOPE_TILE_BOTTOM, "경낮하 기와 §7| 강송");
        builder.add(SPRUCE_LOW_SLOPE_TILE_TOP, "경낮상 기와 §7| 강송");
        builder.add(SPRUCE_LOW_SLOPE_TILE_END, "경낮끝단 기와 §7| 강송");

        // Entities
        builder.add(MTWEntityType.SIT, "앉기");
    }
}
