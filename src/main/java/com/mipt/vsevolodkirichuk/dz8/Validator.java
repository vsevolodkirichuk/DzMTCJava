package com.mipt.vsevolodkirichuk.dz8;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static ValidationResult validate(Object object) {
        if (object == null) {
            ValidationResult result = new ValidationResult();
            result.addError("Object to validate cannot be null");
            return result;
        }

        ValidationResult result = new ValidationResult();
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(object);

                validateNotNull(field, value, result);
                validateSize(field, value, result);
                validateRange(field, value, result);
                validateEmail(field, value, result);

            } catch (IllegalAccessException e) {
                result.addError("Cannot access field: " + field.getName());
            }
        }

        return result;
    }

    private static void validateNotNull(Field field, Object value, ValidationResult result) {
        if (field.isAnnotationPresent(NotNull.class)) {
            NotNull annotation = field.getAnnotation(NotNull.class);
            if (value == null) {
                result.addError(annotation.message());
            }
        }
    }

    private static void validateSize(Field field, Object value, ValidationResult result) {
        if (field.isAnnotationPresent(Size.class)) {
            Size annotation = field.getAnnotation(Size.class);
            
            if (value == null) {
                return;
            }

            if (value instanceof String) {
                String strValue = (String) value;
                int length = strValue.length();
                if (length < annotation.min() || length > annotation.max()) {
                    result.addError(annotation.message());
                }
            }
        }
    }

    private static void validateRange(Field field, Object value, ValidationResult result) {
        if (field.isAnnotationPresent(Range.class)) {
            Range annotation = field.getAnnotation(Range.class);
            
            if (value == null) {
                return;
            }

            if (value instanceof Number) {
                long numValue = ((Number) value).longValue();
                if (numValue < annotation.min() || numValue > annotation.max()) {
                    result.addError(annotation.message());
                }
            }
        }
    }

    private static void validateEmail(Field field, Object value, ValidationResult result) {
        if (field.isAnnotationPresent(Email.class)) {
            Email annotation = field.getAnnotation(Email.class);
            
            if (value == null) {
                return;
            }

            if (value instanceof String) {
                String email = (String) value;
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    result.addError(annotation.message());
                }
            }
        }
    }
}
