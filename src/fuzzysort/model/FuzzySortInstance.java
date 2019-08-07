package fuzzysort.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FuzzySortInstance {
    public Path path;
    private List<String> items = new ArrayList<>();
    private List<FuzzyComparison> comparisons = new ArrayList<>();

    public void addItem(String item) {
        items.add(item);
    }

    public List<String> getItems() {
        return items;
    }

    public List<FuzzyComparison> getComparisons() {
        return comparisons;
    }

    public void addComparison(String item1, String item2, float relation, float accuracy) {
        addComparison(new FuzzyComparison(this, item1, item2, relation, accuracy));
    }

    public void addComparison(FuzzyComparison comparison) {
        comparisons.add(comparison);
    }
}
