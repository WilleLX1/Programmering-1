import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
    private static final int PORT = 54321;

    public static void main(String[] args) throws IOException {
        // Start scanners
        Scanner MenuScanner = new Scanner(System.in);
        Scanner GameScanner = new Scanner(System.in);

        // Variables
        int MenuChoice;
        int BlackjackChoice;
        int RouletteChoice;

        // Pengar i hela spelet
        int fickPengar = ReadFickPengar();
        int lånadePengar = ReadLoanedPengar();
        //int nuvarandeRänta = ReadIntrestRent();
        //int kvarAttBetalaPåLån = ReadMoneyLeftOnRent();

        // Blackjack stats/settings
        int blackjackGamesPlayed = BlackjackReadGamesPlayed();
        int blackjackGamesWon = BlackjackReadGamesWon();
        int blackjackTotalWinLoss = BlackjackReadTotalWinLoss();
        String playerHitOrStandChoice;
        int BlackjackPlayerBetAmount;

        // Roulette stats/settings
        int rouletteGamesPlayed = RouletteReadGamesPlayed();
        int rouletteGamesWon = RouletteReadGamesWon();
        int rouletteTotalWinLoss = RouletteReadTotalWinLoss();

        // Poker stats/settings


        // Create a separate thread to host the stats file in the background
        Thread hostFileThread = new Thread(() -> {
            try {
                hostFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hostFileThread.start(); // Start the background thread for hosting the stats file

        // Main menu
        while (true){
            System.out.println("Välkommen till Ruby Casino!");
            System.out.println("Du börjar med 1000 SEK, klarar du vinna mer pengar än vad du startade med? Spel att välja mellan nedan:");
            System.out.println("1. Blackjack\n2. Roulette\n3. Poker (Texas hold'em)\n4. Inställningar");
            MenuChoice = MenuScanner.nextInt();

            // Check menu anwser (using if instead of switch idk why...)
            if (MenuChoice == 1){
                System.out.println(YELLOW + "\n\nStartar Blackjack...\n\n" + RESET);
                // Menu blackjack
                while (true){
                    System.out.println("Välkommen till Blackjack (21)!");
                    System.out.println("Alternativ:\n1. Spela\n2. Regler\n3. Reset stats\n4. Gå till Huvudmenyn");
                    BlackjackChoice = MenuScanner.nextInt();

                    if (BlackjackChoice == 1) {
                        while (fickPengar > 0) {
                            System.out.println("Startar spel...");

                            // Låt spelaren bestämma hur mycket att betta
                            while (true) {
                                System.out.println("How much do you want to bet? (You have " + fickPengar + ")");
                                BlackjackPlayerBetAmount = GameScanner.nextInt();
                                GameScanner.nextLine(); // Consume the newline character
                                if (BlackjackPlayerBetAmount > 0 && BlackjackPlayerBetAmount <= fickPengar) {
                                    System.out.println("Betting amount set to: " + BlackjackPlayerBetAmount);
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
                                fickPengar += BlackjackPlayerBetAmount * 1.5; // Player får 1.5 gånger så mycket som de betade för att de fick blackjack
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
                                blackjackGamesPlayed++;
                                fickPengar -= BlackjackPlayerBetAmount;
                                blackjackTotalWinLoss -= BlackjackPlayerBetAmount;
                                UpdateStatistics(fickPengar, "Blackjack", blackjackGamesPlayed, blackjackGamesWon, blackjackTotalWinLoss);
                            } else {
                                // Låt AI:n spela och se vad för resultat den får.
                                int aiResult = AIPlaysBlackjack(AICardCount);

                                System.out.println(PURPLE + "AI CC: " + aiResult + "\nPLAYER CC: " + playerCardCount + RESET);

                                // Se vem som vinner
                                if (aiResult > 21) {
                                    System.out.println(GREEN + "AI Busted (PLAYER WON)" + RESET);
                                    blackjackGamesPlayed++;
                                    blackjackGamesWon++;
                                    fickPengar += BlackjackPlayerBetAmount;
                                    blackjackTotalWinLoss += BlackjackPlayerBetAmount;
                                    UpdateStatistics(fickPengar, "Blackjack", blackjackGamesPlayed, blackjackGamesWon, blackjackTotalWinLoss);
                                } else if (aiResult == playerCardCount) {
                                    System.out.println(YELLOW + "It's a draw!" + RESET);
                                    blackjackGamesPlayed++;
                                } else if (aiResult <= playerCardCount || playerCardCount > 21) {
                                    System.out.println(GREEN + "PLAYER WON." + RESET);
                                    blackjackGamesPlayed++;
                                    blackjackGamesWon++;
                                    fickPengar += BlackjackPlayerBetAmount;
                                    blackjackTotalWinLoss += BlackjackPlayerBetAmount;
                                    UpdateStatistics(fickPengar, "Blackjack", blackjackGamesPlayed, blackjackGamesWon, blackjackTotalWinLoss);
                                } else {
                                    System.out.println(RED + "PLAYER LOST." + RESET);
                                    blackjackGamesPlayed++;
                                    fickPengar -= BlackjackPlayerBetAmount;
                                    blackjackTotalWinLoss -= BlackjackPlayerBetAmount;
                                    UpdateStatistics(fickPengar, "Blackjack", blackjackGamesPlayed, blackjackGamesWon, blackjackTotalWinLoss);
                                }

                            }

                            System.out.println("Player's money: " + fickPengar);
                        }
                        System.out.println("Du har inga pengar...");
                    }
                    else if (BlackjackChoice == 2) {
                        System.out.println("Laddar in regler...");
                        System.out.println("Målet i Blackjack är att få en bättre hand än dealern. " +
                                "\nFör att göra det, måste du ha en hand som är högre än dealerns, " +
                                "\nutan att handens totala värde överstiger 21. Du kan även vinna " +
                                "\ngenom att ha ett totalt värde under 22 när det totala värdet på " +
                                "\ndealerns hand överstiger 21.");
                        waitForAnyKeyPress();
                    }
                    else if (BlackjackChoice == 3) {
                        System.out.println("Reseting score...");
                        UpdateStatistics(1000, "Blackjack",0, 0, 0);
                        System.out.println("Successfully resetat dina stats!");
                    }
                    else if (BlackjackChoice == 4) {
                        System.out.println(YELLOW + "\n\nGår till huvudmenyn...\n\n" + RESET);
                        break;
                    }
                    else {
                        System.out.println("Du valde inget av alternativen...");
                    }
                }
            }
            else if (MenuChoice == 2) {
                System.out.println(YELLOW + "\n\nStartar Roulette...\n\n" + RESET);
                // Menu roulette
                while (true){
                    System.out.println("Välkommen till Roulette!");
                    System.out.println("Vänligen välj något av alternativen nedan:");
                    System.out.println("1. Spela\n2. Regler\n3. Reset stats\n4. Gå till Huvudmenyn");
                    RouletteChoice = GameScanner.nextInt();

                    if (RouletteChoice == 1){
                        Random random = new Random();
                        printWheel();

                        while (fickPengar > 0) {
                            System.out.println("Ditt nuvarande saldo: $" + fickPengar);

                            // Ask for the choice (number)
                            System.out.println("Välj ett nummer:");
                            System.out.println("1. Röd");
                            System.out.println("2. Svart");
                            System.out.println("3. Lika");
                            System.out.println("4. Udda");
                            System.out.println("5. Lämna");
                            System.out.print("Skriv ditt val: ");
                            int choice = GameScanner.nextInt();

                            if (choice == 5) {
                                System.out.println("Tack för att du spelade! Ditt nuvarande saldo: $" + fickPengar);
                                break;
                            }

                            int bet = 0;

                            // Validate the choice and set the bet amount accordingly
                            switch (choice) {
                                case 1: // Red
                                case 2: // Black
                                    System.out.print("Skriv hur mycket pengar du vill satsa (\"0\" för att lämna): $");
                                    bet = GameScanner.nextInt();
                                    break;
                                case 3: // Even
                                case 4: // Odd
                                    System.out.print("Skriv hur mycket pengar du vill satsa (\"0\" för att lämna): $");
                                    bet = GameScanner.nextInt();
                                    break;
                                default:
                                    System.out.println("Okänt val. Var snäll och välj en av dessa: 1, 2, 3, 4 eller 5.");
                                    continue;
                            }

                            if (bet == 0) {
                                System.out.println(YELLOW + "Lämnar..." + RESET);
                                break;
                            }

                            if (bet > fickPengar) {
                                System.out.println(RED + "ERROR (Du har inte tillräckligt med pengar...)" + RESET);
                                continue;
                            }

                            int spinResult = random.nextInt(37); // 0 to 36
                            String spinColor = getColorForNumber(spinResult);

                            System.out.print("Spinning the wheel...");
                            simulateSpinning(); // Call the spinning animation method

                            System.out.println(CYAN + "\nThe wheel stopped at " + spinResult + " (" + spinColor + ")" + RESET);

                            if (didPlayerWin(choice, spinResult)) {
                                System.out.println(GREEN + "You win $" + bet + RESET);
                                rouletteGamesPlayed++;
                                rouletteGamesWon++;
                                rouletteTotalWinLoss += bet;
                                fickPengar += bet;
                                UpdateStatistics(fickPengar, "Roulette", rouletteGamesPlayed, rouletteGamesWon, rouletteTotalWinLoss);
                            } else {
                                System.out.println(RED + "You lose $" + bet + RESET);
                                rouletteGamesPlayed++;
                                rouletteTotalWinLoss -= bet;
                                fickPengar -= bet;
                                UpdateStatistics(fickPengar, "Roulette", rouletteGamesPlayed, rouletteGamesWon, rouletteTotalWinLoss);
                            }
                        }

                        System.out.println(YELLOW + "\n\nGame Over!\n\n" + RESET);
                    }
                    else if (RouletteChoice == 2) {
                        System.out.println("Roulette är ett spännande casinospel där en boll släpps på ett numrerat hjul. Spelarna kan satsa på olika utfall, som att bollen hamnar på ett specifikt nummer, " +
                                "\nen viss färg (rött eller svart), om numret är jämnt eller udda, eller inom ett visst dussinområde. Efter att satsningarna är placerade, snurrar hjulet, och det nummer där bollen" +
                                "\nstannar blir det vinnande resultatet. Om din satsning träffar, vinner du en viss mängd pengar baserat på oddsen. Om inte, förlorar du din satsning.");
                        waitForAnyKeyPress();
                    }
                    else if (RouletteChoice == 3) {
                        System.out.println("Reseting score...");
                        UpdateStatistics(1000, "Roulette",0, 0, 0);
                        System.out.println("Successfully resetat dina stats!");
                    }
                    else if (RouletteChoice == 4) {
                        System.out.println(YELLOW + "\n\nGår till huvudmenyn...\n\n" + RESET);
                        break;
                    }
                    else {
                        System.out.println(RED + "\n\nDu valde inget av alternativen...\n\n" + RESET);
                    }
                }
            }
            else if (MenuChoice == 3) {
                while (true){
                    // Variables
                    int pokerPot = 0;
                    int player1Bet;
                    int player2Bet;
                    int player3Bet;
                    int player4Bet;

                    // Menu Choices
                    int pokerChoice;

                    // Declare an ArrayList to store IP addresses with open ports
                    ArrayList<String> openIPs = new ArrayList<>();

                    while (true){
                        System.out.println("Välkommen till Poker (Texas hold'em)!");
                        System.out.println("1. Starta Server\n2. Gå med i server");
                        pokerChoice = GameScanner.nextInt();

                        if (pokerChoice == 1){
                            System.out.println("Startar server...");
                            startServer();
                            System.out.println("Servern är startad!");
                            System.out.println("Väntar på spelare...");

                            // Listen for messages and when found a msg check if it is "NEW-PLAYER" and if yes print "NEW Player! <IP>".
                            // Create a ServerSocket to listen for incoming connections
                            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                                while (true) {
                                    // Accept incoming connections
                                    Socket clientSocket = serverSocket.accept();

                                    // Handle the connection in a separate thread or method
                                    handleClientConnection(clientSocket);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        else if (pokerChoice == 2) {
                            System.out.println("Letar efter öppna servar...");
                            String baseIP = findLocalBaseIP();

                            if (baseIP != null) {
                                System.out.println("Lokalt Nätverk Bas IP: " + baseIP + "X");

                                int startIPRange = 1;
                                int endIPRange = 255;

                                for (int i = startIPRange; i <= endIPRange; i++) {
                                    String ipAddress = baseIP + i;
                                    if (isPortOpen(ipAddress, PORT)) {
                                        System.out.println("Port " + PORT + " is open at IP address: " + ipAddress);
                                        // Add the IP address to the ArrayList
                                        openIPs.add(ipAddress);
                                    }
                                }

                                // Print the IP addresses with open ports
                                System.out.println("IP addresses with open ports: " + openIPs);

                                // Send the "NEW-PLAYER" message to each open IP
                                String messageToSend = "NEW-PLAYER";

                                for (String targetIP : openIPs) {
                                    try {
                                        Socket socket = new Socket(targetIP, PORT);
                                        OutputStream outputStream = socket.getOutputStream();
                                        outputStream.write(messageToSend.getBytes());
                                        outputStream.flush();
                                        socket.close();
                                        System.out.println("Sent 'NEW-PLAYER' message to IP: " + targetIP);
                                    } catch (IOException e) {
                                        System.err.println("Failed to send 'NEW-PLAYER' message to IP: " + targetIP);
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                System.out.println("Unable to determine the local network's base IP.");
                            }
                        }
                    }


                }

            }
            else if (MenuChoice == 4) {
                int VilligAttLåna;
                String LoanVal;
                System.out.println(YELLOW + "\n\nLaddar inställningar...\n\n" + RESET);
                System.out.println("Hur mycket pengar vill du lånna:");
                VilligAttLåna = MenuScanner.nextInt();

                // Are you sure!??
                System.out.println(RED + "Är du säker på detta lån? (j/n)" + RESET);
                LoanVal = MenuScanner.nextLine();

                if (LoanVal == "j") {
                    // Titta så att användaren får låna antalet pengar.
                    //if
                    // Om ja skicka pengarna

                    // Updatera pengar
                    UpdateStatisticsMoneyOnly(fickPengar);
                    System.out.println(GREEN + "Godkänt! Satte pengarna till " + fickPengar + " SEK." + RESET);

                } else if (LoanVal == "n") {
                    System.out.println("Nekat! Användaren avbröt lånet.");
                }

            }
            else {
                System.out.println(RED + "\n\nDu valde inget av alternativen...\n\n" + RESET);
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

    public static String findLocalBaseIP() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        String hostAddress = address.getHostAddress();
                        int lastDotIndex = hostAddress.lastIndexOf('.');
                        if (lastDotIndex != -1) {
                            return hostAddress.substring(0, lastDotIndex + 1);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isPortOpen(String ipAddress, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port), 1000); // Timeout set to 1 second
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    // Intrest Rent:
    public static int calculateTotalAmountToRepay(int loanedMoney, double interestRate) {


        // Ensure that the interest rate is a valid value (not negative).
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative.");
        }

        // Calculate the total amount to be repaid.
        double totalAmount = loanedMoney * (1 + (interestRate / 100));

        // Convert the total amount to an integer if you want an integer result.
        int totalAmountAsInt = (int) totalAmount;

        return totalAmountAsInt;
    }





    public static int drawCard() {
        int cardDrawn;
        cardDrawn = ((int) (Math.random() * 13) + 1); // Generate values between 1 and 13
        return cardDrawn;
    }


    // Blackjack table
    public static void printWheel() {
        System.out.println("  ________________");
        System.out.println(" /  ______/\\______ \\");
        System.out.println("| /  ____/  \\______\\|");
        System.out.println("| | /    /  /     | |");
        System.out.println("| | |   |  |  0  | |");
        System.out.println("| | |___|  |______| |");
        System.out.println("| |  ______  ______ |");
        System.out.println("| | |    0 || 0    ||");
        System.out.println("| | |______||______||");
        System.out.println("| |  ______  ______ |");
        System.out.println("| | |  0   || 0    ||");
        System.out.println("| | |______||______||");
        System.out.println("| |  ______  ______ |");
        System.out.println("| | |   0  ||  0   ||");
        System.out.println("| | |______||______||");
        System.out.println("| |  ______  ______ |");
        System.out.println("| | |    0 ||  0   ||");
        System.out.println("| | |______||______||");
        System.out.println("| |  ______  ______ |");
        System.out.println("| | |   0  ||  0   ||");
        System.out.println("| | |______||______||");
        System.out.println("| \\|_______________|/");
        System.out.println(" \\_________________/ ");
    }

    // Simulate spinning animation
    private static void simulateSpinning() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.print(" " + (int)(Math.random() * 37) + " ");
                Thread.sleep(500); // Add a delay for animation
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Determine the color for a given number
    private static String getColorForNumber(int number) {
        if (number == 0) {
            return "white";
        } else if ((number >= 1 && number <= 10) || (number >= 19 && number <= 28)) {
            return (number % 2 == 0) ? "black" : "red";
        } else {
            return (number % 2 == 0) ? "red" : "black";
        }
    }
    // Check if the player won
    private static boolean didPlayerWin(int choice, int spinResult) {
        if (choice == 1) { // Red
            return getColorForNumber(spinResult).equalsIgnoreCase("red");
        } else if (choice == 2) { // Black
            return getColorForNumber(spinResult).equalsIgnoreCase("black");
        } else if (choice == 3) { // Even
            return spinResult % 2 == 0;
        } else { // Odd
            return spinResult % 2 != 0;
        }
    }


    public static int AIPlaysBlackjack(int currentCard) {
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



    // Poker Stuff
    private static void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            List<Socket> clients = new ArrayList<>();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("New client connected from " + clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Define a method to handle client connections
    private static void handleClientConnection(Socket clientSocket) {
        try {
            System.out.println("New player connected from IP: " + clientSocket.getInetAddress().getHostAddress());

            // Read messages from the client
            InputStream inputStream = clientSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead).trim(); // Trim to remove extra whitespace
                System.out.println("Received message from client: " + message);

                // Check if the received message is "NEW-PLAYER"
                if (message.equals("NEW-PLAYER")) {
                    // Handle the "NEW-PLAYER" message as needed
                    // For example, you can add the player to the game.
                    System.out.println("Got a new player!");
                }
            }

            // Close the client socket when done
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // Roulette Read stuff
    // Read the number of games played from the file
    public static int BlackjackReadGamesPlayed() {
        int gamesPlayed = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games played (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Blackjack Spelade spel: ")) {
                    gamesPlayed = Integer.parseInt(line.substring("Blackjack Spelade spel: ".length()));
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
    public static int BlackjackReadGamesWon() {
        int gamesWon = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games won (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Blackjack Vunna spel: ")) {
                    gamesWon = Integer.parseInt(line.substring("Blackjack Vunna spel: ".length()));
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

    public static int BlackjackReadTotalWinLoss() {
        int totalWinLoss = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games won (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Blackjack Totalt vunnit eller förlorat: ")) {
                    totalWinLoss = Integer.parseInt(line.substring("Blackjack Totalt vunnit eller förlorat: ".length()));
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



    // Roulette Read stuff
    // Read the number of games played from the file
    public static int RouletteReadGamesPlayed() {
        int gamesPlayed = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games played (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Roulette Spelade spel: ")) {
                    gamesPlayed = Integer.parseInt(line.substring("Roulette Spelade spel: ".length()));
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
    public static int RouletteReadGamesWon() {
        int gamesWon = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games won (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Roulette Vunna spel: ")) {
                    gamesWon = Integer.parseInt(line.substring("Roulette Vunna spel: ".length()));
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

    public static int RouletteReadTotalWinLoss() {
        int totalWinLoss = 0;

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the relevant line for games won (you may adjust this line as needed)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Roulette Totalt vunnit eller förlorat: ")) {
                    totalWinLoss = Integer.parseInt(line.substring("Roulette Totalt vunnit eller förlorat: ".length()));
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

                bufferedWriter.write("Pengar: 1000"); // Initialize with zero pengar
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
    public static int ReadLoanedPengar(){
        int currentLoanedPengar = 0;

        try {
            // Läser fickPengarna från filen.
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();
            if (line != null) {
                // Extract the integer value from the line
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    currentLoanedPengar = Integer.parseInt(parts[1].trim());
                }
            }

            // Stänger filen
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // Om filen inte finns, skapa den och lägg "fickPengar" i den.
            try {
                FileWriter fileWriter = new FileWriter(filePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write("Loaned Pengar: 0"); // Initialize with zero pengar
                bufferedWriter.newLine(); // Add a newline

                // Stänger filen
                bufferedWriter.close();

                // Skapade filen!
            } catch (IOException ex) {
                System.err.println("ERROR (Kunde inte skapa Lånade Pengar filen): " + ex.getMessage());
            }
        } catch (IOException e) {
            System.err.println("ERROR (kunde inte läsa Lånade Pengar filen): " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("ERROR (parsing integer): " + e.getMessage());
        }

        return currentLoanedPengar;
    }

    public static void UpdateStatisticsMoneyOnly(int pengarToUpdate){
        try {
            // Open the file for reading
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            // Read the existing contents line by line
            StringBuilder fileContents = new StringBuilder();
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    // Modify the first line
                    line = ("Pengar: " + pengarToUpdate);
                    firstLine = false;
                }
                fileContents.append(line).append(System.lineSeparator());
            }

            // Close the reader
            reader.close();

            // Open the file for writing (this will overwrite the original file)
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            // Write the modified contents back to the file
            writer.write(fileContents.toString());

            // Close the writer
            writer.close();

            System.out.println("First line modified successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void UpdateStatistics(int pengarToUpdate, String game, int gamesPlayed, int gamesWon, int totalWinLoss) {
        try {
            String username = System.getProperty("user.name");

            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            if (game == "Blackjack"){
                // Write the updated statistics to the text file
                bufferedWriter.write("Pengar: " + pengarToUpdate);
                bufferedWriter.newLine();
                bufferedWriter.write("Blackjack Spelade spel: " + gamesPlayed);
                bufferedWriter.newLine();
                bufferedWriter.write("Blackjack Vunna spel: " + gamesWon);
                bufferedWriter.newLine();
                bufferedWriter.write("Blackjack Totalt vunnit eller förlorat: " + totalWinLoss);
                bufferedWriter.newLine();
                bufferedWriter.write("Användare: " + username);
                bufferedWriter.newLine();
                bufferedWriter.write("Roulette Spelade spel: " + RouletteReadGamesPlayed());
                bufferedWriter.newLine();
                bufferedWriter.write("Roulette Vunna spel: " + RouletteReadGamesWon());
                bufferedWriter.newLine();
                bufferedWriter.write("Roulette Totalt vunnit eller förlorat: " + RouletteReadTotalWinLoss());
            } else if (game == "Roulette") {
                // Write the updated statistics to the text file
                bufferedWriter.write("Pengar: " + pengarToUpdate);
                bufferedWriter.newLine();
                bufferedWriter.write("Blackjack Spelade spel: " + BlackjackReadGamesPlayed());
                bufferedWriter.newLine();
                bufferedWriter.write("Blackjack Vunna spel: " + BlackjackReadGamesWon());
                bufferedWriter.newLine();
                bufferedWriter.write("Blackjack Totalt vunnit eller förlorat: " + BlackjackReadTotalWinLoss());
                bufferedWriter.newLine();
                bufferedWriter.write("Användare: " + username);
                bufferedWriter.newLine();
                bufferedWriter.write("Roulette Spelade spel: " + gamesPlayed);
                bufferedWriter.newLine();
                bufferedWriter.write("Roulette Vunna spel: " + gamesWon);
                bufferedWriter.newLine();
                bufferedWriter.write("Roulette Totalt vunnit eller förlorat: " + totalWinLoss);
            }


            // Close the file
            bufferedWriter.close();

            System.out.println("Updated statistics...");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}