package dev.magyul.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.magyul.MTWMod;
import dev.magyul.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegionSecondArgument implements ArgumentType<String> {
    // 바다
    private static final List<String> Ocean = List.of("동해안", "서해안", "남해안", "북해안");
    // 함경도
    private static final List<String> HamgYeongDo = List.of("온성", "경원", "경흥", "종성", "회령", "부령", "경성", "무산", "명천", "길주", "갑산", "단천", "삼수", "장진", "북청", "함흥", "정평", "영흥", "덕원", "안변");
    // 평안도
    private static final List<String> PyeongAnDo = List.of("후주", "강계", "위원", "초산", "희천", "벽동", "창성", "삭주", "의주", "용천", "선천", "구성", "운산", "영변", "정주", "맹산", "개천", "안주", "숙천", "은신", "성천", "양덕", "상원", "평양", "증산", "삼화");
    // 황해도
    private static final List<String> HwangHaeDo = List.of("곡산", "수안", "신계", "토산", "서흥", "평산", "배천", "황주", "봉산", "재령", "해주", "강령", "옹진", "안악", "문화", "은율", "풍천", "장연");
    // 강원도
    private static final List<String> GangWonDo = List.of("이천", "평강", "회양", "고성", "금성", "철원", "춘천", "인제", "양양", "홍천", "강릉", "원주", "영월", "삼척", "울진");
    // 전라도
    private static final List<String> JeolLaDo = List.of("금산", "무주", "전주", "옥구", "임실", "남원", "부안", "흥덕", "담양", "광주", "함평", "순천", "광양", "낙안", "장흥", "나주", "함평", "해남", "진도");
    // 경상도
    private static final List<String> GyeongSangSo = List.of("봉화", "예천", "문경", "안동", "영해", "청송", "의성", "선산", "상주", "인동", "영일", "경주", "대구", "성주", "대구", "거창", "합천", "밍양", "울주", "동래", "김해", "창원", "함안", "고성", "거제", "남해", "진주", "함양", "울릉");
    // 충청도
    private static final List<String> ChungCheongDo = List.of("제천", "단양", "충주", "괴산", "청주", "천안", "보은", "옥천", "영동", "공주", "아산", "예산", "공주", "연산", "부여", "홍주", "당진", "서산", "태안", "보령", "비인", "서천");
    // 경기도
    private static final List<String> GyeonGgiDo = List.of("삭령", "장단", "파주", "포천", "가평", "양주", "한성", "통진", "강화", "부평", "인천", "광주", "용인", "이천", "여주", "안성", "수원", "남양");
    // 제주도
    private static final List<String> JeJuDo = List.of("제주", "대정", "정의");

    private RegionSecondArgument() {
    }

    public static RegionSecondArgument second() {
        return new RegionSecondArgument();
    }

    public static String getSecond(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) {
        return StringUtil.readString(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var first = context.getArgument("first", String.class);
        List<String> result = new ArrayList<>();
        switch (first) {
            case "바다":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), Ocean, result);
                break;
            case "함경도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), HamgYeongDo, result);
                break;
            case "평안도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), PyeongAnDo, result);
                break;
            case "황해도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), HwangHaeDo, result);
                break;
            case "강원도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), GangWonDo, result);
                break;
            case "전라도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), JeolLaDo, result);
                break;
            case "경상도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), GyeongSangSo, result);
                break;
            case "충청도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), ChungCheongDo, result);
                break;
            case "경기도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), GyeonGgiDo, result);
                break;
            case "제주도":
                StringUtil.copyPartialMatches(builder.getRemainingLowerCase(), JeJuDo, result);
                break;
        }
        for (var suggest : result) {
            builder.suggest(suggest);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return null;
    }
}
