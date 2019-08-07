package fuzzysort.solver;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.ToCompare;
import fuzzysort.ui.GraphDisplay;
import fuzzysort.ui.SortDisplay;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fuzzysort.meta.ExampleGenerator.VisualsEnabled;

public class Solver {
    private final FuzzySortInstance instance;
    private final int graphIterations;
    private final Random r;
    private final float accuToStrengthBase;
    private final float forceAmount;
    private final Graph graph;

    public Solver(FuzzySortInstance instance, int dimensions, int graphIterations, float forceRelation, float forceAmount, Random r, float accuToStrengthBase) {
        this.instance = instance;
        this.forceAmount = forceAmount;
        this.graphIterations = graphIterations;
        this.r = r;
        this.accuToStrengthBase = accuToStrengthBase;
        this.graph = new Graph(dimensions, forceRelation, r);
        for (FuzzyComparison comp: instance.getComparisons()) {
            this.graph.connect(this.graph.getNode(comp.item1), this.graph.getNode(comp.item2),
                    relationToDiff(comp.relation), accuracyToStrength(comp.accuracy));
        }
        if (VisualsEnabled) {
            new GraphDisplay(this.graph).showWindow();
            new SortDisplay(this.graph).showWindow();
        }
    }

    public List<String> interactiveFill(Function<ToCompare, FuzzyComparison> input, int connections) {
        for (int c = 0; c < connections; c++) {
            if (VisualsEnabled) {
                System.out.println(String.join(",", graph.getItemOrder()));
            }
            fillConnection(input);
            solve(graphIterations, forceAmount);
        }
        for (float f = 1; f > 0; f -= 0.0001f) {
            solve(graphIterations / 10, forceAmount * f);
        }
        if (VisualsEnabled) {
            System.out.println("Final order: " + String.join(",", graph.getItemOrder()));
        }
        return graph.getItemOrder();
    }

    private void fillConnection(Function<ToCompare, FuzzyComparison> input) {
        // if any item is not connected to the trunk (defined by the first item of the instance)
        // connect it to the middlemost item of the trunk
        List<String> items = instance.getItems();
        int itemCount = items.size();
        Graph.Node n1 = graph.getNode(items.get(0));
        String newItem = null;
        for (String item : items) {
            if (!graph.areConnected(n1, graph.getNode(item))) {
                newItem = item;
                break;
            }
        }
        if (newItem != null) {
            List<String> trunkOrder = graph.getItemOrder().stream()
                    .filter((item) -> graph.areConnected(n1, graph.getNode(item)))
                    .collect(Collectors.toList());
            String middleItem = trunkOrder.get(trunkOrder.size()/2);
            addComparison(input.apply(new ToCompare(newItem, middleItem)));
            return;
        }

        // otherwise, for each node, find the one the most away logically,
        // and connect a random pair of the farthest nodes

        AtomicInteger furthest = new AtomicInteger(1);
        Set<Graph.NodePair> farAwayCandidates = new HashSet<>(itemCount * itemCount / 2);
        for (String item: items) {
            Graph.Node source = graph.getNode(item);
            graph.search(source, (node, depth) -> {
                if (node == source) return;
                if (depth > furthest.get()) {
                    furthest.set(depth);
                    farAwayCandidates.clear();
                }
                farAwayCandidates.add(new Graph.NodePair(source, node));
            });
        }

        //System.out.println("depth connection: " + furthest.get() + ", found " + farAwayCandidates.size() + " candidates");
        List<Graph.NodePair> candidates = new ArrayList<>(farAwayCandidates);
        Graph.NodePair nodePair = candidates.get(r.nextInt(candidates.size()));
        addComparison(input.apply(new ToCompare(nodePair.n1.item, nodePair.n2.item)));
    }

    private void addComparison(FuzzyComparison comp) {
        instance.addComparison(comp);
        graph.connect(this.graph.getNode(comp.item1), this.graph.getNode(comp.item2),
                relationToDiff(comp.relation), accuracyToStrength(comp.accuracy));
    }

    public void solve(int iterations, float forceAmount) {
        for (int i = 0; i < iterations; i++) {
            solveStep(forceAmount);
            if (VisualsEnabled && i % 50 == 0) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void solveStep(float forceAmount) {
        graph.solveForces(forceAmount);
    }

    private static float relationToDiff(float relation) {
        return relation;
    }

    private float accuracyToStrength(float accuracy) {
        return (float) Math.pow(accuToStrengthBase, -accuracy);
    }

}
