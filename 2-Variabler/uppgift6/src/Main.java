import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scan = new Scanner (System.in);
        Integer RakneSatt;
        System.out.print("Welcome!\nVilket räknesätt vill du använda?\n1. Addition\n2. Subtraktion\n3. Multiplikation\n4. Divition\nVälj via att skriva antigen 1, 2, 3 eller 4!\nSkriv här: ");
        RakneSatt = scan.nextInt();
        if (RakneSatt == 1)
        {
            System.out.print("\nDu har valt addition! :D\n");
        } else if (RakneSatt == 2)
        {
            System.out.print("\nDu har valt subtraktion! :D\n");
        } else if (RakneSatt == 3)
        {
            System.out.print("\nDu har valt multiplikation! :D\n");
        } else if (RakneSatt == 4)
        {
            System.out.print("\nDu har valt Divition! :D\n");
        }

        Double Tal1;
        Double Tal2;
        Double Summa;

        System.out.print("Skriv in ett värde: ");
        Tal1 = scan.nextDouble ( );
        System.out.print("Skriv in ett till värde: ");
        Tal2 = scan.nextDouble ( );

        if (RakneSatt == 1) {
            Summa = Tal1 + Tal2;
            System.out.println("Svaret på " + Tal1 + " + " + Tal2 + " är " + Summa);
        } else if (RakneSatt == 2) {
            Summa = Tal1 - Tal2;
            System.out.println("Svaret på " + Tal1 + " - " + Tal2 + " är " + Summa);
        } else if (RakneSatt == 3) {
            Summa = Tal1 * Tal2;
            System.out.println("Svaret på " + Tal1 + " * " + Tal2 + " är " + Summa);
        } else if (RakneSatt == 4) {
            Summa = Tal1 / Tal2;
            System.out.println("Svaret på " + Tal1 + " / " + Tal2 + " är " + Summa);
        } else {
            System.out.println("Invalid operation selected.");
            return;
        }
    }
}