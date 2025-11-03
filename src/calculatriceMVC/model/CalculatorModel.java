package calculatriceMVC.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import calculatriceMVC.controler.CalculatorControlerInterface;

public class CalculatorModel implements CalculatorModelInterface {

    private final Deque<Double> stack = new ArrayDeque<>();
    private double accum;

    // ---- Observateur (le contrôleur) ----
    private CalculatorControlerInterface listener;

    /** Brancher l'observateur (contrôleur) */
    public void setListener(CalculatorControlerInterface listener) {
        this.listener = listener;
        notifyChange(); // pousse l'état initial à la vue
    }

    /** Notifie le contrôleur à chaque changement */
    private void notifyChange() {
        if (listener != null) {
            listener.change(String.valueOf(accum));
            listener.change(toListTopFirst());
        }
    }

    
    @Override
    public void push(double v) {
        stack.push(v);
        notifyChange();
    }

    @Override
    public double pop() {
        if (stack.isEmpty()) throw new IllegalStateException("Pile vide (pop)");
        double v = stack.pop();
        accum=v;
        notifyChange();
        return v;
        
    }

    @Override
    public void drop() {
        if (stack.isEmpty()) throw new IllegalStateException("Pile vide (drop)");
        stack.pop();
        notifyChange();
    }

    @Override
    public void swap() {
        if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 éléments pour swap");
        double a = stack.pop();
        double b = stack.pop();
        stack.push(a);
        stack.push(b);
        notifyChange();
    }

    @Override
    public void clear() {
        stack.clear();
        accum = 0;       // logique : on remet l'accumulateur à zéro
        notifyChange();
    }

    public boolean isEmpty() { return stack.isEmpty(); }
    public int size() { return stack.size(); }
    public List<Double> toListTopFirst() { return new ArrayList<>(stack); }

    @Override
    public void add() {
        if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 opérandes");
        double b = stack.pop();
        double a = stack.pop();
        accum = a + b;
        stack.push(accum);
        notifyChange();
    }

    @Override
    public void substract() {
        if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 opérandes");
        double b = stack.pop();
        double a = stack.pop();
        accum = a - b;
        stack.push(accum);
        notifyChange();
    }

    @Override
    public void multiply() {
        if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 opérandes ");
        double b = stack.pop();
        double a = stack.pop();
        accum = a * b;
        stack.push(accum);
        notifyChange();
    }

    @Override
    public void divide() {
        if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 opérandes");
        double b = stack.pop();
        double a = stack.pop();
        if (b == 0) throw new IllegalStateException("On ne peut pas diviser par zéro");
        accum = a / b;
        stack.push(accum);
        notifyChange();
    }

    @Override
    public void opposite() {
        if (stack.isEmpty()) throw new IllegalStateException("Pile vide (opposé)");
        double a = stack.pop();
        accum = -a;
        stack.push(accum);
        notifyChange();
    }

    @Override
    public String toString() {
        return "CalculatorModel [accum=" + accum + "]";
    }
}
