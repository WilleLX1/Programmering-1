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

    public static void main(String[] args) {
        // Start scanners
        Scanner MenuScanner = new Scanner(System.in);
        Scanner GameScanner = new Scanner(System.in);

        // Variables
        int firstChoice;
        int playerMoney = 1000;
        String playerHitOrStandChoice;
        int playerBetAmount;

        // Menu
        System.out.println("Välkommen till Blackjack (21)!");
        System.out.println("Alternativ:\n1. Spela\n2. Inställningar\n3. Regler");
        firstChoice = MenuScanner.nextInt();

        if (firstChoice == 1) {
            while (playerMoney > 0) {
                System.out.println("Startar spel...");

                // Låt spelaren bestämma hur mycket att betta
                while (true) {
                    System.out.println("How much do you want to bet? (You have " + playerMoney + ")");
                    playerBetAmount = GameScanner.nextInt();
                    GameScanner.nextLine(); // Consume the newline character
                    if (playerBetAmount > 0 && playerBetAmount <= playerMoney) {
                        System.out.println("Betting amount set to: " + playerBetAmount);
                        break;
                    } else {
                        System.out.println("Invalid bet amount. Please enter a valid amount.");
                    }
                }

                // Ge både spelaren och dealern (AI) 2 kort var.
                int playerCardCount;
                do {
                    playerCardCount = drawCard() + drawCard();
                } while (playerCardCount >= 22);

                int AICardCount;
                do {
                    AICardCount = drawCard() + drawCard();
                } while (AICardCount >= 22);



                // Printa ut info som spelaren behöver
                System.out.println("Some information:\n" + PURPLE + "Player card count: " + playerCardCount + "\nAI card count: " + AICardCount + "\n" + RESET);

                // Checka om spelaren har blackjack (21)
                if (playerCardCount == 21) {
                    System.out.println("Blackjack! Player wins!");
                    playerMoney += playerBetAmount * 1.5; // Player gets 1.5 times the bet for blackjack
                    continue;
                }

                // Fråga spelaren om den vill hit or stand
                while (playerCardCount < 22) {
                    System.out.println(CYAN + "Stand or hit? (s/h)" + RESET);
                    playerHitOrStandChoice = GameScanner.nextLine();

                    if (playerHitOrStandChoice.equals("s")) {
                        System.out.println("Player has chosen stand.");
                        System.out.println("Player card count is: " + playerCardCount);
                        break;
                    } else if (playerHitOrStandChoice.equals("h")) {
                        System.out.println("Player has chosen hit.");
                        int newCardAmount = drawCard();
                        playerCardCount += newCardAmount;
                        System.out.println("New player card count is: " + playerCardCount);
                    } else {
                        System.out.println("Invalid choice. Please enter 's' to stand or 'h' to hit.");
                    }
                }

                // Checka om spelaren busted
                if (playerCardCount > 21) {
                    System.out.println(RED + "PLAYER Busted" + RESET);
                    playerMoney -= playerBetAmount;
                } else {
                    // Låt AI:n spela och se vad för resultat den får.
                    int aiResult = AIPlays(AICardCount);

                    System.out.println(PURPLE + "AI CC: " + aiResult + "\nPLAYER CC: " + playerCardCount + RESET);

                    // Se vem som vinner
                    if (aiResult > 21) {
                        System.out.println(GREEN + "AI Busted (PLAYER WON)" + RESET);
                        playerMoney += playerBetAmount;
                    } else if (aiResult == playerCardCount) {
                        System.out.println(YELLOW + "It's a draw!" + RESET);
                    } else if (aiResult <= playerCardCount || playerCardCount > 21) {
                        System.out.println(GREEN + "PLAYER WON." + RESET);
                        playerMoney += playerBetAmount;
                    } else {
                        System.out.println(RED + "PLAYER LOST." + RESET);
                        playerMoney -= playerBetAmount;
                    }

                }

                System.out.println("Player's money: " + playerMoney);
            }
        }
        else if (firstChoice == 2) {
            System.out.println("Laddar inställningar...");
        }
        else if (firstChoice == 3) {
            System.out.println("Laddar in regler...");
            System.out.println("Målet i Blackjack är att få en bättre hand än dealern. " +
                    "\nFör att göra det, måste du ha en hand som är högre än dealerns, " +
                    "\nutan att handens totala värde överstiger 21. Du kan även vinna " +
                    "\ngenom att ha ett totalt värde under 22 när det totala värdet på " +
                    "\ndealerns hand överstiger 21.");
        }
        else {
            System.out.println("Du valde inget av alternativen...");
        }
    }

    public static int drawCard() {
        int cardDrawn;
        cardDrawn = ((int) (Math.random() * 13) + 1); // Generate values between 1 and 10
        return cardDrawn;
    }

    public static int AIPlays(int currentCard) {
        int AICardCount = currentCard;

        while (AICardCount < 17) {
            int newCard = drawCard();

            // Treat Ace (1 or 11) correctly based on the current AI card count
            if (newCard == 1) {
                if (AICardCount + 11 <= 21) {
                    newCard = 11; // Treat Ace as 11 if it won't cause a bust
                } else {
                    newCard = 1; // Treat Ace as 1 otherwise
                }
            }

            // Map values greater than 10 to 10 (face cards and 10s)
            if (newCard > 10) {
                newCard = 10;
            }

            AICardCount += newCard;

            // Handle Ace (value 11 or 1)
            if (AICardCount > 21 && newCard == 11) {
                AICardCount -= 10; // Change Ace value from 11 to 1
            }
        }

        return AICardCount;
    }
}