package com.mipt.vsevolodkirichuk.dz11;
import java.util.Optional;

public class ValidationDecorator implements DataService {
    private final DataService wrappedService;

    public ValidationDecorator(DataService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        validateKey(key);
        return wrappedService.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        validateKey(key);
        validateData(data);
        wrappedService.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        validateKey(key);
        return wrappedService.deleteData(key);
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }

    private void validateData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
    }
}
