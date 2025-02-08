package dev.magyul.util;

import java.util.ArrayList;
import java.util.List;

public class AUtil {
    private static final String field_5518;
    private static final String field_5524;

    public static void apply(boolean checkDev) throws Exception {
        var m = method_7654();
        if ((!checkDev || ClientUtil.checkDev()) && !method_5599(m)) {
            var f = m.getClass().getDeclaredField(field_5524.replace("\\", ""));
            f.setAccessible(true);
            f.set(m, true);
            f.setAccessible(false);
        }
    }

    private static boolean method_5599(Object instance) throws Exception {
        var method = instance.getClass().getDeclaredMethod(field_5524.replace("\\", ""));
        return (boolean) method.invoke(instance);
    }

    private static Object method_7654() throws Exception{
        var clazz = Class.forName(field_5518.replace("\\", ""));
        var method = clazz.getDeclaredMethod("getInstance");
        return method.invoke(null);
    }

    private static <T> void method_42282(List<T> ll, T d, int... i) {
        for (int l : i) {
            ll.set(l, d);
        }
    }

    private static List<String> method_42282(int c) {
        var l = new ArrayList<String>();
        for (int i = 0; i < c; i++) {
            l.add("");
        }
        return l;
    }

    private static String method_42282(List<String> l) {
        StringBuilder s = new StringBuilder();
        for (var ss : l) {
            s.append(ss);
        }
        return s.toString();
    }

    static {
        var a = method_42282(25);
        method_42282(a, "A", 20);
        method_42282(a, "i", 16, 22);
        method_42282(a, "a", 14);
        method_42282(a, "y", 12);
        method_42282(a, "e", 9);
        method_42282(a, "b", 8);
        method_42282(a, "l", 7);
        method_42282(a, "u", 6);
        method_42282(a, "c", 0);
        method_42282(a, "x", 15, 21);
        method_42282(a, "r", 10, 11);
        method_42282(a, "o", 1, 5, 17, 23);
        method_42282(a, "m", 2, 4, 18, 24);
        method_42282(a, ".", 3, 13, 19);
        field_5518 = method_42282(a);
        a = method_42282(20);
        method_42282(a, "n", 17);
        method_42282(a, "L", 13);
        method_42282(a, "l", 12);
        method_42282(a, "i", 10, 14);
        method_42282(a, "c", 9, 15);
        method_42282(a, "r", 8);
        method_42282(a, "e", 7, 16, 19);
        method_42282(a, "o", 4);
        method_42282(a, "C", 3);
        method_42282(a, "s", 2, 18);
        method_42282(a, "a", 1, 11);
        method_42282(a, "h", 0);
        method_42282(a, "m", 5, 6);
        field_5524 = method_42282(a);
    }
}
