package com.mipt.vsevolodkirichuk.dz11;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class CachingDecoratorTest {
    private DataService baseService;
    private CachingDecorator cachingDecorator;

    @BeforeEach
    void setUp() {
        baseService = new SimpleDataService();
        cachingDecorator = new CachingDecorator(baseService);
    }

    @Test
    void testCacheHitAfterFirstFind() {
        cachingDecorator.saveData("key1", "value1");
        
        Optional<String> firstCall = cachingDecorator.findDataByKey("key1");
        Optional<String> secondCall = cachingDecorator.findDataByKey("key1");
        
        assertEquals("value1", firstCall.orElse(null));
        assertEquals("value1", secondCall.orElse(null));
        assertTrue(cachingDecorator.getCache().containsKey("key1"));
    }

    @Test
    void testCacheUpdatedOnSave() {
        cachingDecorator.saveData("key1", "value1");
        assertEquals("value1", cachingDecorator.getCache().get("key1"));
        
        cachingDecorator.saveData("key1", "value2");
        assertEquals("value2", cachingDecorator.getCache().get("key1"));
    }

    @Test
    void testCacheInvalidatedOnDelete() {
        cachingDecorator.saveData("key1", "value1");
        assertTrue(cachingDecorator.getCache().containsKey("key1"));
        
        boolean deleted = cachingDecorator.deleteData("key1");
        assertTrue(deleted);
        assertFalse(cachingDecorator.getCache().containsKey("key1"));
    }

    @Test
    void testFindNonExistentKey() {
        Optional<String> result = cachingDecorator.findDataByKey("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteNonExistentKey() {
        boolean deleted = cachingDecorator.deleteData("nonexistent");
        assertFalse(deleted);
    }

    @Test
    void testMultipleKeys() {
        cachingDecorator.saveData("key1", "value1");
        cachingDecorator.saveData("key2", "value2");
        cachingDecorator.saveData("key3", "value3");
        
        assertEquals(3, cachingDecorator.getCache().size());
        assertEquals("value1", cachingDecorator.findDataByKey("key1").orElse(null));
        assertEquals("value2", cachingDecorator.findDataByKey("key2").orElse(null));
        assertEquals("value3", cachingDecorator.findDataByKey("key3").orElse(null));
    }

    @Test
    void testCacheIsolation() {
        cachingDecorator.saveData("key1", "value1");
        
        baseService.deleteData("key1");
        
        Optional<String> cachedValue = cachingDecorator.findDataByKey("key1");
        assertEquals("value1", cachedValue.orElse(null));
    }
}
