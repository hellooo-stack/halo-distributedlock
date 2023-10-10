package site.hellooo.distributedlock.core.common;

public class StringUtils {
    public static String empty() {
        return "";
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmpty(CharSequence charSequence) {
        return !isEmpty(charSequence);
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
}
