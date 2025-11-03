package calculatriceMVC;

import javax.swing.SwingUtilities;

import calculatriceMVC.controler.CalculatorControler;
import calculatriceMVC.model.CalculatorModel;
import calculatriceMVC.view.CalculatorGUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorModel model = new CalculatorModel();
            CalculatorGUI view   = new CalculatorGUI();
            new CalculatorControler(model, view);  
            view.setVisible(true);
        });
    }
}