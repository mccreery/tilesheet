package tilesheet;

import javax.swing.JOptionPane;

import tilesheet.ArgsHandler;

public class Main {
    public static void main(String[] args) {
        ArgsHandler argsHandler = new ArgsHandler(args);

        if(argsHandler.shouldShowGui()) {
            JOptionPane.showMessageDialog(null, "gui");
        } else if(argsHandler.isHelpCommand()) {
            argsHandler.printHelp();
        } else if(argsHandler.getImageFiles().isEmpty()) {
            System.err.println("error: no images specified");
            argsHandler.printHelp();
        } else {
            System.out.println(argsHandler.getContext().getDetails());
            System.out.println();
        }
    }
}
