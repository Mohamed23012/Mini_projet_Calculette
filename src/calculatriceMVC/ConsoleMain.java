package calculatriceMVC;


import java.util.Scanner;

public class ConsoleMain {
    public static void main(String[] args) {
        
        CalculatorModel m = new CalculatorModel();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!sc.hasNext()) break;
            String cmd = sc.next();
            try {
                switch (cmd) {
                    case "push" -> {
                        if (!sc.hasNextDouble()) {
                            System.out.println("⚠️  Il faut un nombre après 'push'.");
                            sc.nextLine();
                            continue;
                        }
                        double v = sc.nextDouble();
                        m.push(v);
                    }
                    case "pop" -> System.out.println("=> " + m.pop());
                    case "drop" -> m.drop();
                    case "swap" -> m.swap();
                    case "clear" -> m.clear();

                    case "add" -> m.add();
                    case "sub" -> m.substract();
                    case "mul" -> m.multiply();
                    case "div" -> m.divide();
                    case "opp" -> {
                        if (m.isEmpty()) throw new IllegalStateException("Pile vide (opposé)");
                        m.opposite();
                    }

                    case "quit" -> {
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Commande inconnue.");
                }
                System.out.println(m);  
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }
}
