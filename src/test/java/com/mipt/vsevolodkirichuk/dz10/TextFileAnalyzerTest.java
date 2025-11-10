package com.mipt.vsevolodkirichuk.dz10;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class TextFileAnalyzerTest {

    @Test
    void testAnalyzeFile() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path testFile = Files.createTempFile("test", ".txt");
        Files.write(testFile, Arrays.asList("Hello world!", "This is test."));

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

        assertEquals(2, result.getLineCount(), "Количество строк должно быть 2");

        assertEquals(5, result.getWordCount(), "Количество слов должно быть 5");

        assertEquals(25, result.getCharCount(), "Количество символов должно быть 25");

        Map<Character, Integer> charFreq = result.getCharFrequency();
        assertNotNull(charFreq, "Карта частот символов не должна быть null");
        assertTrue(charFreq.size() > 0, "Карта частот должна содержать данные");

        assertEquals(3, charFreq.get(' '), "Пробел должен встречаться 3 раза");

        Files.deleteIfExists(testFile);
    }
    
    @Test
    void testSaveAnalysisResult() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(2, 5, 20);

        Path outputFile = Files.createTempFile("analysis", ".txt");
        analyzer.saveAnalysisResult(result, outputFile.toString());

        assertTrue(Files.exists(outputFile), "Файл должен быть создан");
        assertTrue(Files.size(outputFile) > 0, "Размер файла должен быть больше 0");

        List<String> lines = Files.readAllLines(outputFile);
        String content = String.join("\n", lines);

        assertTrue(content.contains("2"), "Файл должен содержать количество строк: 2");
        assertTrue(content.contains("5"), "Файл должен содержать количество слов: 5");
        assertTrue(content.contains("20"), "Файл должен содержать количество символов: 20");
        assertTrue(content.contains("Результаты анализа"), "Файл должен содержать заголовок");

        Files.deleteIfExists(outputFile);
    }
    
    @Test
    void testAnalyzeEmptyFile() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path emptyFile = Files.createTempFile("empty", ".txt");

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(emptyFile.toString());

        assertEquals(0, result.getLineCount(), "Количество строк в пустом файле должно быть 0");
        assertEquals(0, result.getWordCount(), "Количество слов в пустом файле должно быть 0");
        assertEquals(0, result.getCharCount(), "Количество символов в пустом файле должно быть 0");

        Files.deleteIfExists(emptyFile);
    }
    
    @Test
    void testAnalyzeFileWithMultipleSpaces() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path testFile = Files.createTempFile("spaces", ".txt");
        Files.write(testFile, Arrays.asList("Hello    world", "Test     file"));

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

        assertEquals(4, result.getWordCount(), "Множественные пробелы должны учитываться правильно");
        assertEquals(2, result.getLineCount(), "Должно быть 2 строки");

        Files.deleteIfExists(testFile);
    }
    
    @Test
    void testAnalyzeFileWithSpecialCharacters() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path testFile = Files.createTempFile("special", ".txt");
        Files.write(testFile, Arrays.asList("Hello, world!", "123 + 456 = 579"));

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

        assertTrue(result.getCharCount() > 0, "Файл должен содержать символы");
        assertTrue(result.getWordCount() > 0, "Файл должен содержать слова");

        Map<Character, Integer> freq = result.getCharFrequency();
        assertTrue(freq.containsKey(','), "Должна быть запятая");
        assertTrue(freq.containsKey('!'), "Должен быть восклицательный знак");

        Files.deleteIfExists(testFile);
    }
    
    @Test
    void testCharFrequencyAnalysis() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path testFile = Files.createTempFile("freq", ".txt");
        Files.write(testFile, Arrays.asList("aaa", "bb", "c"));

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

        Map<Character, Integer> freq = result.getCharFrequency();
        assertEquals(3, freq.get('a'), "Символ 'a' должен встречаться 3 раза");
        assertEquals(2, freq.get('b'), "Символ 'b' должен встречаться 2 раза");
        assertEquals(1, freq.get('c'), "Символ 'c' должен встречаться 1 раз");

        Files.deleteIfExists(testFile);
    }
}
