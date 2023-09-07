import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scan = new Scanner (System.in);
        String userFirstName;
        String userLastName;
        System.out.print("Skriv ditt förnamn:\n");
        userFirstName = scan.nextLine();
        System.out.print("Skriv ditt efternamn:\n");
        userLastName = scan.nextLine();
        System.out.print("Hello Mr " + userLastName + ", " + userFirstName + " " + userLastName);
    }
}