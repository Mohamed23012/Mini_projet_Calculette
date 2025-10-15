package calculatriceMVC;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Pile {
	
	 
private final Deque<Double> stack = new ArrayDeque<>();


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
}
