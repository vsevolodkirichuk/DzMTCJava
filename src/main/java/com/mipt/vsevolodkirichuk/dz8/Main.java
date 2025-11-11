package com.mipt.vsevolodkirichuk.dz8;
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Пример 1: Невалидный пользователь ===");
        User user1 = new User();
        user1.setName("A");
        user1.setEmail("invalid-email");
        user1.setAge(200);
        user1.setPassword("123");

        ValidationResult result1 = Validator.validate(user1);
        
        if (!result1.isValid()) {
            System.out.println("Ошибки валидации:");
            result1.getErrors().forEach(error -> System.out.println("  - " + error));
        }

        System.out.println("\n=== Пример 2: Валидный пользователь ===");
        User user2 = new User();
        user2.setName("John Doe");
        user2.setEmail("john.doe@example.com");
        user2.setAge(25);
        user2.setPassword("securepass123");

        ValidationResult result2 = Validator.validate(user2);
        
        if (result2.isValid()) {
            System.out.println("Пользователь валиден!");
        } else {
            System.out.println("Ошибки валидации:");
            result2.getErrors().forEach(error -> System.out.println("  - " + error));
        }

        System.out.println("\n=== Пример 3: Null значения ===");
        User user3 = new User();
        user3.setName(null);
        user3.setEmail(null);

        ValidationResult result3 = Validator.validate(user3);
        
        if (!result3.isValid()) {
            System.out.println("Ошибки валидации:");
            result3.getErrors().forEach(error -> System.out.println("  - " + error));
        }
    }
}
