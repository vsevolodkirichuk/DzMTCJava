package com.mipt.vsevolodkirichuk.dz11;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MetricableDecorator implements DataService {
    private final DataService wrappedService;
    private final MetricService metricService;

    public MetricableDecorator(DataService wrappedService) {
        this.wrappedService = wrappedService;
        this.metricService = new MetricService();
    }

    public MetricableDecorator(DataService wrappedService, MetricService metricService) {
        this.wrappedService = wrappedService;
        this.metricService = metricService;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        Instant start = Instant.now();
        Optional<String> result = wrappedService.findDataByKey(key);
        Duration duration = Duration.between(start, Instant.now());
        metricService.sendMetric(duration);
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        Instant start = Instant.now();
        wrappedService.saveData(key, data);
        Duration duration = Duration.between(start, Instant.now());
        metricService.sendMetric(duration);
    }

    @Override
    public boolean deleteData(String key) {
        Instant start = Instant.now();
        boolean result = wrappedService.deleteData(key);
        Duration duration = Duration.between(start, Instant.now());
        metricService.sendMetric(duration);
        return result;
    }

    public static class MetricService {
        public void sendMetric(Duration duration) {
            System.out.println("Метод выполнялся: " + duration.toString());
        }
    }
}
