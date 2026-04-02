package com.mipt.vsevolodkirichuk.dz12;
public class BankAccount {
    private final long id;
    private int balance;

    public BankAccount(long id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public long getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    public void withdraw(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (balance < amount) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
    }

    @Override
    public String toString() {
        return "BankAccount{id=" + id + ", balance=" + balance + "}";
    }
}
