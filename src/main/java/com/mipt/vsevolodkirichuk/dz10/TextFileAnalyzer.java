package com.mipt.vsevolodkirichuk.dz10;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TextFileAnalyzer {

    public static class AnalysisResult {
        private final long lineCount;  // количество строк в файле
        private final long wordCount;  // количество слов в файле
        private final long charCount;  // количество символов в файле
        private final Map<Character, Integer> charFrequency; // частота использования каждого символа

        public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Integer> charFrequency) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
            this.charFrequency = charFrequency != null ? new HashMap<>(charFrequency) : new HashMap<>();
        }

        public AnalysisResult(long lineCount, long wordCount, long charCount) {
            this(lineCount, wordCount, charCount, new HashMap<>());
        }

        public long getLineCount() {
            return lineCount;
        }

        public long getWordCount() {
            return wordCount;
        }

        public long getCharCount() {
            return charCount;
        }

        public Map<Character, Integer> getCharFrequency() {
            return new HashMap<>(charFrequency);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Результаты анализа файла ===\n");
            sb.append("Количество строк: ").append(lineCount).append("\n");
            sb.append("Количество слов: ").append(wordCount).append("\n");
            sb.append("Количество символов: ").append(charCount).append("\n");
            
            if (!charFrequency.isEmpty()) {
                sb.append("\n=== Частота использования символов ===\n");
                charFrequency.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(entry -> {
                        char ch = entry.getKey();
                        String displayChar = ch == '\n' ? "\\n" : 
                                           ch == '\r' ? "\\r" : 
                                           ch == '\t' ? "\\t" : 
                                           String.valueOf(ch);
                        sb.append("'").append(displayChar).append("': ").append(entry.getValue()).append("\n");
                    });
            }
            
            return sb.toString();
        }
    }

    public AnalysisResult analyzeFile(String filePath) throws IOException {
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;
        Map<Character, Integer> charFrequency = new HashMap<>();

        // Используем try-with-resources для автоматического закрытия потоков
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lineCount++;
                
                // Подсчет символов (включая пробелы)
                charCount += line.length();
                
                // Подсчет слов (разделяем по пробельным символам)
                if (!line.trim().isEmpty()) {
                    String[] words = line.trim().split("\\s+");
                    wordCount += words.length;
                }
                
                // Анализ частоты символов
                for (char ch : line.toCharArray()) {
                    charFrequency.put(ch, charFrequency.getOrDefault(ch, 0) + 1);
                }
            }
        }

        return new AnalysisResult(lineCount, wordCount, charCount, charFrequency);
    }

    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        // Используем try-with-resources для автоматического закрытия потоков
        try (FileWriter fileWriter = new FileWriter(outputPath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            
            // Записываем результаты в файл используя toString()
            bufferedWriter.write(result.toString());
            bufferedWriter.flush();
        }
    }

    public static void main(String[] args) {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();
        
        try {
            // Создаем тестовый файл для демонстрации
            String testFilePath = "test_input.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFilePath))) {
                writer.write("Hello world!\n");
                writer.write("This is a test file.\n");
                writer.write("Java IO is powerful.\n");
            }

            AnalysisResult result = analyzer.analyzeFile(testFilePath);

            System.out.println(result);

            String outputPath = "analysis_output.txt";
            analyzer.saveAnalysisResult(result, outputPath);
            System.out.println("Результаты сохранены в файл: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
