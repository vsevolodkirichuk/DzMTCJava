package com.mipt.vsevolodkirichuk.dz11;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Пример 1: Все декораторы вместе ===");
        DataService service = new ValidationDecorator(
            new MetricableDecorator(
                new LoggingDecorator(
                    new CachingDecorator(
                        new SimpleDataService()
                    )
                )
            )
        );

        service.saveData("key", "data");
        System.out.println();

        Optional<String> data = service.findDataByKey("key");
        System.out.println("Result: " + data.orElse("not found"));
        System.out.println();

        service.deleteData("key");
        System.out.println();

        Optional<String> noData = service.findDataByKey("key");
        System.out.println("Result after delete: " + noData.orElse("not found"));
        System.out.println();

        System.out.println("=== Пример 2: Только кеширование ===");
        DataService cachedService = new CachingDecorator(new SimpleDataService());
        cachedService.saveData("user1", "John");
        System.out.println("First call: " + cachedService.findDataByKey("user1").orElse("not found"));
        System.out.println("Second call (from cache): " + cachedService.findDataByKey("user1").orElse("not found"));
        System.out.println();

        System.out.println("=== Пример 3: Валидация ошибок ===");
        DataService validatedService = new ValidationDecorator(new SimpleDataService());
        try {
            validatedService.saveData(null, "data");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught validation error: " + e.getMessage());
        }

        try {
            validatedService.saveData("key", null);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught validation error: " + e.getMessage());
        }
    }
}
