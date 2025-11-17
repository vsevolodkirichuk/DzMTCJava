package com.mipt.vsevolodkirichuk.dz11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class ValidationDecoratorTest {
    private DataService baseService;
    private ValidationDecorator validationDecorator;

    @BeforeEach
    void setUp() {
        baseService = new SimpleDataService();
        validationDecorator = new ValidationDecorator(baseService);
    }

    @Test
    void testValidSaveData() {
        assertDoesNotThrow(() -> validationDecorator.saveData("key1", "value1"));
        Optional<String> result = baseService.findDataByKey("key1");
        assertEquals("value1", result.orElse(null));
    }

    @Test
    void testValidFindData() {
        baseService.saveData("key1", "value1");
        assertDoesNotThrow(() -> validationDecorator.findDataByKey("key1"));
    }

    @Test
    void testValidDeleteData() {
        baseService.saveData("key1", "value1");
        assertDoesNotThrow(() -> validationDecorator.deleteData("key1"));
    }

    @Test
    void testSaveDataWithNullKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.saveData(null, "value1")
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveDataWithEmptyKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.saveData("", "value1")
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveDataWithWhitespaceKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.saveData("   ", "value1")
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveDataWithNullData() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.saveData("key1", null)
        );
        assertEquals("Data cannot be null", exception.getMessage());
    }

    @Test
    void testFindDataWithNullKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.findDataByKey(null)
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testFindDataWithEmptyKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.findDataByKey("")
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testDeleteDataWithNullKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.deleteData(null)
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testDeleteDataWithEmptyKey() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationDecorator.deleteData("")
        );
        assertEquals("Key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testSaveDataWithEmptyStringValue() {
        assertDoesNotThrow(() -> validationDecorator.saveData("key1", ""));
        Optional<String> result = baseService.findDataByKey("key1");
        assertEquals("", result.orElse(null));
    }

    @Test
    void testMultipleValidOperations() {
        validationDecorator.saveData("key1", "value1");
        validationDecorator.saveData("key2", "value2");
        
        assertEquals("value1", validationDecorator.findDataByKey("key1").orElse(null));
        assertEquals("value2", validationDecorator.findDataByKey("key2").orElse(null));
        
        assertTrue(validationDecorator.deleteData("key1"));
        assertFalse(validationDecorator.findDataByKey("key1").isPresent());
    }
}
