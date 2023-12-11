import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

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


    public static void main(String[] args) throws IOException {
        Scanner MenuScanner = new Scanner(System.in);

        // Variabler
        int firstChoice;
        boolean serverIsRunning = false;

        // Search for files the entire time...
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                hiddenDiscoverAndDownloadFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 25, TimeUnit.SECONDS);



        while (true) {
            // Display menu options
            showMenu();

            // User choice
            System.out.print("Skriv ditt val: ");
            firstChoice = MenuScanner.nextInt();
            switch (firstChoice) {
                case 1:
                    if (!serverIsRunning){
                        System.out.println(YELLOW + "Startar webbserver..." + RESET);
                        startWebServer();
                        serverIsRunning = true;
                        break;
                    }
                    else {
                        System.out.println(RED + "ERROR (Webbservern är redan igång...)" + RESET);
                        break;
                    }
                case 2:
                    System.out.println(YELLOW + "Uppdaterar statestik..." + RESET);
                    discoverAndDownloadFiles();
                    waitForAnyKeyPress(); // Wait for key press after download
                    break;
                case 3:
                    System.out.println(YELLOW + "Avslutar..." + RESET);
                    return;
                default:
                    System.out.println(RED + "Ogiltigt val..." + RESET);
            }
            // Wait for a key press before returning to the menu
            waitForAnyKeyPress();
        }
    }
    public static void discoverAndDownloadFiles() throws IOException {
        System.out.println("Started search for files on the local network!");
        InetAddress localAddress = InetAddress.getLocalHost();
        String localIP = localAddress.getHostAddress();
        String localPrefix = localIP.substring(0, localIP.lastIndexOf('.') + 1); // Extract the not-cut part

        int port = 12345; // Port number should match the one used for hosting

        ExecutorService executor = Executors.newFixedThreadPool(20); // Adjust the thread pool size as needed

        // Scan local network for machines hosting the file using multiple threads
        for (int i = 1; i <= 254; i++) {
            final String targetIP = localPrefix + i; // Replace with your network's IP scheme

            if (targetIP.equals(localIP)) {
                executor.execute(() -> {
                    try {
                        System.out.println("Trying to connect to: " + targetIP);
                        Socket socket = new Socket(targetIP, port);
                        System.out.println("Connected to: " + targetIP);

                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = new FileOutputStream("C:\\users\\public\\fickPengar_" + targetIP + ".txt");

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        System.out.println("File downloaded from: " + targetIP);
                        outputStream.close();
                        socket.close();
                        inputStream.close();
                    } catch (IOException e) {
                        System.err.println("Failed to connect to: " + targetIP);
                        // Handle connection failures or machines not hosting the file
                    }
                });
            }
        }

        // Shutdown the thread pool after all tasks are submitted
        executor.shutdown();
    }
    public static void hiddenDiscoverAndDownloadFiles() throws IOException {
        InetAddress localAddress = InetAddress.getLocalHost();
        String localIP = localAddress.getHostAddress();
        String localPrefix = localIP.substring(0, localIP.lastIndexOf('.') + 1); // Extract the not-cut part

        int port = 12345; // Port number should match the one used for hosting

        ExecutorService executor = Executors.newFixedThreadPool(20); // Adjust the thread pool size as needed

        // Scan local network for machines hosting the file using multiple threads
        for (int i = 1; i <= 254; i++) {
            final String targetIP = localPrefix + i; // Replace with your network's IP scheme

            if (!targetIP.equals(localIP)) {
                executor.execute(() -> {
                    try {
                        Socket socket = new Socket(targetIP, port);

                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = new FileOutputStream("C:\\users\\public\\fickPengar_" + targetIP + ".txt");

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.close();
                        socket.close();
                        inputStream.close();
                    } catch (IOException e) {
                        // Handle connection failures or machines not hosting the file
                    }
                });
            }
        }

        // Shutdown the thread pool after all tasks are submitted
        executor.shutdown();
    }

    private static void showMenu() {
        System.out.println(CYAN + "Hej!\nDetta är en kontroll panel för blackjack " +
                "spelen på ditt nätverk. Vänligen välj någon av alternativen nedan:\n" +
                BLUE + "1. Starta webbserver (Startar en sida med Apache)\n" +
                "2. Uppdatera statistik (Försöker hämta data från alla under lokalt nätverk)\n" +
                "3. Avsluta (Stänger webbserver och avslutar programmet)\n" + RESET);
    }

    private static void waitForAnyKeyPress() {
        System.out.println(YELLOW + "Tryck på Enter för att fortsätta..." + RESET);
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void startWebServer() throws IOException {
        // Get localIP to print later...
        InetAddress localAddress = InetAddress.getLocalHost();
        String localIP = localAddress.getHostAddress();

        int port = 8080; // Port number for the web server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create a context for the root path ("/") and set a handler for it
        server.createContext("/", new RootHandler());

        // Start the server
        server.start();

        System.out.println("Web server is running on port " + port + ". You can access it at: http:\\\\" + localIP + ":8080");
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Generate dynamic HTML content
            String dynamicContent = generateDynamicHTML();

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, dynamicContent.length());

            // Get the output stream and write the dynamic HTML response
            OutputStream os = exchange.getResponseBody();
            os.write(dynamicContent.getBytes());
            os.close();
        }



        private String generateDynamicHTML() {
            // Create a dynamic HTML content as a string
            StringBuilder dynamicContent = new StringBuilder();
            dynamicContent.append("<html><head>");
            dynamicContent.append("<style>");
            dynamicContent.append("ul.tabmenu {");
            dynamicContent.append("list-style-type: none;");
            dynamicContent.append("margin: 0;");
            dynamicContent.append("padding: 0;");
            dynamicContent.append("overflow: hidden;");
            dynamicContent.append("background-color: #333;");
            dynamicContent.append("}");

            dynamicContent.append("li.tabitem {");
            dynamicContent.append("float: left;");
            dynamicContent.append("}");

            dynamicContent.append("li.tabitem a {");
            dynamicContent.append("display: block;");
            dynamicContent.append("color: white;");
            dynamicContent.append("text-align: center;");
            dynamicContent.append("padding: 14px 16px;");
            dynamicContent.append("text-decoration: none;");
            dynamicContent.append("}");

            dynamicContent.append("li.tabitem a:hover {");
            dynamicContent.append("background-color: #ddd;");
            dynamicContent.append("color: black;");
            dynamicContent.append("}");

            dynamicContent.append(".tabcontent {");
            dynamicContent.append("display: none;");
            dynamicContent.append("padding: 20px;");
            dynamicContent.append("border: 1px solid #ccc;");
            dynamicContent.append("}");

            dynamicContent.append("table {");
            dynamicContent.append("border-collapse: collapse;");
            dynamicContent.append("width: 100%;");
            dynamicContent.append("}");

            dynamicContent.append("th, td {");
            dynamicContent.append("border: 1px solid #ddd;");
            dynamicContent.append("padding: 8px;");
            dynamicContent.append("text-align: left;");
            dynamicContent.append("}");

            dynamicContent.append("tr:nth-child(even) {");
            dynamicContent.append("background-color: #f2f2f2;");
            dynamicContent.append("}");

            dynamicContent.append("th {");
            dynamicContent.append("background-color: #333;");
            dynamicContent.append("color: white;");
            dynamicContent.append("}");

            dynamicContent.append("</style>");
            dynamicContent.append("</head><body>");
            dynamicContent.append("<h1>Game Statistics</h1>");

            // Create the tab menu
            dynamicContent.append("<ul class='tabmenu'>");
            dynamicContent.append("<li class='tabitem'><a href='javascript:void(0)' onclick='openTab(event, \"blackjack\")'>Blackjack Stats</a></li>");
            dynamicContent.append("<li class='tabitem'><a href='javascript:void(0)' onclick='openTab(event, \"roulette\")'>Roulette Stats</a></li>");
            dynamicContent.append("<li class='tabitem'><a href='javascript:void(0)' onclick='openTab(event, \"combined\")'>All Combined</a></li>");
            dynamicContent.append("</ul>");

            // Create the tab content for Blackjack Stats
            dynamicContent.append("<div id='blackjack' class='tabcontent'>");
            dynamicContent.append("<h2>Blackjack Statistics</h2>");
            dynamicContent.append("<table>");
            dynamicContent.append("<tr><th>IP Address</th><th>Money</th><th>Games Played</th><th>Games Won</th><th>Total Win/Loss</th><th>Win/Loss Percentage</th><th>User</th></tr>");

            // Finds file that holds stats
            File folder = new File("C:\\users\\public\\");
            File[] files = folder.listFiles();

            // Populate Blackjack Stats
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("fickPengar_")) {
                        String ipAddress = file.getName().replace("fickPengar_", "").replace(".txt", "");
                        String user = readUserFromFile(file);
                        Statistics stats = blackjackReadStatisticsFromFile(file);
                        String winLossPercentage = String.format("%.2f", calculateWinLossPercentage(stats));

                        dynamicContent.append("<tr><td>").append(ipAddress).append("</td><td>").append(stats.money).append("</td><td>")
                                .append(stats.gamesPlayed).append("</td><td>").append(stats.gamesWon).append("</td><td>")
                                .append(stats.totalWinLoss).append("</td><td>").append(winLossPercentage).append("</td><td>").append(user).append("</td></tr>");
                    }
                }
            }

            dynamicContent.append("</table>");
            dynamicContent.append("</div>");

            // Create the tab content for Roulette Stats
            dynamicContent.append("<div id='roulette' class='tabcontent'>");
            dynamicContent.append("<h2>Roulette Statistics</h2>");
            dynamicContent.append("<table>");
            dynamicContent.append("<tr><th>IP Address</th><th>Money</th><th>Games Played</th><th>Games Won</th><th>Total Win/Loss</th><th>Win/Loss Percentage</th><th>User</th></tr>");

            // Populate Roulette Stats
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("fickPengar_")) {
                        String ipAddress = file.getName().replace("fickPengar_", "").replace(".txt", "");
                        String user = readUserFromFile(file);
                        Statistics stats = rouletteReadStatisticsFromFile(file);
                        String winLossPercentage = String.format("%.2f", calculateWinLossPercentage(stats));

                        dynamicContent.append("<tr><td>").append(ipAddress).append("</td><td>").append(stats.money).append("</td><td>")
                                .append(stats.gamesPlayed).append("</td><td>").append(stats.gamesWon).append("</td><td>")
                                .append(stats.totalWinLoss).append("</td><td>").append(winLossPercentage).append("</td><td>").append(user).append("</td></tr>");
                    }
                }
            }

            // End Roulette Table
            dynamicContent.append("</table>");
            dynamicContent.append("</div>");

            // Create the tab content for All Combined Stats
            dynamicContent.append("<div id='combined' class='tabcontent'>");
            dynamicContent.append("<h2>All Combined Statistics</h2>");
            dynamicContent.append("<table>");
            dynamicContent.append("<tr><th>IP Address</th><th>Money</th><th>Games Played</th><th>Games Won</th><th>Total Win/Loss</th><th>Win/Loss Percentage</th><th>User</th></tr>");

            dynamicContent.append("</table>");
            dynamicContent.append("</div>");

            // Populate All Combined Stats table


            // JavaScript to handle tab switching
            dynamicContent.append("<script>");
            dynamicContent.append("function openTab(evt, tabName) {");
            dynamicContent.append("var i, tabcontent, tablinks;");
            dynamicContent.append("tabcontent = document.getElementsByClassName('tabcontent');");
            dynamicContent.append("for (i = 0; i < tabcontent.length; i++) {");
            dynamicContent.append("tabcontent[i].style.display = 'none';");
            dynamicContent.append("}");
            dynamicContent.append("tablinks = document.getElementsByClassName('tabitem');");
            dynamicContent.append("for (i = 0; i < tablinks.length; i++) {");
            dynamicContent.append("tablinks[i].className = tablinks[i].className.replace(' active', '');");
            dynamicContent.append("}");
            dynamicContent.append("document.getElementById(tabName).style.display = 'block';");
            dynamicContent.append("evt.currentTarget.className += ' active';");
            dynamicContent.append("}");
            dynamicContent.append("</script>");

            dynamicContent.append("</body></html>");

            return dynamicContent.toString();
        }


        private String readUserFromFile(File file) {
            String user = "";
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Användare:")) {
                        user = line.split(":")[1].trim();
                        break; // Exit the loop once the user is found
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return user;
        }

        private double calculateWinLossPercentage(Statistics stats) {
            if (stats.gamesPlayed == 0) {
                return 0.0; // Avoid division by zero
            }
            return (double) stats.gamesWon / stats.gamesPlayed * 100.0;
        }
        private Statistics blackjackReadStatisticsFromFile(File file) {
            Statistics stats = new Statistics();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Pengar:")) {
                        stats.money = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Blackjack Spelade spel:")) {
                        stats.gamesPlayed = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Blackjack Vunna spel:")) {
                        stats.gamesWon = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Blackjack Totalt vunnit eller förlorat:")) {
                        stats.totalWinLoss = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stats;
        }
        private Statistics rouletteReadStatisticsFromFile(File file) {
            Statistics stats = new Statistics();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Pengar:")) {
                        stats.money = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Roulette Spelade spel:")) {
                        stats.gamesPlayed = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Roulette Vunna spel:")) {
                        stats.gamesWon = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Roulette Totalt vunnit eller förlorat:")) {
                        stats.totalWinLoss = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stats;
        }

        private Statistics allReadStatisticsFromFile(File file) {
            Statistics allStats = new Statistics();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Pengar:")) {
                        allStats.money += Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Spelade spel:")) {
                        allStats.gamesPlayed += Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Vunna spel:")) {
                        allStats.gamesWon += Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Totalt vunnit eller förlorat:")) {
                        allStats.totalWinLoss += Integer.parseInt(line.split(":")[1].trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return allStats;
        }


        class Statistics {
            int money = 0;
            int gamesPlayed = 0;
            int gamesWon = 0;
            int totalWinLoss = 0;

            public void combine(Statistics other) {
                this.money += other.money;
                this.gamesPlayed += other.gamesPlayed;
                this.gamesWon += other.gamesWon;
                this.totalWinLoss += other.totalWinLoss;
            }
        }

    }

}