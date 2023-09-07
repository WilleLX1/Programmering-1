public class Main {
    public static void main(String[] args)
    {
        System.out.println("Hello world!"); // Som vanlig print
        System.out.print("Hello world without ln!"); // vanlig print
        System.out.print("\"Hello jorden!\""); // Lägger till på förra printen
        System.out.print("\n\nHello\njorden!"); // Gå ner 2 rader och sepera ord
        System.out.print("\033[1;92m \nGrön färg!"); // Färg :D
        System.out.print("\033[0m\nIngen färg..."); // Ingen Färg... D:
        System.out.print("\n" + 3.14); // Siffra med decimaler... Wow

        System.out.print("\nAgent 00" + 7 + " - James Bond\n\n"); // Skriver text som includerar nummer
        String namn = "NTI Gymnasiet"; // skapar en ny string-variabel som heter: "namn" med datan: "NTI Gymnasiet"
        int year = 2022; // Skapar en ny integer-variabel som heter: "year" och har datan: "2022"
        System.out.println(namn + year); // Combinerar "name" och "year" variablerna och skriver resultatet.
    }
}