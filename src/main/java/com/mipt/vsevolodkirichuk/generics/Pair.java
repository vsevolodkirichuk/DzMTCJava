package com.mipt.vsevolodkirichuk.generics;

public class Pair<K, V> {
  private K key;
  private V value;

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public void setValue(V value) {
    this.value = value;
  }

  public Pair<V, K> swap() {
    return new Pair<>(this.value, this.key);
  }

  @Override
  public String toString() {
    return "Pair{key=" + key + ", value=" + value + "}";
  }

  public static void main(String[] args) {
    final Pair<String, Integer> pair = new Pair<>("Age", 25);
    System.out.println(pair);
    final Pair<Integer, String> swapped = pair.swap();
    System.out.println(swapped);
  }
}