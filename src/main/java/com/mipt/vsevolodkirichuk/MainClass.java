package com.mipt.vsevolodkirichuk;

public class MainClass {
    // Пункт 7: Приватные поля без инициализации
    private int number;
    private String text;
    
    // Пункт 8: protected статическое поле double без инициализации
    protected static double staticValue;
    
    // Пункт 9: Публичное неизменяемое поле long с инициализацией
    public final long constantValue = 1000L;
    
    // Пункт 6: Метод точки входа в программу
    public static void main(String[] args) {
        // Пункт 10: Цикл от 0 до 15
        for (int i = 0; i < 16; i++) {
            System.out.println("Iter: " + i);
        }
    }
}
