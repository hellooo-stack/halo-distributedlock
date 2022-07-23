package site.hellooo.distributedlock.common;

public class StringUtils {
    public static String empty() {
        return "";
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}