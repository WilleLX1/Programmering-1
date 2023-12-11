import java.util.Scanner;

public class uppgift5b {
    public static void main(String args[]) {
        String svar;
        Scanner scan = new Scanner(System.in);
        do {
            System.out.println("Vill du spela igen? J/N");
            svar = scan.nextLine();
        } while (svar.equals("j"));
    }
}