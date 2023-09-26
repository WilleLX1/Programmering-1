import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        System.out.println("Skriv en siffra mellan 1 och 100 och om du har rätt får du fly härifrån");

        // Starta scanners
        Scanner scan = new Scanner (System.in);
        Scanner scan2 = new Scanner (System.in);

        // Variabler
        int SlumpatNummer;
        int GissatNummer;
        int AntalGissningar = 1;
        String VDF_Svar;

        while (true)
        {
            // Vill du fortsätta text.
            if (AntalGissningar > 1)
            {
                char choice = getContinueChoice(scan2);
                if (choice == 'N') {
                    return;
                }
            }
            SlumpatNummer = ((int)(Math.random( )*100+1));

            System.out.println("Skriv ett nummer mellan 1 och 100:");
            GissatNummer = scan.nextInt();

            if (SlumpatNummer < GissatNummer){
                System.out.println("Slumpat nummer är mindre. (" + SlumpatNummer + ")");
                AntalGissningar++;
            } else if (SlumpatNummer > GissatNummer) {
                System.out.println("Slumpat nummer är större. (" + SlumpatNummer + ")");
                AntalGissningar++;
            } else if (SlumpatNummer == GissatNummer) {
                System.out.println("Du hade rätt! (" + SlumpatNummer + ")");
                if (AntalGissningar == 1){
                    System.out.println("Woah, du klarade det på första försöket... Väldigt snyggt!");
                }
                else {
                    System.out.println("Det tog bara " + AntalGissningar + " gånger!");
                }
                break;
            }
        }
    }
    private static char getContinueChoice(Scanner scan) {
        char val;
        while (true) {
            System.out.println("Vill du forsätta? (J/N)");
            val = scan.nextLine().trim().toUpperCase().charAt(0);
            if (val == 'J' || val == 'N') {
                System.out.println(val == 'J' ? "Forsätter..." : "Stänger av (Stäng inte av datorn...)");
                break;
            } else {
                System.out.println("Skriv \"J\" eller \"N\".");
            }
        }
        return val;
    }
}
