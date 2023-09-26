import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        // Skapa en Scanner för att ta emot användarens inmatning
        Scanner scanner = new Scanner(System.in);

        // I denna uppgift vill jag att ni skall skapa en berättelse åt mig.
        // Jag skall som användare få svara på några frågor om mig själv eller om saker som intresserar mig.
        // Därefter är det er uppgift att bygga en berättelse till mig som innehåller de element
        // som jag tidigare har fått ange. För att klara av uppgiften måste ni använda er av Scanner
        // samt variabler. Titta gärna på videon nedan för att få idé om hur uppgiften kan se ut.
        // Jag vill att ni använder er av kommentarer i er kod, för att beskriva olika delar av
        // koden för er själva men även för mig.

        // Börja med att fråga användaren för input till textens genre.
        // Fråga användaren om deras namn
        System.out.print("Vad är ditt namn? ");
        String namn = scanner.nextLine();

        // Fråga användaren om deras ålder
        System.out.print("Hur gammal är du? ");
        int alder = scanner.nextInt();
        scanner.nextLine(); // Rensa newline-karaktären

        // Fråga användaren om deras intresse
        System.out.print("Vad är ditt största intresse? ");
        String intresse = scanner.nextLine();

        // Skapa en berättelse baserad på användarens svar
        System.out.println("\nHär är din personliga berättelse:");
        System.out.println("Det var en gång en person som hette " + namn + ".");
        System.out.println(namn + " var " + alder + " år gammal och älskade " + intresse + ".");
        System.out.println("En dag bestämde sig " + namn + " för att utforska världen och upptäcka nya äventyr.");
        System.out.println("Och så levde " + namn + " lycklig i många år.");

        // Stäng Scanner
        scanner.close();
    }
}