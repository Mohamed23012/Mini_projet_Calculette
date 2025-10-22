package calculatriceMVC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class CalculetteView extends JFrame {

    //  Thème 
    private static final Color BG       = new Color(0x0F, 0x14, 0x1A); // fond fenêtre
    private static final Color CARD     = new Color(0x19, 0x1F, 0x29); // cartes/panneaux
    private static final Color ACCENT   = new Color(0x62, 0xA8, 0xE5); // bleu clair
    private static final Color MUTED    = new Color(0x8A, 0x95, 0xA5); // texte secondaire
    private static final Color BTN_BG   = new Color(0x21, 0x28, 0x36);
    private static final Color BTN_BG_H = new Color(0x28, 0x31, 0x44);
    private static final Color BTN_OP   = new Color(0x2D, 0x37, 0x4C);
    private static final Font  FONT_BASE= new Font("Inter", Font.PLAIN, 14);
    private static final Font  FONT_MONO= new Font("JetBrains Mono", Font.PLAIN, 16);
    private static final int   RADIUS   = 12;

    // --- Contrôleur (on garde l’invocation par nom de méthode) ---
    private transient Object controller;

    // --- UI pile (5 lignes) + accroche “accu” ---
    private final JLabel[] stackLines = new JLabel[5];
    private final JLabel accuLabel = new JLabel("accu = 0", SwingConstants.LEFT);

    // --- Saisie locale ---
    private StringBuilder entry = new StringBuilder();
    private boolean entering = false;

    public CalculetteView() {
        super("Calculette NPI");
        // Nimbus (intégré au JRE) pour un rendu + propre
        try { for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) UIManager.setLookAndFeel(info.getClassName()); } catch (Exception ignored) {}
        // Couleurs globales
        getContentPane().setBackground(BG);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(14, 14));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(16, 16, 16, 16));

        // --- En-tête ---
        JPanel header = card();
        header.setLayout(new BorderLayout());
        JLabel title = new JLabel("🧮  Calculatrice NPI", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(FONT_BASE.deriveFont(Font.BOLD, 18f));
        header.add(title, BorderLayout.WEST);

        accuLabel.setForeground(MUTED);
        accuLabel.setFont(FONT_BASE);
        header.add(accuLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Bloc centre : pile + clavier + colonne opérateurs ---
        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setOpaque(false);

        // Pile (carte)
        JPanel stackCard = card();
        stackCard.setLayout(new BorderLayout(8, 8));

        JLabel stackTitle = new JLabel("Pile (top → bottom)");
        stackTitle.setForeground(MUTED);
        stackTitle.setFont(FONT_BASE);
        stackCard.add(stackTitle, BorderLayout.NORTH);

        JPanel stackPanel = new JPanel(new GridLayout(5, 1, 8, 8));
        stackPanel.setOpaque(false);
        for (int i = 0; i < stackLines.length; i++) {
            JLabel l = new JLabel("", SwingConstants.RIGHT);
            l.setOpaque(true);
            l.setBackground(new Color(0x22, 0x2A, 0x39));
            l.setForeground(Color.WHITE);
            l.setFont(FONT_MONO.deriveFont(16f));
            l.setBorder(new EmptyBorder(8, 12, 8, 12));
            l.setPreferredSize(new Dimension(260, 32));
            stackLines[i] = l;
            stackPanel.add(l);
        }
        stackCard.add(stackPanel, BorderLayout.CENTER);

        body.add(stackCard, BorderLayout.NORTH); // au-dessus

        // Pavé numérique
        JPanel numCard = card();
        numCard.setLayout(new GridLayout(4, 3, 8, 8));
        String[][] nums = { {"7","8","9"}, {"4","5","6"}, {"1","2","3"}, {"0",".","+/-"} };
        for (String[] row : nums) for (String txt : row) {
            JButton b = pillButton(txt);
            b.addActionListener(e -> onNumPad(txt));
            numCard.add(b);
        }
        body.add(numCard, BorderLayout.CENTER);

        // Colonne opérateurs
        JPanel opsCard = card();
        opsCard.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0; gc.insets = new Insets(4,4,4,4);

        JButton bPlus  = opButton("+");
        JButton bMinus = opButton("−");
        JButton bMul   = opButton("×");
        JButton bDiv   = opButton("÷");
        JButton bOpp   = pillButton("±");
        JButton bPush  = pillButton("PUSH");
        JButton bDrop  = pillButton("DROP");
        JButton bSwap  = pillButton("SWAP");
        JButton bClear = pillButton("CLS");
        JButton bPop   = pillButton("POP");

        bPlus.addActionListener(e -> invoke("onAdd"));
        bMinus.addActionListener(e -> invoke("onSub"));
        bMul.addActionListener(e -> invoke("onMul"));
        bDiv.addActionListener(e -> invoke("onDiv"));
        bOpp.addActionListener(e -> invoke("onOpp"));
        bPush.addActionListener(e -> invoke("onPush"));
        bDrop.addActionListener(e -> invoke("onDrop"));
        bSwap.addActionListener(e -> invoke("onSwap"));
        bClear.addActionListener(e -> invoke("onClear"));
        bPop.addActionListener(e -> invoke("onPop"));

        int y = 0;
        gc.gridy = y++; opsCard.add(bPlus, gc);
        gc.gridy = y++; opsCard.add(bMinus, gc);
        gc.gridy = y++; opsCard.add(bMul, gc);
        gc.gridy = y++; opsCard.add(bDiv, gc);
        gc.gridy = y++; opsCard.add(bOpp, gc);
        gc.gridy = y++; opsCard.add(bPush, gc);
        gc.gridy = y++; opsCard.add(bDrop, gc);
        gc.gridy = y++; opsCard.add(bSwap, gc);
        gc.gridy = y++; opsCard.add(bClear, gc);
        gc.gridy = y++; gc.weighty = 1.0; opsCard.add(Box.createVerticalGlue(), gc);
        gc.gridy = y;   gc.weighty = 0.0; opsCard.add(bPop, gc);

        body.add(opsCard, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);

        // --- Barre de statut ---
        JPanel status = card();
        status.setLayout(new BorderLayout());
        JLabel hint = new JLabel("Entrée: chiffres • Enter=PUSH • Backspace=POP • C=clear • S=swap • D=drop");
        hint.setForeground(MUTED);
        hint.setFont(FONT_BASE.deriveFont(12f));
        status.add(hint, BorderLayout.WEST);
        add(status, BorderLayout.SOUTH);

        // Raccourcis clavier
        addKeyBindings(body);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().putClientProperty("JComponent.sizeVariant", "regular");
    }

    // ---------- API vue/contrôleur ----------
    public void setController(Object controller) { this.controller = controller; }

    public void setAccum(String accuText) {
        accuLabel.setText("accu = " + (accuText == null ? "0" : accuText));
    }

    public void render(List<Double> topFirst) {
        for (int i = 0; i < stackLines.length; i++) {
            String txt = (i < topFirst.size()) ? pretty(topFirst.get(i)) : "";
            stackLines[i].setText(txt);
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

    // ---------- Saisie ----------
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
        if (entering) stackLines[stackLines.length - 1].setText(entry.length() == 0 ? "0" : entry.toString());
    }

    // ---------- Raccourcis ----------
    private void addKeyBindings(JComponent root) {
        root.setFocusable(true);
        root.requestFocusInWindow();
        root.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c)) { onNumPad(String.valueOf(c)); return; }
                if (c == '.' || c == ',') { onNumPad("."); return; }
                if (c == '+') { invoke("onAdd"); return; }
                if (c == '-') { invoke("onSub"); return; }
                if (c == '*') { invoke("onMul"); return; }
                if (c == '/') { invoke("onDiv"); return; }
                if (c == 'p' || c == 'P') { invoke("onPush"); return; }
                if (c == 'c' || c == 'C') { invoke("onClear"); return; }
                if (c == 's' || c == 'S') { invoke("onSwap"); return; }
                if (c == 'd' || c == 'D') { invoke("onDrop"); return; }
            }
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)      invoke("onPush");
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) invoke("onPop");
            }
        });
    }

    // ---------- Helpers UI ----------
    private JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // léger “glow” en haut
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),RADIUS,RADIUS);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        return p;
    }

    private JButton pillButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(BTN_BG);
        b.setFont(FONT_BASE.deriveFont(14f));
        b.setPreferredSize(new Dimension(72, 44));
        b.setBorder(new LineBorder(new Color(0x33,0x3B,0x4B), 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addChangeListener(e -> b.setBackground(b.getModel().isRollover() ? BTN_BG_H : BTN_BG));
        return b;
    }

    private JButton opButton(String text) {
        JButton b = pillButton(text);
        b.setBackground(BTN_OP);
        b.setBorder(new LineBorder(ACCENT, 1, true));
        return b;
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
