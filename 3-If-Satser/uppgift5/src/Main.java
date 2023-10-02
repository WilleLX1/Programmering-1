import java.awt.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    // ANSI färg koder
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Man inte kan ta ut mer pengar än vad som finns på kontot!
    // Lösenordskontroll…
    // Visa saldo efter uttag
    // Kunna ta ut mer pengar direkt om så önskas
    // Välja på att ta ut pengar eller se saldo
    // Endast ta ut jämna summor
    // Tydliga instruktioner

    public static class User {
        private String name;
        private int pin;
        private int money;

        public User(String name, int pin, int money) {
            this.name = name;
            this.pin = pin;
            this.money = money;
        }

        public String getName() {
            return name;
        }

        public int getPin() {
            return pin;
        }

        public int getMoney() {
            return money;
        }

        public void withdrawMoney(int amount) {
            if (money >= amount) {
                money -= amount;
            }
        }
    }

    public static void main(String[] args) {
        Scanner MenuScanner = new Scanner(System.in);

        // Generera random namn och pinkoder
        int numberOfUsers = 9999; // antalet användare som har pinkod.
        User[] users = generateRandomUsers(numberOfUsers);

        // VARIABLER
        String namn = "Okänd";
        int pinkod = 0;
        boolean inloggad = false;
        int menuVal;
        int uttagVal;
        String uttagIgenVal;
        User loggedInUser = null;


        while (true) {
            // Skriva in PIN
            while (!inloggad) {
                System.out.println("Skriv in din pin: ");
                pinkod = MenuScanner.nextInt();

                for (int i = 0; i < numberOfUsers; i++) {
                    if (pinkod == users[i].getPin()) {
                        namn = users[i].getName();
                        loggedInUser = users[i]; // Spara current user till en variabel
                        System.out.println(GREEN + "Korrekt pinkod för " + namn + "!\n" + RESET);
                        inloggad = true;
                        break;
                    }
                }

                if (!inloggad) {
                    System.out.println(RED + "Fel pin!\n" + RESET);
                }
            }

            // Gå till huvudmenu
            System.out.println("Välkommen " + namn + "!");
            System.out.println("1. Utag av pengar\n2. Visa saldo\n3. Avsluta\n");
            System.out.print("Ange ditt val: ");
            // Få välja mellan alternativ.
            menuVal = MenuScanner.nextInt();
            if (menuVal == 1) {
                // Ta ut pengar
                System.out.println(YELLOW + "\n\nLaddar in...\n\n" + RESET);

                while (true) {
                    // Välj hur mycket att ta ut
                    System.out.println("Hur mycket pengar vill tas ut?");
                    System.out.print("Ange beloppet (100/200/300/400/500): ");

                    uttagVal = MenuScanner.nextInt();

                    // Titta så att det finns tillräckligt och skicka pengarna.
                    for (int i = 0; i < numberOfUsers; i++) {
                        if (users[i].getPin() == pinkod) {
                            if (uttagVal == 100 || uttagVal == 200 || uttagVal == 300 || uttagVal == 400 || uttagVal == 500) {
                                if (users[i].getMoney() >= uttagVal) {
                                    users[i].withdrawMoney(uttagVal);
                                    System.out.println(GREEN + "Uttaget på " + uttagVal + " SEK är godkänt" + RESET);
                                } else {
                                    System.out.println(RED + "ERROR (Inte tillräckligt med pengar)" + RESET);
                                }
                            } else {
                                System.out.println(RED + "\n\nOgiltigt val. Återgår till huvudmenyn.\n" + RESET);
                            }
                            break;
                        }
                    }
                    // Visa saldo
                    System.out.println(YELLOW + "Saldot på " + loggedInUser.name + "s konto är " + loggedInUser.money + " SEK\n" + RESET);


                    // Fråga om ett till uttag.
                    uttagIgenVal = MenuScanner.nextLine(); // Min enter-to-continue tycker på denna.
                    System.out.println("Mer uttag? (j/n)");
                    System.out.print("Ange \"j\" eller \"n\": ");
                    uttagIgenVal = MenuScanner.nextLine();
                    if (uttagIgenVal.equals("j")) {
                        System.out.println("Fortsätter med uttag...");
                    } else if (uttagIgenVal.equals("n")) {
                        System.out.println("Återgår till huvudmenyn...\n\n");
                        break;
                    } else {
                        System.out.println("\n\nOgiltigt val. Återgår till huvudmenyn.\n");
                        break;
                    }
                }

            } else if (menuVal == 2) {
                if (loggedInUser != null) {
                    System.out.println(YELLOW + "\n\nLaddar in...\n\n" + RESET);
                    System.out.println(GREEN + "Saldot för " + loggedInUser.getName() + "s konto är " + loggedInUser.getMoney() + " SEK." + RESET);
                    waitForAnyKeyPress();
                    System.out.println(YELLOW + "\n\nLaddar in...\n\n" + RESET);
                } else {
                    System.out.println(RED + "\n\nERROR (Felaktig förfrågan. Du måste logga in först.)\n" + RESET);
                }
            } else if (menuVal == 3) {
                // Avsluta
                // Logga ut och gå till login skärmen
                // Skicka ut bankkortet
                System.out.println(YELLOW + "\n\nAvslutar...\n\n" + RESET);
                inloggad = false;
            } else {
                System.out.println(RED + "\n\nOgiltigt val. Försök igen.\n\n" + RESET);
            }
        }

    }

    private static void waitForAnyKeyPress() {
        System.out.println(YELLOW + "Tryck på enter för att fortsätta" + RESET);
        try {
            while (true) {
                int key = System.in.read();
                if (key != -1) {
                    break; // Gå ut ur loop när man trycker på esc
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static User[] generateRandomUsers(int count) {
        User[] users = new User[count];
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String name = generateRandomName(random);
            int pin = 1000 + random.nextInt(9000); // Generera ett random 4 nummer för PIN
            int money = 1000 + random.nextInt(9000); // Generera ett random nummer mellan 1000 och 9999 för pengarna.
            users[i] = new User(name, pin, money);
        }

        return users;
    }

    private static String generateRandomName(Random random) {
        String[] availableFirstNames = {
                "Martin", "Ismail", "Youssef", "Khaja", "Pedram", "Neo", "Sammy", "Yousef",
                "Joudi", "Emily", "Hugo", "Elias", "Mike", "Vilmer", "Emil", "Viggo", "Jwan",
                "Khaled", "Timmy", "Benjamin", "Anton", "Samir", "Arvid", "Adam", "Sara",
                "Oscar", "Isak", "Elliot", "Sebastian", "Matin"
        };

        String[] additionalFirstNames = {
                "Sophia", "Olivia", "Emma", "Ava", "Charlotte", "Amelia", "Mia", "Harper", "Evelyn", "Abigail",
                "Emily", "Elizabeth", "Sofia", "Avery", "Ella", "Scarlett", "Grace", "Chloe", "Victoria", "Riley",
                "Aria", "Lily", "Aubrey", "Zoey", "Nora", "Camila", "Hannah", "Layla", "Brooklyn", "Zoe",
                "Leah", "Stella", "Hazel", "Ellie", "Paisley", "Audrey", "Skylar", "Violet", "Claire", "Bella",
                "Aurora", "Lucy", "Anna", "Samantha", "Caroline", "Genesis", "Aaliyah", "Kennedy", "Kinsley"
        };

        int randomIndex = random.nextInt(availableFirstNames.length + additionalFirstNames.length);
        if (randomIndex < availableFirstNames.length) {
            return availableFirstNames[randomIndex];
        } else {
            return additionalFirstNames[randomIndex - availableFirstNames.length];
        }
    }
}