package fuzzysort.ui;

import fuzzysort.model.FuzzyComparison;
import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.ToCompare;
import fuzzysort.solver.Graph;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CompareDialog {
    private final FuzzySortInstance instance;
    private final ToCompare toCompare;
    private final JOptionPane optionPane;
    private final JSlider compareSlider;
    private final JSlider accuracySlider;


    public CompareDialog(FuzzySortInstance instance, JFrame parent, ToCompare toCompare) {
        this.instance = instance;
        this.toCompare = toCompare;

        optionPane = new JOptionPane();
        compareSlider = getSlider(-10, 10, 0);
        accuracySlider = getSlider(0, FuzzyComparison.ACCURACY_VAGUE, FuzzyComparison.ACCURACY_MIDDLE);
        optionPane.setMessage(new Object[] {
                "Which is better? (" + toCompare.item1 + " - " + toCompare.item2 + ")",
                compareSlider,
                "",
                "How sure are you? (absolutely - very vague)",
                accuracySlider});
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.YES_OPTION);
        JDialog dialog = optionPane.createDialog(parent, "Compare these two");
        dialog.setVisible(true);
    }

    private static JSlider getSlider(float min, float max, float initial) {
        JSlider slider = new JSlider();
        slider.setMinimum(Math.round(min * 100));
        slider.setMaximum(Math.round(max * 100));
        slider.setValue(Math.round(initial * 100));
        return slider;
    }

    public FuzzyComparison getValue() {
        optionPane.getInputValue();
        return new FuzzyComparison(instance, toCompare.item1, toCompare.item2,
                compareSlider.getValue() * 0.01f, accuracySlider.getValue() * 0.01f);
    }
}
