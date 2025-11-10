package com.mipt.vsevolodkirichuk.dz10;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class FileProcessor {
    public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
        if (partSize <= 0) {
            throw new IllegalArgumentException("Размер части должен быть положительным числом");
        }

        Path sourceFile = Paths.get(sourcePath);
        
        // Проверяем что исходный файл существует
        if (!Files.exists(sourceFile)) {
            throw new IOException("Исходный файл не существует: " + sourcePath);
        }

        Path outputDirectory = Paths.get(outputDir);
        Files.createDirectories(outputDirectory);

        List<Path> partPaths = new ArrayList<>();

        String originalFileName = sourceFile.getFileName().toString();

        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
            
            long fileSize = sourceChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate(partSize);
            
            int partNumber = 1;
            long bytesRead = 0;
            
            while (bytesRead < fileSize) {
                // Очищаем буфер для следующего чтения
                buffer.clear();
                
                // Читаем данные в буфер
                int read = sourceChannel.read(buffer);
                if (read == -1) {
                    break; // Достигнут конец файла
                }
                
                bytesRead += read;
                
                // Подготавливаем буфер к чтению (переключаем из режима записи в режим чтения)
                buffer.flip();
                
                // Создаем имя файла для части
                String partFileName = originalFileName + ".part" + partNumber;
                Path partPath = outputDirectory.resolve(partFileName);
                
                // Записываем данные из буфера в файл части
                try (FileChannel partChannel = FileChannel.open(partPath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    
                    while (buffer.hasRemaining()) {
                        partChannel.write(buffer);
                    }
                }
                
                partPaths.add(partPath);
                partNumber++;
            }
        }
        
        return partPaths;
    }

    public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
        if (partPaths == null || partPaths.isEmpty()) {
            throw new IllegalArgumentException("Список частей не может быть пустым");
        }

        for (Path partPath : partPaths) {
            if (!Files.exists(partPath)) {
                throw new IOException("Файл части не существует: " + partPath);
            }
        }

        Path outputFile = Paths.get(outputPath);

        Path parentDir = outputFile.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        try (FileChannel outputChannel = FileChannel.open(outputFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Path partPath : partPaths) {
                // Открываем канал для чтения части
                try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
                    
                    long partSize = partChannel.size();
                    long bytesTransferred = 0;
                    

                    while (bytesTransferred < partSize) {
                        long transferred = partChannel.transferTo(
                            bytesTransferred,
                            partSize - bytesTransferred,
                            outputChannel
                        );
                        
                        if (transferred == 0) {
                            ByteBuffer buffer = ByteBuffer.allocate(8192);
                            partChannel.position(bytesTransferred);
                            
                            int read = partChannel.read(buffer);
                            if (read == -1) {
                                break;
                            }
                            
                            buffer.flip();
                            while (buffer.hasRemaining()) {
                                outputChannel.write(buffer);
                            }
                            
                            bytesTransferred += read;
                        } else {
                            bytesTransferred += transferred;
                        }
                    }
                }
            }
        }
    }


    public void cleanupParts(List<Path> partPaths) throws IOException {
        for (Path partPath : partPaths) {
            Files.deleteIfExists(partPath);
        }
    }

    public static void main(String[] args) {
        FileProcessor processor = new FileProcessor();

        try {
            Path testFile = Paths.get("test_data.bin");
            byte[] testData = new byte[10000]; // 10KB данных
            for (int i = 0; i < testData.length; i++) {
                testData[i] = (byte) (i % 256);
            }
            Files.write(testFile, testData);
            System.out.println("Создан тестовый файл: " + testFile);

            String outputDir = "parts";
            List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 3000);
            System.out.println("Файл разбит на " + parts.size() + " частей:");
            for (Path part : parts) {
                System.out.println("  - " + part + " (" + Files.size(part) + " байт)");
            }

            Path mergedFile = Paths.get("merged_data.bin");
            processor.mergeFiles(parts, mergedFile.toString());
            System.out.println("Части объединены в файл: " + mergedFile);

            byte[] originalData = Files.readAllBytes(testFile);
            byte[] mergedData = Files.readAllBytes(mergedFile);
            
            boolean identical = java.util.Arrays.equals(originalData, mergedData);
            System.out.println("Файлы идентичны: " + identical);

            processor.cleanupParts(parts);
            System.out.println("Части файлов удалены");

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
