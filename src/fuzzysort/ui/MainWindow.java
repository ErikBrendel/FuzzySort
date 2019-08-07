package fuzzysort.ui;

import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.IO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;

public class MainWindow extends JFrame {
    private FuzzySortInstance instance;

    public MainWindow(FuzzySortInstance instance) {
        this.instance = instance;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setTitle("Fuzzy Sort");
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        addButton("Save", () -> {
            IO.save(instance);
        });
        addButton("Show Items", () -> {
            JOptionPane.showMessageDialog(this, String.join("\n", instance.getItems()), "Items", JOptionPane.PLAIN_MESSAGE);
        });
    }

    private void addButton(String title, Runnable handler) {
        JButton btn = new JButton(title);
        btn.addActionListener(actionEvent -> handler.run());
        add(btn);
    }
}
