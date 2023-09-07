
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scan = new Scanner (System.in);
        double C; // Celsius
        Integer F; // Fahrenheit

        System.out.print("Ange ett nummer i Fahrenheit: ");
        F = scan.nextInt();

        C = ( F - 32 ) / 1.8;

        System.out.print("Här är " + F + "°F i celsius: " + C + "°C");

    }
}