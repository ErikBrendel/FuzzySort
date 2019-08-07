package fuzzysort;

import fuzzysort.model.FuzzySortInstance;
import fuzzysort.model.IO;
import fuzzysort.ui.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        //JFileChooser chooser = new JFileChooser();
        //chooser.setFileFilter(new FileNameExtensionFilter("Instances", "fuzz"));
        //int result = chooser.showDialog(null, "Open Instance to load");
        //if (result != JFileChooser.APPROVE_OPTION) return;
        //FuzzySortInstance instance = IO.load(chooser.getSelectedFile());

        FuzzySortInstance instance = IO.load(new File("/media/erik/brot/projects/FuzzySort/acs.fuzz").toPath());
        new MainWindow(instance).setVisible(true);
    }
}
