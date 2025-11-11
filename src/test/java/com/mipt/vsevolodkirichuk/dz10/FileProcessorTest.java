package com.mipt.vsevolodkirichuk.dz10;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class FileProcessorTest {

    @Test
    void testSplitAndMergeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        byte[] testData = new byte[1500]; // 1.5KB данных
        new Random().nextBytes(testData);
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertEquals(3, parts.size(), "Должно быть создано 3 части");

        for (Path part : parts) {
            assertTrue(Files.exists(part), "Часть должна существовать: " + part);
        }

        assertEquals(500, Files.size(parts.get(0)), "Первая часть должна быть 500 байт");
        assertEquals(500, Files.size(parts.get(1)), "Вторая часть должна быть 500 байт");
        assertEquals(500, Files.size(parts.get(2)), "Третья часть должна быть 500 байт");

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());

        assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile),
                "Исходный и объединенный файлы должны быть идентичны");

        Files.deleteIfExists(testFile);
        Files.deleteIfExists(mergedFile);
        processor.cleanupParts(parts);
    }

    @Test
    void testSplitFileWithOddSize() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        byte[] testData = new byte[1234];
        new Random(42).nextBytes(testData); // Используем seed для воспроизводимости
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertEquals(3, parts.size(), "Должно быть создано 3 части");

        assertEquals(500, Files.size(parts.get(0)), "Первая часть должна быть 500 байт");
        assertEquals(500, Files.size(parts.get(1)), "Вторая часть должна быть 500 байт");
        assertEquals(234, Files.size(parts.get(2)), "Третья часть должна быть 234 байта");

        // Суммарный размер частей должен равняться размеру исходного файла
        long totalSize = parts.stream().mapToLong(p -> {
            try {
                return Files.size(p);
            } catch (IOException e) {
                return 0;
            }
        }).sum();
        assertEquals(1234, totalSize, "Суммарный размер частей должен равняться размеру исходного файла");

        // Объединяем и проверяем
        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());
        assertArrayEquals(testData, Files.readAllBytes(mergedFile),
                "Данные после объединения должны совпадать с исходными");

        Files.deleteIfExists(testFile);
        Files.deleteIfExists(mergedFile);
        processor.cleanupParts(parts);
    }

    @Test
    void testSplitSmallFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("small", ".dat");
        byte[] testData = new byte[100]; // 100 байт
        new Random().nextBytes(testData);
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertEquals(1, parts.size(), "Должна быть создана только 1 часть");
        assertEquals(100, Files.size(parts.get(0)), "Часть должна быть 100 байт");

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());
        assertArrayEquals(testData, Files.readAllBytes(mergedFile));

        Files.deleteIfExists(testFile);
        Files.deleteIfExists(mergedFile);
        processor.cleanupParts(parts);
    }

    @Test
    void testSplitEmptyFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("empty", ".dat");
        Files.write(testFile, new byte[0]);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertEquals(0, parts.size(), "Для пустого файла не должно быть частей");

        Files.deleteIfExists(testFile);
    }

    @Test
    void testMergeFilesInOrder() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path part1 = Files.createTempFile("part1", ".dat");
        Path part2 = Files.createTempFile("part2", ".dat");
        Path part3 = Files.createTempFile("part3", ".dat");

        Files.write(part1, new byte[]{1, 2, 3});
        Files.write(part2, new byte[]{4, 5, 6});
        Files.write(part3, new byte[]{7, 8, 9});

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(List.of(part1, part2, part3), mergedFile.toString());

        byte[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(expected, Files.readAllBytes(mergedFile),
                "Данные должны быть объединены в правильном порядке");

        Files.deleteIfExists(part1);
        Files.deleteIfExists(part2);
        Files.deleteIfExists(part3);
        Files.deleteIfExists(mergedFile);
    }

    @Test
    void testSplitNonExistentFile() {
        FileProcessor processor = new FileProcessor();

        String outputDir = System.getProperty("java.io.tmpdir");
        
        assertThrows(IOException.class, () -> {
            processor.splitFile("nonexistent_file.dat", outputDir, 500);
        }, "Должно быть выброшено исключение при попытке разбить несуществующий файл");
    }

    @Test
    void testMergeWithMissingPart() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path existingPart = Files.createTempFile("part", ".dat");
        Files.write(existingPart, new byte[]{1, 2, 3});
        Path missingPart = Path.of("nonexistent_part.dat");

        Path outputFile = Files.createTempFile("merged", ".dat");

        assertThrows(IOException.class, () -> {
            processor.mergeFiles(List.of(existingPart, missingPart), outputFile.toString());
        }, "Должно быть выброшено исключение при попытке объединить несуществующую часть");

        // Очищаем
        Files.deleteIfExists(existingPart);
        Files.deleteIfExists(outputFile);
    }

    @Test
    void testSplitWithInvalidPartSize() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        Files.write(testFile, new byte[]{1, 2, 3});
        String outputDir = Files.createTempDirectory("parts").toString();

        assertThrows(IllegalArgumentException.class, () -> {
            processor.splitFile(testFile.toString(), outputDir, -100);
        }, "Должно быть выброшено исключение при отрицательном размере части");

        assertThrows(IllegalArgumentException.class, () -> {
            processor.splitFile(testFile.toString(), outputDir, 0);
        }, "Должно быть выброшено исключение при нулевом размере части");

        Files.deleteIfExists(testFile);
    }

    @Test
    void testLargeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("large", ".dat");
        byte[] testData = new byte[10240];
        new Random(123).nextBytes(testData);
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 1024);

        assertEquals(10, parts.size(), "Должно быть создано 10 частей");

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());

        assertArrayEquals(testData, Files.readAllBytes(mergedFile),
                "Большой файл должен быть восстановлен корректно");

        Files.deleteIfExists(testFile);
        Files.deleteIfExists(mergedFile);
        processor.cleanupParts(parts);
    }

    @Test
    void testPartFileNaming() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("myfile", ".txt");
        Files.write(testFile, new byte[1500]);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        String originalFileName = testFile.getFileName().toString();
        for (int i = 0; i < parts.size(); i++) {
            String expectedName = originalFileName + ".part" + (i + 1);
            assertTrue(parts.get(i).getFileName().toString().equals(expectedName),
                    "Имя части должно соответствовать шаблону: " + expectedName);
        }

        Files.deleteIfExists(testFile);
        processor.cleanupParts(parts);
    }
}
