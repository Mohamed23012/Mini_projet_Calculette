package calculatriceMVC.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName()))
                    UIManager.setLookAndFeel(info.getClassName());
        } catch (Exception ignored) {}

        Color BG = new Color(255, 255, 255);
        Color ECRAN = new Color(200, 220, 200);
        Color TEXTE = Color.BLACK;
        Color OPERATEURS = new Color(70, 110, 255); 
        Font F_TITRE = new Font("SansSerif", Font.BOLD, 16);
        Font F_TEXTE = new Font("Monospaced", Font.PLAIN, 14);
        Font F_BTN = new Font("SansSerif", Font.BOLD, 13);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ECRAN);
        header.setBorder(new EmptyBorder(6, 8, 6, 8));
        JLabel title = new JLabel("Calculatrice NPI");
        title.setFont(F_TITRE);
        title.setForeground(TEXTE);
        accuLabel.setForeground(TEXTE);
        header.add(title, BorderLayout.WEST);
        header.add(accuLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);

        JPanel stackPanel = new JPanel(new GridLayout(5, 1, 2, 2));
        stackPanel.setBackground(ECRAN);
        stackPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        for (int i = 0; i < stackLines.length; i++) {
            stackLines[i] = new JLabel("", SwingConstants.RIGHT);
            stackLines[i].setFont(F_TEXTE);
            stackLines[i].setForeground(TEXTE);
            stackPanel.add(stackLines[i]);
        }
        body.add(stackPanel, BorderLayout.NORTH);

        JPanel numPanel = new JPanel(new GridLayout(4, 3, 4, 4));
        numPanel.setOpaque(false);
        String[][] nums = { {"7","8","9"}, {"4","5","6"}, {"1","2","3"}, {"0",".","+/-"} };
        for (String[] row : nums)
            for (String txt : row) {
                JButton b = new JButton(txt);
                b.setFont(F_BTN);
                b.setForeground(TEXTE);
                b.setBackground(new Color(192, 192, 192));
                b.setMargin(new Insets(4, 4, 4, 4));
                b.addActionListener(e -> onNumPad(txt));
                numPanel.add(b);
            }
        body.add(numPanel, BorderLayout.CENTER);

        JPanel opsPanel = new JPanel(new GridLayout(10, 1, 4, 4));
        opsPanel.setOpaque(false);
        String[] ops = { "+", "−", "×", "÷", "±", "PUSH", "DROP", "SWAP", "POP", "CLS" };
        String[] methods = { "onAdd", "onSub", "onMul", "onDiv", "onOpp", "onPush", "onDrop", "onSwap", "onPop", "onClear" };
        for (int i = 0; i < ops.length; i++) {
            JButton b = new JButton(ops[i]);
            b.setFont(F_BTN);
            b.setForeground(TEXTE);
            b.setBackground((i < 4) ? OPERATEURS : new Color(255, 210, 80));
            b.setFocusPainted(false);
            b.setMargin(new Insets(4, 4, 4, 4));
            String m = methods[i];
            b.addActionListener(e -> invoke(m));
            opsPanel.add(b);
        }
        body.add(opsPanel, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void setController(Object controller) { this.controller = controller; }

    public void setAccum(String accuText) {
        accuLabel.setText("accu = " + (accuText == null ? "0" : accuText));
    }

    public void render(List<Double> topFirst) {
        int n = topFirst.size();
        for (int i = 0; i < stackLines.length; i++) {
            int index = n - 1 - i;
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
