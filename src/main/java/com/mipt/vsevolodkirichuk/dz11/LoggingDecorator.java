package com.mipt.vsevolodkirichuk.dz11;
import java.util.Optional;

public class LoggingDecorator implements DataService {
    private final DataService wrappedService;

    public LoggingDecorator(DataService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        System.out.println("Finding data by key: " + key);
        Optional<String> result = wrappedService.findDataByKey(key);
        System.out.println("Found data: " + result.orElse("null"));
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        System.out.println("Saving data: key=" + key + ", data=" + data);
        wrappedService.saveData(key, data);
        System.out.println("Data saved successfully");
    }

    @Override
    public boolean deleteData(String key) {
        System.out.println("Deleting data by key: " + key);
        boolean result = wrappedService.deleteData(key);
        System.out.println("Delete result: " + result);
        return result;
    }
}
