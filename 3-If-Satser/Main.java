import java.util.Scanner;
import java.io.*;
import java.util.Random;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static final String filePath = "C:\\users\\public\\FickPengar.txt";

    public static void main(String[] args) throws IOException {
        // Start scanners
        Scanner MenuScanner = new Scanner(System.in);
        Scanner GameScanner = new Scanner(System.in);

        // Variables
        int firstChoice;
        int fickPengar = ReadFickPengar();
        int gamesPlayed = ReadGamesPlayed();
        int gamesWon = ReadGamesWon();
        int totalWinLoss = ReadTotalWinLoss();
        String playerHitOrStandChoice;
        int playerBetAmount;

        // Create a separate thread to host the file in the background
        Thread hostFileThread = new Thread(() -> {
            try {
                hostFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hostFileThread.start(); // Start the background thread for hosting the file


        // Menu
        while (true){
            System.out.println("Välkommen till Blackjack (21)!");
            System.out.println("Alternativ:\n1. Spela\n2. Inställningar\n3. Regler\n4. Reset stats");
            firstChoice = MenuScanner.nextInt();

            if (firstChoice == 1) {
                while (fickPengar > 0) {
                    System.out.println("Startar spel...");

                    // Låt spelaren bestämma hur mycket att betta
                    while (true) {
                        System.out.println("How much do you want to bet? (You have " + fickPengar + ")");
                        playerBetAmount = GameScanner.nextInt();
                        GameScanner.nextLine(); // Consume the newline character
                        if (playerBetAmount > 0 && playerBetAmount <= fickPengar) {
                            System.out.println("Betting amount set to: " + playerBetAmount);
                            break;
                        } else {
                            System.out.println("Invalid bet amount. Please enter a valid amount.");
                        }
                    }

                    // Ge både spelaren och dealern (AI) 2 kort var.
                    int playerCardCount = 0;
                    do {
                        int kort1 = drawCard();
                        int kort2 = drawCard();

                        if (kort1 == 1) {
                            if (playerCardCount + 11 <= 21) {
                                kort1 = 11; // Treat Ace as 11 if it won't cause a bust
                            } else {
                                kort1 = 1; // Treat Ace as 1 otherwise
                            }
                        }
                        if (kort2 == 2) {
                            if (playerCardCount + 11 <= 21) {
                                kort2 = 11; // Treat Ace as 11 if it won't cause a bust
                            } else {
                                kort2 = 1; // Treat Ace as 1 otherwise
                            }
                        }

                        if (kort1 >= 10){
                            kort1 = 10;
                        }
                        if (kort2 >= 10){
                            kort2 = 10;
                        }
                        playerCardCount = kort1 + kort2;
                    } while (playerCardCount >= 22);

                    int AICardCount;
                    do {
                        AICardCount = drawCard() + drawCard();
                    } while (AICardCount >= 22);



                    // Printa ut info som spelaren behöver
                    System.out.println("Some information:\n" + PURPLE + "Player card count: " + playerCardCount + "\nAI card count: " + AICardCount + "\n" + RESET);

                    // Checka om spelaren har blackjack (21)
                    if (playerCardCount == 21) {
                        System.out.println(GREEN + "Player Got Blackjack!" + RESET);
                        fickPengar += playerBetAmount * 1.5; // Player får 1.5 gånger så mycket som de betade för att de fick blackjack
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
                            if (newCardAmount == 2) {
                                if (playerCardCount + 11 <= 21) {
                                    newCardAmount = 11; // Treat Ace as 11 if it won't cause a bust
                                } else {
                                    newCardAmount = 1; // Treat Ace as 1 otherwise
                                }
                            }

                            if (newCardAmount >= 10){
                                newCardAmount = 10;
                            }

                            playerCardCount += newCardAmount;
                            System.out.println("New player card count is: " + playerCardCount);
                        } else {
                            System.out.println("Invalid choice. Please enter 's' to stand or 'h' to hit.");
                        }
                    }

                    // Checka om spelaren busted
                    if (playerCardCount > 21) {
                        System.out.println(RED + "PLAYER Busted" + RESET);
                        gamesPlayed++;
                        fickPengar -= playerBetAmount;
                        totalWinLoss -= playerBetAmount;
                        UpdateStatistics(fickPengar, gamesPlayed, gamesWon, totalWinLoss);
                    } else {
                        // Låt AI:n spela och se vad för resultat den får.
                        int aiResult = AIPlays(AICardCount);

                        System.out.println(PURPLE + "AI CC: " + aiResult + "\nPLAYER CC: " + playerCardCount + RESET);

                        // Se vem som vinner
                        if (aiResult > 21) {
                            System.out.println(GREEN + "AI Busted (PLAYER WON)" + RESET);
                            gamesPlayed++;
                            gamesWon++;
                            fickPengar += playerBetAmount;
                            totalWinLoss += playerBetAmount;
                            UpdateStatistics(fickPengar, gamesPlayed, gamesWon, totalWinLoss);
                        } else if (aiResult == playerCardCount) {
                            System.out.println(YELLOW + "It's a draw!" + RESET);
                            gamesPlayed++;
                        } else if (aiResult <= playerCardCount || playerCardCount > 21) {
                            System.out.println(GREEN + "PLAYER WON." + RESET);
                            gamesPlayed++;
                            gamesWon++;
                            fickPengar += playerBetAmount;
                            totalWinLoss += playerBetAmount;
                            UpdateStatistics(fickPengar, gamesPlayed, gamesWon, totalWinLoss);
                        } else {
                            System.out.println(RED + "PLAYER LOST." + RESET);
                            gamesPlayed++;
                            fickPengar -= playerBetAmount;
                            totalWinLoss -= playerBetAmount;
                            UpdateStatistics(fickPengar, gamesPlayed, gamesWon, totalWinLoss);
                        }

                    }

                    System.out.println("Player's money: " + fickPengar);
                }
                System.out.println("Du har inga pengar...");
            }
            else if (firstChoice == 2) {
                System.out.println(YELLOW + "\n\nLaddar inställningar...\n\n" + RESET);
                System.out.println("Hur mycket pengar vill du ha:");
                fickPengar = MenuScanner.nextInt();
                UpdateStatistics(fickPengar, gamesPlayed, gamesWon, totalWinLoss);
                System.out.println("Godkänt! Satte pengarna till " + fickPengar + " SEK.");
            }
            else if (firstChoice == 3) {
                System.out.println("Laddar in regler...");
                System.out.println("Målet i Blackjack är att få en bättre hand än dealern. " +
                        "\nFör att göra det, måste du ha en hand som är högre än dealerns, " +
                        "\nutan att handens totala värde överstiger 21. Du kan även vinna " +
                        "\ngenom att ha ett totalt värde under 22 när det totala värdet på " +
                        "\ndealerns hand överstiger 21.");
                waitForAnyKeyPress();
            }
            else if (firstChoice == 4) {
                System.out.println("Reseting score...");
                UpdateStatistics(1000, 0, 0, 0);
                System.out.println("Successfully resetat dina stats!");
            } else {
                System.out.println("Du valde inget av alternativen...");
            }
        }
    }

    public static void hostFile(String filePath) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345); // Choose a port number
        InetAddress localAddress = InetAddress.getLocalHost();
        String localIP = localAddress.getHostAddress();

        while (true) {
            Socket clientSocket = serverSocket.accept();

            InputStream inputStream = new FileInputStream(filePath);
            OutputStream outputStream = clientSocket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            clientSocket.close();
            inputStream.close();
        }
    }

    public static int drawCard() {
        int cardDrawn;
        cardDrawn = ((int) (Math.random() * 13) + 1); // Generate values between 1 and 13
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

    // Read the number of games played from the file
    public static int ReadGamesPlayed() {
        int gamesPlayed = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games played (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Spelade spel: ")) {
                    gamesPlayed = Integer.parseInt(line.substring("Spelade spel: ".length()));
                    break;
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // Handle the case where the file doesn't exist (create it if needed)
            // ...

        } catch (IOException | NumberFormatException e) {
            // Handle other exceptions
            // ...
        }

        return gamesPlayed;
    }

    // Read the number of games won from the file
    public static int ReadGamesWon() {
        int gamesWon = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games won (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Vunna spel: ")) {
                    gamesWon = Integer.parseInt(line.substring("Vunna spel: ".length()));
                    break;
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // Handle the case where the file doesn't exist (create it if needed)
            // ...

        } catch (IOException | NumberFormatException e) {
            // Handle other exceptions
            // ...
        }

        return gamesWon;
    }

    public static int ReadTotalWinLoss() {
        int totalWinLoss = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games won (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Totalt vunnit eller förlorat: ")) {
                    totalWinLoss = Integer.parseInt(line.substring("Totalt vunnit eller förlorat: ".length()));
                    break;
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // Handle the case where the file doesn't exist (create it if needed)
            // ...

        } catch (IOException | NumberFormatException e) {
            // Handle other exceptions
            // ...
        }

        return totalWinLoss;
    }
    public static int ReadFickPengar() {
        int currentPengar = 0;

        try {
            // Läser fickPengarna från filen.
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            if (line != null) {
                // Extract the integer value from the line
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    currentPengar = Integer.parseInt(parts[1].trim());
                }
            }

            // Stänger filen
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // Om filen inte finns, skapa den och lägg "fickPengar" i den.
            try {
                FileWriter fileWriter = new FileWriter(filePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write("Pengar: 0"); // Initialize with zero pengar
                bufferedWriter.newLine(); // Add a newline

                // Stänger filen
                bufferedWriter.close();

                // Skapade filen!
            } catch (IOException ex) {
                System.err.println("ERROR (Kunde inte skapa fickPengar filen): " + ex.getMessage());
            }
        } catch (IOException e) {
            System.err.println("ERROR (kunde inte läsa fickPengar filen): " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("ERROR (parsing integer): " + e.getMessage());
        }

        return currentPengar;
    }
    public static void UpdateStatistics(int pengarToUpdate, int gamesPlayed, int gamesWon, int totalWinLoss) {
        try {
            String username = System.getProperty("user.name");

            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the updated statistics to the text file
            bufferedWriter.write("Pengar: " + pengarToUpdate);
            bufferedWriter.newLine();
            bufferedWriter.write("Spelade spel: " + gamesPlayed);
            bufferedWriter.newLine();
            bufferedWriter.write("Vunna spel: " + gamesWon);
            bufferedWriter.newLine();
            bufferedWriter.write("Totalt vunnit eller förlorat: " + totalWinLoss);
            bufferedWriter.newLine();
            bufferedWriter.write("Användare: " + username);

            // Close the file
            bufferedWriter.close();

            System.out.println("Updated statistics...");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}