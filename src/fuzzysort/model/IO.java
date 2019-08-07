package fuzzysort.model;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class IO {
    public static FuzzySortInstance load(File path) {
        try {
            FuzzySortInstance instance = new FuzzySortInstance();
            List<String> content = Files.readAllLines(path.toPath());
            for (String item: content.get(0).split(",")) {
                instance.addItem(item);
            }
            content.remove(0);
            for (String comparison: content) {
                instance.addComparison(new FuzzyComparison(instance, comparison));
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(FuzzySortInstance instance) {
        try {
            List<String> data = new ArrayList<>();
            data.add(String.join(",", instance.getItems()));
            for (FuzzyComparison comparison: instance.getComparisons()) {
                data.add(comparison.serialize());
            }
            Files.write(instance.getPath(), data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
