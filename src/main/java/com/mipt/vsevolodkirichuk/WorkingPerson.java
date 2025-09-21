package com.mipt.vsevolodkirichuk;

public abstract class WorkingPerson {
    public abstract void work(int hours);
    
    public boolean goHome(String firstString, String secondString) {
        return firstString.equals(secondString);
    }
}
