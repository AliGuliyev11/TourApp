package com.mycode.tourapptelegrambot.checkTypes;

public class TypeCheck {
    public static boolean isPrimitive(Class<?> type) {
        return (type == int.class || type == long.class || type == double.class || type == float.class
                || type == boolean.class || type == byte.class || type == char.class || type == short.class);
    }

    public static Object boxPrimitiveClass(Class<?> type, String text) {
        if (type == int.class) {

            return Integer.valueOf(text);
        } else if (type == long.class) {
            return Long.valueOf(text);
        } else if (type == double.class) {
            return Double.valueOf(text);
        } else if (type == float.class) {
            return Float.valueOf(text);
        } else if (type == byte.class) {
            return Byte.valueOf(text);
        } else if (type == short.class) {
            return Short.valueOf(text);
        } else {
            String string = "class '" + type.getName() + "' is not a primitive";
            throw new IllegalArgumentException(string);
        }
    }
}
