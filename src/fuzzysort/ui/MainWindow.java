package fuzzysort.ui;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.IO;
import fuzzysort.model.ToCompare;
import fuzzysort.solver.Solver;

import javax.swing.*;
import java.util.Random;
import java.util.function.Function;

import static fuzzysort.ui.Display2D.EqualScaleDimensions;

public class MainWindow extends JFrame {

    private final FuzzySortInstance instance;
    private Random r = new Random();
    private Solver solver;
    private Thread animatorThread = null;

    private int passiveAnimationsPerFrame = 20;
    private float passiveAnimationsStrength = 0.1f;

    public MainWindow(FuzzySortInstance instance) {
        this.instance = instance;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setTitle("Fuzzy Sort");
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        addButton("Save", () -> {
            IO.save(instance);
        });
        addButton("Show Items", () -> {
            JOptionPane.showMessageDialog(this, String.join("\n", instance.getItems()), "Items", JOptionPane.PLAIN_MESSAGE);
        });
        addButton("Set Seed", () -> {
            r = new Random(getInputLong("Seed", 42));
        });
        addButton("Set Solver", () -> {
            solver = new Solver(instance,
                    getInputInt("Dimensions", 1),
                    getInputInt("GraphIterations", 200),
                    getInputFloat("ForceRelation", 0.9f),
                    getInputFloat("ForceAmount", 5f), r,
                    getInputFloat("accuToStrengthBase", 2));
        });
        addButton("Solve", () -> {
            solver.interactiveFill(GetUserCompareFunc(), getInputInt("Connections", 1));
        });
        addButton("Show Graph", () -> {
            new GraphDisplay(solver.graph).showWindow();
        });
        addButton("Animate Graph", () -> {
            solver.solve(getInputInt("Iterations", 200), getInputFloat("ForceAmount", 10));
        });
        addButton("Toggle passive animation", () -> {
            if (animatorThread == null) {
                animatorThread = new Thread(() -> {
                    while (animatorThread != null) {
                        solver.solve(passiveAnimationsPerFrame, passiveAnimationsStrength);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                animatorThread.start();
            } else {
                animatorThread = null;
            }
        });
        addButton("Passive animation settings", () -> {
            passiveAnimationsPerFrame = getInputInt("steps per frame", passiveAnimationsPerFrame);
            passiveAnimationsStrength = getInputFloat("animation strength", passiveAnimationsStrength);
        });
        addButton("Disturb", () -> {
            solver.graph.disturb(getInputFloat("Strength", 10));
        });
        addButton("Toggle Equal Scale", () -> {
            EqualScaleDimensions = !EqualScaleDimensions;
        });
    }

    private Function<ToCompare, FuzzyComparison> GetUserCompareFunc() {
        return (pair) -> new CompareDialog(instance, this, pair).getValue();
    }

    private float getInputFloat(String message, float defaultValue) {
        return Float.parseFloat(getInput(message, String.valueOf(defaultValue)));
    }
    private int getInputInt(String message, int defaultValue) {
        return Integer.parseInt(getInput(message, String.valueOf(defaultValue)));
    }
    private long getInputLong(String message, long defaultValue) {
        return Long.parseLong(getInput(message, String.valueOf(defaultValue)));
    }
    private String getInput(String message, String defaultValue) {
        return JOptionPane.showInputDialog(this, message, defaultValue);
    }

    private void addButton(String title, Runnable handler) {
        JButton btn = new JButton(title);
        btn.addActionListener(actionEvent -> new Thread(handler).start());
        add(btn);
    }
}
