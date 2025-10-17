package calculatriceMVC;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CalculatorModel implements CalculatorModelInterface {
		 
	private final Deque<Double> stack = new ArrayDeque<>();
	private double accum;
	
	public void push(double v) { stack.push(v); }
	
	
	public double pop() {
	if (stack.isEmpty()) throw new IllegalStateException("Pile vide (pop)");
	return stack.pop();
	}
	
	
	public void drop() {
	if (stack.isEmpty()) throw new IllegalStateException("Pile vide (drop)");
	stack.pop();
	}
	
	
	public void swap() {
	if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 éléments pour swap");
	double a = stack.pop();
	double b = stack.pop();
	stack.push(a);
	stack.push(b);
	}
	
    public void clear() { stack.clear(); }
	public boolean isEmpty() { return stack.isEmpty(); }
	public int size() { return stack.size(); }
	public List<Double> toListTopFirst() { return new ArrayList<>(stack); }	
	
	public void add() {
	    if (stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 opérandes");
	    double b = stack.pop();
	    double a = stack.pop();
	    accum = a + b;
	    stack.push(accum);
	}
	public void substract() {
		if(stack.size() < 2) throw new IllegalStateException("Besoin d'au moins 2 opérandes");
		double b =stack.pop();
		double a = stack.pop();
		accum= a - b ;
		stack.push(accum);
	}
	public void multiply() {
		if(stack.size() <2) throw new IllegalStateException("Besoin d'au moins 2 opérande ");
		double b=stack.pop();
		double a=stack.pop();
		accum = a*b;
		stack.push(accum);
		}
	public void divide() {
		if(stack.size() <2) throw new IllegalStateException("Besoin  d'au moins 2 opérande");
		double b =stack.pop();
		double a=stack.pop();
		if(b == 0) throw new IllegalStateException("On ne peut pas de diviser sur zéro");
		accum = a/b;
		stack.push(accum);
		}
	@Override
	public String toString() {
		return "CalculatorModel [accum=" + accum + "]";
	}
    public void opposite() {
		double a =stack.pop();
		accum = -a;
		stack.push (accum);
		}
}