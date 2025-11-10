package com.mipt.vsevolodkirichuk.dz8;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void testValidUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAge(25);
        user.setPassword("password123");

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testNotNullValidation() {
        User user = new User();
        user.setName(null);
        user.setEmail(null);
        user.setAge(25);
        user.setPassword("password123");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().contains("Имя не может быть null"));
        assertTrue(result.getErrors().contains("Email не может быть null"));
    }

    @Test
    void testSizeValidationTooShort() {
        User user = new User();
        user.setName("A");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setPassword("123");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Имя должно быть от 2 до 50 символов"));
        assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));
    }

    @Test
    void testSizeValidationTooLong() {
        User user = new User();
        user.setName("A".repeat(51));
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setPassword("A".repeat(21));

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Имя должно быть от 2 до 50 символов"));
        assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));
    }

    @Test
    void testSizeValidationBoundary() {
        User user = new User();
        user.setName("AB");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setPassword("123456");

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());
    }

    @Test
    void testRangeValidationTooLow() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setAge(-1);
        user.setPassword("password");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));
    }

    @Test
    void testRangeValidationTooHigh() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setAge(151);
        user.setPassword("password");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));
    }

    @Test
    void testRangeValidationBoundary() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setAge(0);
        user.setPassword("password");

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());

        user.setAge(150);
        result = Validator.validate(user);

        assertTrue(result.isValid());
    }

    @Test
    void testEmailValidation() {
        User user = new User();
        user.setName("John");
        user.setEmail("invalid-email");
        user.setAge(25);
        user.setPassword("password");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Некорректный формат email"));
    }

    @Test
    void testValidEmails() {
        User user = new User();
        user.setName("John");
        user.setAge(25);
        user.setPassword("password");

        String[] validEmails = {
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "test123@test-domain.com"
        };

        for (String email : validEmails) {
            user.setEmail(email);
            ValidationResult result = Validator.validate(user);
            assertTrue(result.isValid(), "Email should be valid: " + email);
        }
    }

    @Test
    void testInvalidEmails() {
        User user = new User();
        user.setName("John");
        user.setAge(25);
        user.setPassword("password");

        String[] invalidEmails = {
            "invalid",
            "@example.com",
            "test@",
            "test@@example.com",
            "test@example",
            "test example@test.com"
        };

        for (String email : invalidEmails) {
            user.setEmail(email);
            ValidationResult result = Validator.validate(user);
            assertFalse(result.isValid(), "Email should be invalid: " + email);
        }
    }

    @Test
    void testMultipleErrors() {
        User user = new User();
        user.setName("A");
        user.setEmail("invalid");
        user.setAge(200);
        user.setPassword("123");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertEquals(4, result.getErrors().size());
    }

    @Test
    void testNullObject() {
        ValidationResult result = Validator.validate(null);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("null"));
    }

    @Test
    void testNullFieldsDoNotTriggerSizeOrRange() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setAge(null);
        user.setPassword(null);

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());
    }

    @Test
    void testEmptyUser() {
        User user = new User();

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    void testValidationResultAddError() {
        ValidationResult result = new ValidationResult();
        assertTrue(result.isValid());

        result.addError("Error 1");
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        result.addError("Error 2");
        assertEquals(2, result.getErrors().size());
    }

    @Test
    void testValidationResultImmutableErrors() {
        ValidationResult result = new ValidationResult();
        result.addError("Error");

        assertThrows(UnsupportedOperationException.class, () -> {
            result.getErrors().add("Another error");
        });
    }
}
