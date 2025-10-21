package calculatriceMVC;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorModel model = new CalculatorModel();
            CalculetteView view   = new CalculetteView();
            new CalculetteController(model, view);  
            view.setVisible(true);
        });
    }
}