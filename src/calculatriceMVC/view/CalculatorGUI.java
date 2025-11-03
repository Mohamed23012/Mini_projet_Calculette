package calculatriceMVC.view;

// JavaFX semble ne plus être compatible avec Java23, on utilise donc Swing :
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CalculatorGUI extends JFrame {

    private transient Object controller;

    private final JLabel[] stackLines = new JLabel[5];
    private final JLabel accuLabel = new JLabel("accu = 0", SwingConstants.LEFT);

    private StringBuilder entry = new StringBuilder();
    private boolean entering = false;

    public CalculatorGUI() {
        super("Calculatrice NPI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // En-tête
        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("Calculatrice NPI"), BorderLayout.WEST);
        header.add(accuLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Corps principal
        JPanel body = new JPanel(new BorderLayout(10, 10));

        // Pile
        JPanel stackPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        for (int i = 0; i < stackLines.length; i++) {
            stackLines[i] = new JLabel("", SwingConstants.RIGHT);
            stackPanel.add(stackLines[i]);
        }
        body.add(stackPanel, BorderLayout.NORTH);

        // Pavé numérique
        JPanel numPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        String[][] nums = { {"7","8","9"}, {"4","5","6"}, {"1","2","3"}, {"0",".","+/-"} };
        for (String[] row : nums)
            for (String txt : row) {
                JButton b = new JButton(txt);
                b.addActionListener(e -> onNumPad(txt));
                numPanel.add(b);
            }
        body.add(numPanel, BorderLayout.CENTER);

        // Colonne opérateurs
        JPanel opsPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        String[] ops = { "+", "−", "×", "÷", "±", "PUSH", "DROP", "SWAP", "CLS", "POP" };
        String[] methods = { "onAdd", "onSub", "onMul", "onDiv", "onOpp", "onPush", "onDrop", "onSwap", "onClear", "onPop" };
        for (int i = 0; i < ops.length; i++) {
            JButton b = new JButton(ops[i]);
            String m = methods[i];
            b.addActionListener(e -> invoke(m));
            opsPanel.add(b);
        }
        body.add(opsPanel, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);

        pack(); //Ajuste auto de la fenêtre
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // API Vue / Contrôleur
    public void setController(Object controller) { this.controller = controller; }

    public void setAccum(String accuText) {
        accuLabel.setText("accu = " + (accuText == null ? "0" : accuText));
    }

    public void render(List<Double> topFirst) {
        int n = topFirst.size();
        // on remplit de bas en haut
        for (int i = 0; i < stackLines.length; i++) {
            int index = n - 1 - i; // inverse l’ordre
            String txt = (index >= 0) ? pretty(topFirst.get(index)) : "";
            stackLines[stackLines.length - 1 - i].setText(txt);
        }
        renderEntryOnBottom();
    }


    public boolean hasPendingEntry() { return entering && entry.length() > 0; }

    public Double readAndClearEntryOrNull() {
        if (!hasPendingEntry()) return null;
        try {
            double v = Double.parseDouble(entry.toString().replace(',', '.'));
            entering = false; entry.setLength(0);
            return v;
        } catch (NumberFormatException nfe) {
            entering = false; entry.setLength(0);
            return null;
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // === Gestion de la saisie via pavé numérique ===
    private void onNumPad(String key) {
        switch (key) {
            case "+/-" -> toggleSign();
            case "."   -> appendDot();
            default    -> appendDigit(key);
        }
        renderEntryOnBottom();
    }

    private void appendDigit(String d) {
        if (!entering) { entry = new StringBuilder(); entering = true; }
        if (entry.toString().equals("0")) entry = new StringBuilder();
        entry.append(d);
    }

    private void appendDot() {
        if (!entering) { entry = new StringBuilder("0"); entering = true; }
        if (entry.indexOf(".") < 0 && entry.indexOf(",") < 0) entry.append(".");
    }

    private void toggleSign() {
        if (!entering) { entry = new StringBuilder("0"); entering = true; }
        if (entry.toString().startsWith("-")) entry = new StringBuilder(entry.substring(1));
        else if (!entry.toString().equals("0")) entry.insert(0, "-");
    }

    private void renderEntryOnBottom() {
        if (entering)
            stackLines[stackLines.length - 1].setText(entry.length() == 0 ? "0" : entry.toString());
    }

    private void invoke(String method) {
        if (controller == null) return;
        try { controller.getClass().getMethod(method).invoke(controller); }
        catch (Exception ignored) {}
    }

    private static String pretty(Double d) {
        if (d == null) return "";
        String s = d.toString();
        return s.endsWith(".0") ? s.substring(0, s.length()-2) : s;
    }
}
