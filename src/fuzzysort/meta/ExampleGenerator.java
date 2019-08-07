package fuzzysort.meta;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.ToCompare;
import fuzzysort.solver.Solver;
import fuzzysort.ui.GraphDisplay;
import fuzzysort.ui.SortDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ExampleGenerator {

    public static final boolean VisualsEnabled = true;

    private static final int itemCount = 400;

    public static void main(String[] args) {
        Random r = new Random();
        List<Integer> ints = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            ints.add(i);
        }
        Collections.shuffle(ints, r);
        AtomicInteger count = new AtomicInteger(0);
        ints.sort((a, b) -> {
            count.incrementAndGet();
            return a - b;
        });
        System.out.println("Comparison count for sorting: " + count);

        FuzzySortInstance instance = fromInts(itemCount, r);

        //Function<ToCompare, FuzzyComparison> model = fixedOffComp(r, instance, 6);
        Function<ToCompare, FuzzyComparison> model = categoricalOffComp(r, instance, 10);

        Solver solver = new Solver(instance, 0, 400, 0.9f, 17f, r, 2.2f);
        if (VisualsEnabled) {
            new GraphDisplay(solver.graph).showWindow();
            new SortDisplay(solver.graph).showWindow();
        }
        solver.interactiveFill(model, 500);
    }

    public static Function<ToCompare, FuzzyComparison> fixedOffComp(Random r, FuzzySortInstance instance, int offRadius) {
        int offDia = offRadius * 2 + 1;
        return (task) -> {
            int v1 = Integer.parseInt(task.item1);
            int v2 = Integer.parseInt(task.item2);
            int off = r.nextInt(offDia) - offRadius;
            return new FuzzyComparison(instance, task.item1, task.item2, v2 - v1 + off, Math.abs(off));
        };
    }

    public static Function<ToCompare, FuzzyComparison> categoricalOffComp(Random r, FuzzySortInstance instance, int offRadius) {
        int offDia = offRadius * 2 + 1;
        return (task) -> {
            int v1 = Integer.parseInt(task.item1);
            int v2 = Integer.parseInt(task.item2);
            int diff = v2 - v1;
            int absDiff = Math.abs(diff);
            float accuracy = FuzzyComparison.ACCURACY_HIGH;
            if (absDiff < 5) {
                accuracy = FuzzyComparison.ACCURACY_VAGUE;
            } else if (absDiff < 10) {
                accuracy = FuzzyComparison.ACCURACY_LOW;
            } else if (absDiff < 20) {
                accuracy = FuzzyComparison.ACCURACY_MIDDLE;
            }
            int off = r.nextInt(offDia) - offRadius;
            return new FuzzyComparison(instance, task.item1, task.item2, v2 - v1 + off, accuracy);
        };
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
