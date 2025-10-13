package com.mipt.vsevolodkirichuk.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtils {

  public static <T> List<T> mergeLists(List<? extends T> list1,
                                       List<? extends T> list2) {
    List<T> result = new ArrayList<>();

    if (list1 != null) {
      result.addAll(list1);
    }
    if (list2 != null) {
      result.addAll(list2);
    }

    return result;
  }

  public static <T> void addAll(List<? super T> destination,
                                List<? extends T> source) {
    if (destination == null || source == null) {
      return;
    }
    destination.addAll(source);
  }

  public static void main(String[] args) {
    final List<Integer> list1 = Arrays.asList(1, 2, 3);
    final List<Double> list2 = Arrays.asList(4.5, 5.6);
    final List<Number> merged = CollectionUtils.mergeLists(list1, list2);
    System.out.println("Merged: " + merged);

    final List<Object> destination = new ArrayList<>();
    CollectionUtils.addAll(destination, list1);
    System.out.println("Destination after addAll: " + destination);
  }
}