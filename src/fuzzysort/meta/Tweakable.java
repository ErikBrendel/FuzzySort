package fuzzysort.meta;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.solver.Solver;

import java.util.*;

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
        int offRange = offMax * 2 + 1;

        // parameter loading
        float forceRelation = Float.parseFloat(arguments.get("--forceRelation"));
        int graphIterations = Integer.parseInt(arguments.get("--graphIterations"));
        float forceAmount = Float.parseFloat(arguments.get("--forceAmount"));
        float accuToStrengthBase = Float.parseFloat(arguments.get("--accuToStrengthBase"));
        int dimensions = Integer.parseInt(arguments.get("--dimensions"));

        Random r = new Random(seed);
        FuzzySortInstance instance = ExampleGenerator.fromInts(itemCount, r);

        long startTime = System.currentTimeMillis();

        List<String> result = new Solver(instance, dimensions, graphIterations, forceRelation, forceAmount, r, accuToStrengthBase).interactiveFill((task) -> {
            int v1 = Integer.parseInt(task.item1);
            int v2 = Integer.parseInt(task.item2);
            int off = r.nextInt(offRange) - offMax;
            return new FuzzyComparison(instance, task.item1, task.item2, v2 - v1 + off, Math.abs(off));
        }, connectionCount);

        long endTime = System.currentTimeMillis();
        float seconds = (endTime - startTime) / 1000f;

        int diff = 0;
        for (int i = 0; i < itemCount; i++) {
            diff += Math.abs(Integer.parseInt(result.get(i)) - i + 1);
        }
        double unsortedness = diff / (double) itemCount;
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