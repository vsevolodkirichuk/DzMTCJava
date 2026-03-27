package com.mipt.vsevolodkirichuk.dz11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class LoggingDecoratorTest {
    private DataService baseService;
    private LoggingDecorator loggingDecorator;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        baseService = new SimpleDataService();
        loggingDecorator = new LoggingDecorator(baseService);
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testFindDataLogging() {
        baseService.saveData("key1", "value1");
        loggingDecorator.findDataByKey("key1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Finding data by key: key1"));
        assertTrue(output.contains("Found data: value1"));
    }

    @Test
    void testSaveDataLogging() {
        loggingDecorator.saveData("key1", "value1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Saving data: key=key1, data=value1"));
        assertTrue(output.contains("Data saved successfully"));
    }

    @Test
    void testDeleteDataLogging() {
        baseService.saveData("key1", "value1");
        loggingDecorator.deleteData("key1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Deleting data by key: key1"));
        assertTrue(output.contains("Delete result: true"));
    }

    @Test
    void testFindNonExistentKeyLogging() {
        loggingDecorator.findDataByKey("nonexistent");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Finding data by key: nonexistent"));
        assertTrue(output.contains("Found data: null"));
    }

    @Test
    void testDeleteNonExistentKeyLogging() {
        loggingDecorator.deleteData("nonexistent");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Deleting data by key: nonexistent"));
        assertTrue(output.contains("Delete result: false"));
    }

    @Test
    void testMultipleOperationsLogging() {
        loggingDecorator.saveData("key1", "value1");
        loggingDecorator.findDataByKey("key1");
        loggingDecorator.deleteData("key1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Saving data"));
        assertTrue(output.contains("Finding data"));
        assertTrue(output.contains("Deleting data"));
    }

    @Test
    void testActualDataOperations() {
        loggingDecorator.saveData("key1", "value1");
        Optional<String> result = loggingDecorator.findDataByKey("key1");
        
        assertEquals("value1", result.orElse(null));
    }
}
