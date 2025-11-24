package com.mipt.vsevolodkirichuk.dz12;
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        
        System.out.println("=== Пример 1: Простой перевод ===");
        BankAccount acc1 = new BankAccount(1, 1000);
        BankAccount acc2 = new BankAccount(2, 500);
        
        System.out.println("До перевода:");
        System.out.println(acc1);
        System.out.println(acc2);
        
        bank.sendToAccount(acc1, acc2, 200);
        
        System.out.println("\nПосле перевода 200:");
        System.out.println(acc1);
        System.out.println(acc2);
        
        System.out.println("\n=== Пример 2: Многопоточные переводы ===");
        BankAccount acc3 = new BankAccount(3, 10000);
        BankAccount acc4 = new BankAccount(4, 10000);
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                bank.sendToAccount(acc3, acc4, 10);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                bank.sendToAccount(acc4, acc3, 10);
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("После 100 переводов туда-обратно:");
        System.out.println(acc3);
        System.out.println(acc4);
        System.out.println("Общий баланс: " + (acc3.getBalance() + acc4.getBalance()));
        
        System.out.println("\n=== Пример 3: Недостаточно средств ===");
        BankAccount poorAcc = new BankAccount(5, 50);
        BankAccount richAcc = new BankAccount(6, 1000);
        
        try {
            bank.sendToAccount(poorAcc, richAcc, 100);
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        
        System.out.println(poorAcc);
        System.out.println(richAcc);
        
        System.out.println("\n=== Пример 4: Демонстрация deadlock (будет зависание) ===");
        System.out.println("ВНИМАНИЕ: Следующий код может вызвать deadlock!");
        System.out.println("Запускаем два потока с методом sendToAccountDeadlock...");
        
        BankAccount deadAcc1 = new BankAccount(7, 1000);
        BankAccount deadAcc2 = new BankAccount(8, 1000);
        
        Thread deadThread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                bank.sendToAccountDeadlock(deadAcc1, deadAcc2, 10);
                System.out.println("Thread 1: transfer " + (i + 1));
            }
        });
        
        Thread deadThread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                bank.sendToAccountDeadlock(deadAcc2, deadAcc1, 10);
                System.out.println("Thread 2: transfer " + (i + 1));
            }
        });
        
        deadThread1.start();
        deadThread2.start();
        
        try {
            deadThread1.join(2000);
            deadThread2.join(2000);
            
            if (deadThread1.isAlive() || deadThread2.isAlive()) {
                System.out.println("\n⚠️ DEADLOCK ОБНАРУЖЕН! Потоки зависли.");
                System.out.println("Принудительно завершаем программу...");
                System.exit(0);
            } else {
                System.out.println("Deadlock не произошел (случайно избежали)");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
