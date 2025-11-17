package com.mipt.vsevolodkirichuk.dz11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class MetricableDecoratorTest {
    private DataService baseService;
    private MetricableDecorator metricableDecorator;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        baseService = new SimpleDataService();
        metricableDecorator = new MetricableDecorator(baseService);
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testFindDataSendsMetric() {
        baseService.saveData("key1", "value1");
        metricableDecorator.findDataByKey("key1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Метод выполнялся:"));
    }

    @Test
    void testSaveDataSendsMetric() {
        metricableDecorator.saveData("key1", "value1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Метод выполнялся:"));
    }

    @Test
    void testDeleteDataSendsMetric() {
        baseService.saveData("key1", "value1");
        metricableDecorator.deleteData("key1");
        
        String output = outputStream.toString();
        assertTrue(output.contains("Метод выполнялся:"));
    }

    @Test
    void testMultipleOperationsSendMetrics() {
        metricableDecorator.saveData("key1", "value1");
        metricableDecorator.findDataByKey("key1");
        metricableDecorator.deleteData("key1");
        
        String output = outputStream.toString();
        int count = output.split("Метод выполнялся:").length - 1;
        assertEquals(3, count);
    }

    @Test
    void testActualDataOperations() {
        metricableDecorator.saveData("key1", "value1");
        Optional<String> result = metricableDecorator.findDataByKey("key1");
        
        assertEquals("value1", result.orElse(null));
    }

    @Test
    void testCustomMetricService() {
        MockMetricService mockMetricService = new MockMetricService();
        MetricableDecorator decorator = new MetricableDecorator(baseService, mockMetricService);
        
        decorator.saveData("key1", "value1");
        decorator.findDataByKey("key1");
        decorator.deleteData("key1");
        
        assertEquals(3, mockMetricService.getCallCount());
    }

    private static class MockMetricService extends MetricableDecorator.MetricService {
        private int callCount = 0;

        @Override
        public void sendMetric(Duration duration) {
            callCount++;
        }

        public int getCallCount() {
            return callCount;
        }
    }
}
