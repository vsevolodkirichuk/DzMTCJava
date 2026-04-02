package com.mipt.vsevolodkirichuk.dz12;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {
    private Bank bank;
    private BankAccount account1;
    private BankAccount account2;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        account1 = new BankAccount(1, 1000);
        account2 = new BankAccount(2, 1000);
    }

    @Test
    void testSimpleTransfer() {
        bank.sendToAccount(account1, account2, 100);
        
        assertEquals(900, account1.getBalance());
        assertEquals(1100, account2.getBalance());
    }

    @Test
    void testMultipleTransfers() {
        bank.sendToAccount(account1, account2, 100);
        bank.sendToAccount(account2, account1, 50);
        bank.sendToAccount(account1, account2, 200);
        
        assertEquals(750, account1.getBalance());
        assertEquals(1250, account2.getBalance());
    }

    @Test
    void testConcurrentTransfers() throws InterruptedException {
        BankAccount acc1 = new BankAccount(1, 10000);
        BankAccount acc2 = new BankAccount(2, 10000);
        
        int threadCount = 100;
        int transferAmount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount / 2; i++) {
            executor.submit(() -> {
                try {
                    bank.sendToAccount(acc1, acc2, transferAmount);
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    bank.sendToAccount(acc2, acc1, transferAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(20000, acc1.getBalance() + acc2.getBalance());
    }

    @Test
    void testConcurrentTransfersWithMultipleAccounts() throws InterruptedException {
        BankAccount[] accounts = new BankAccount[5];
        for (int i = 0; i < 5; i++) {
            accounts[i] = new BankAccount(i, 1000);
        }
        
        int totalInitialBalance = 5000;
        int threadCount = 200;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            int fromIdx = i % 5;
            int toIdx = (i + 1) % 5;
            
            executor.submit(() -> {
                try {
                    if (accounts[fromIdx].getBalance() >= 10) {
                        bank.sendToAccount(accounts[fromIdx], accounts[toIdx], 10);
                    }
                } catch (IllegalStateException e) {
                    
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        int totalFinalBalance = 0;
        for (BankAccount account : accounts) {
            totalFinalBalance += account.getBalance();
        }
        
        assertEquals(totalInitialBalance, totalFinalBalance);
    }

    @Test
    void testDeadlockScenario() throws InterruptedException {
        BankAccount acc1 = new BankAccount(1, 1000);
        BankAccount acc2 = new BankAccount(2, 1000);
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    bank.sendToAccountDeadlock(acc1, acc2, 10);
                } catch (IllegalStateException e) {
                    
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    bank.sendToAccountDeadlock(acc2, acc1, 10);
                } catch (IllegalStateException e) {
                    
                }
            }
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join(3000);
        thread2.join(3000);
        
        assertTrue(thread1.isAlive() || thread2.isAlive() || 
                  (acc1.getBalance() + acc2.getBalance() == 2000));
    }

    @Test
    void testNoDeadlockWithCorrectMethod() throws InterruptedException {
        BankAccount acc1 = new BankAccount(1, 10000);
        BankAccount acc2 = new BankAccount(2, 10000);
        
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount / 2; i++) {
            executor.submit(() -> {
                try {
                    bank.sendToAccount(acc1, acc2, 10);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    bank.sendToAccount(acc2, acc1, 10);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertTrue(completed);
        assertEquals(20000, acc1.getBalance() + acc2.getBalance());
    }

    @Test
    void testInsufficientFunds() {
        BankAccount poorAccount = new BankAccount(1, 50);
        BankAccount richAccount = new BankAccount(2, 1000);
        
        assertThrows(IllegalStateException.class, () -> {
            bank.sendToAccount(poorAccount, richAccount, 100);
        });
        
        assertEquals(50, poorAccount.getBalance());
        assertEquals(1000, richAccount.getBalance());
    }

    @Test
    void testNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account2, -100);
        });
    }

    @Test
    void testZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account2, 0);
        });
    }

    @Test
    void testNullFromAccount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(null, account2, 100);
        });
    }

    @Test
    void testNullToAccount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, null, 100);
        });
    }

    @Test
    void testBothAccountsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(null, null, 100);
        });
    }

    @Test
    void testSameAccount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account1, 100);
        });
    }

    @Test
    void testConcurrentInsufficientFunds() throws InterruptedException {
        BankAccount acc = new BankAccount(1, 100);
        BankAccount target = new BankAccount(2, 0);
        
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    bank.sendToAccount(acc, target, 50);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(2, successCount.get());
        assertEquals(8, failCount.get());
        assertEquals(0, acc.getBalance());
        assertEquals(100, target.getBalance());
    }

    @Test
    void testRaceConditionPrevention() throws InterruptedException {
        BankAccount acc1 = new BankAccount(1, 1000);
        BankAccount acc2 = new BankAccount(2, 1000);
        BankAccount acc3 = new BankAccount(3, 1000);
        
        int threadCount = 300;
        ExecutorService executor = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            int operation = i % 3;
            
            executor.submit(() -> {
                try {
                    if (operation == 0) {
                        bank.sendToAccount(acc1, acc2, 5);
                    } else if (operation == 1) {
                        bank.sendToAccount(acc2, acc3, 5);
                    } else {
                        bank.sendToAccount(acc3, acc1, 5);
                    }
                } catch (IllegalStateException e) {
                    
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEquals(3000, acc1.getBalance() + acc2.getBalance() + acc3.getBalance());
    }
}
