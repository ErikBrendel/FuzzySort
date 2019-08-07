package fuzzysort.meta;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.ToCompare;
import fuzzysort.solver.Solver;
import fuzzysort.ui.GraphDisplay;
import fuzzysort.ui.SortDisplay;

import java.util.*;
import java.util.function.Function;

import static fuzzysort.meta.ExampleGenerator.VisualsEnabled;

public class Tweakable {
    public static void main(String[] args) {
        Map<String, String> arguments = argparse(args);

        long seed = Long.parseLong(arguments.get("--seed"));

        // instance loading
        String[] instanceConfigSplit = arguments.get("-i").split("/");
        String[] instanceConfig = instanceConfigSplit[instanceConfigSplit.length - 1].split(",");
        int itemCount = Integer.parseInt(instanceConfig[0]);
        int connectionCount = Integer.parseInt(instanceConfig[1]);
        int offMax = Integer.parseInt(instanceConfig[2]);

        // parameter loading
        float forceRelation = Float.parseFloat(arguments.get("--forceRelation"));
        int graphIterations = Integer.parseInt(arguments.get("--graphIterations"));
        float forceAmount = Float.parseFloat(arguments.get("--forceAmount"));
        float accuToStrengthBase = Float.parseFloat(arguments.get("--accuToStrengthBase"));
        int dimensions = Integer.parseInt(arguments.get("--dimensions"));

        Random r = new Random(seed);
        FuzzySortInstance instance = ExampleGenerator.fromInts(itemCount, r);

        long startTime = System.currentTimeMillis();

        //Function<ToCompare, FuzzyComparison> compModel = ExampleGenerator.fixedOffComp(r, instance, offMax);
        Function<ToCompare, FuzzyComparison> compModel = ExampleGenerator.categoricalOffComp(r, instance, offMax);

        Solver solver = new Solver(instance, dimensions, graphIterations, forceRelation, forceAmount, r, accuToStrengthBase);
        if (VisualsEnabled) {
            new GraphDisplay(solver.graph).showWindow();
            new SortDisplay(solver.graph).showWindow();
        }
        List<String> result = solver.interactiveFill(compModel, connectionCount);

        long endTime = System.currentTimeMillis();
        float seconds = (endTime - startTime) / 1000f;

        int diff = 0;
        for (int i = 0; i < itemCount; i++) {
            int thisDiff = Math.abs(Integer.parseInt(result.get(i)) - i + 1);
            diff += thisDiff * thisDiff;
        }
        double unsortedness = Math.sqrt(diff / (double) itemCount);
        System.out.println(-unsortedness + " " + seconds);
    }

    private static Map<String, String> argparse(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            arguments.put(args[i], args[i + 1]);
        }
        return arguments;
    }
}
