package site.hellooo.distributedlock.common;

public final class ArgChecker {
    public static void check(boolean predicate) {
        if (!predicate) {
            throw new IllegalArgumentException();
        }
    }

    public static void check(boolean predicate, String message) {
        if (!predicate) {
            throw new IllegalArgumentException(stringValue(message));
        }
    }

    public static void check(boolean predicate, String message, Object... values) {
        if (!predicate) {
            throw new IllegalArgumentException(String.format(stringValue(message), values));
        }
    }

    public static <T> void checkNotNull(T ref, String message) {
        if (ref == null) {
            throw new NullPointerException(stringValue(message));
        }
    }

    private static String stringValue(String message) {
        return message == null ? StringUtils.empty() : message;
    }
}
