package calculatriceMVC;

public interface CalculatorModelInterface {
	public void push(double v);
	public double pop();
	public void drop();
	public void swap();
	public void clear();
	public double add(double num1 , double num2);
	public double substract(double num1 , double num2);
	public void multiply (double num1 , double num2);
	public void divide (double num1 , double num2);
	public void opposite(double num);
}
