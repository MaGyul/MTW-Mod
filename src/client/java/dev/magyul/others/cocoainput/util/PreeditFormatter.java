package dev.magyul.others.cocoainput.util;

import dev.magyul.util.Tuple3;

public class PreeditFormatter {
    public static final char SECTION = 167; // avoid shift-jis bug...

    public static Tuple3<String, Integer, Boolean> formatMarkedText(String aString, int position1, int length1) {//유틸리티
        StringBuilder builder = new StringBuilder(aString);
        boolean hasCaret = length1 == 0;
        if (!hasCaret) {//주제절이 있다
            builder.insert(position1 + length1, SECTION + "r" + SECTION + "n");//주어 절의 끝에서 수식어를 재설정하고 밑줄 수식어를 설정합니다.
            builder.insert(position1, SECTION + "l");//주어 절의 시작 부분에 굵은 수식어 추가
        } else {//주체설이 없다(캐럿이 존재하기 때문에 그것을 의식한다).
            builder.insert(position1, SECTION + "r" + SECTION + "n");
        }
        builder.insert(0, SECTION + "r" + SECTION + "n");//먼저 밑줄 수식어 설정
        builder.append(SECTION + "r");//마지막에 수식어 재설정
        return new Tuple3<>(new String(builder), position1, hasCaret);
    }
}