package site.hellooo.distributedlock.core.common;

public class ClassUtils {
    public static <O> String getObjClassName(O object) {
        if (object == null) {
            return "null";
        } else {
            Class<?> clazz = object.getClass();
            return clazz.getName();
        }
    }
}
