package com.mipt.vsevolodkirichuk.dz12;
public class Bank {

    public void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts cannot be null");
        }
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        synchronized (from) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (to) {
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                } else {
                    throw new IllegalStateException("Insufficient funds");
                }
            }
        }
    }

    public void sendToAccount(BankAccount from, BankAccount to, int amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts cannot be null");
        }
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        BankAccount first = from.getId() < to.getId() ? from : to;
        BankAccount second = from.getId() < to.getId() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                } else {
                    throw new IllegalStateException("Insufficient funds");
                }
            }
        }
    }
}
