package fuzzysort.meta;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.solver.Solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ExampleGenerator {

    public static final boolean VisualsEnabled = true;

    private static final int itemCount = 70;

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            ints.add(i);
        }
        Collections.shuffle(ints);
        AtomicInteger count = new AtomicInteger(0);
        ints.sort((a, b) -> {
            count.incrementAndGet();
            return a - b;
        });
        System.out.println("Comparison count for sorting: " + count);

        Random r = new Random();
        FuzzySortInstance instance = fromInts(itemCount, r);
        new Solver(instance, 1, 1800, 0.93f, 5.7f, r, 2.6f).interactiveFill((task) -> {
            int v1 = Integer.parseInt(task.item1);
            int v2 = Integer.parseInt(task.item2);
            int off = r.nextInt(13) - 6;
            return new FuzzyComparison(instance, task.item1, task.item2, v2 - v1 + off, Math.abs(off));
        }, 120);
    }

    public static FuzzySortInstance fromInts(int count, Random r) {
        List<String> items = new ArrayList<>(count);
        for (int item = 0; item < count; item++) {
            items.add(item + "");
        }
        Collections.shuffle(items, r);


        FuzzySortInstance instance = new FuzzySortInstance();
        for (String item : items) {
            instance.addItem(item);
        }
        return instance;
    }
}
