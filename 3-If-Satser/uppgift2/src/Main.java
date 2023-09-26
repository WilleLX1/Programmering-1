import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        // Start scanner 1
        Scanner scan = new Scanner (System.in);
        String Svar;

        while (true){
            System.out.println("Vill du fortsätta? (J/N)");
            Svar = scan.nextLine();
            if (Svar.equals("J")){
                System.out.println("Du har fortsatt...");
                // Fortsätt koden här igentligen.

            } else if (Svar.equals("N")) {
                System.out.println("Avslutar...");
                break;
            }
            else {
                System.out.println("Du har inte skrivet J eller N, kom ihåg att den är case sentive.");
            }
        }

    }
}