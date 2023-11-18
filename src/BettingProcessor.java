import java.io.*;
import java.util.*;

public class BettingProcessor {
    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Match> matches = new HashMap<>();
    private long casinoBalance = 0;

    private ArrayList<Object> arrayList = new ArrayList<>();

    public static void main(String[] args) {

        BettingProcessor bettingProcessor = new BettingProcessor();
        bettingProcessor.processData();
        bettingProcessor.writeResultsToFile();


    }

    private void processData() {
        try {
            // Read player data and match data from files
            readMatchData();
            readPlayerData();

        } catch (IOException e) {
            System.err.println("Error reading data: " + e.getMessage());
        }
    }

    private void readPlayerData() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/player_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processPlayerAction(line);
            }
        }
    }

    private void readMatchData() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/match_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processMatchResult(line);
            }
        }
    }


    private void processPlayerAction(String line) {
        if (line != null) {
            // Split the line into individual values
            String[] values = line.split(",");

            // Extract values
            String playerId = values[0];
            String operation = values[1];
            String matchId = values.length > 2 ? values[2] : null;
            int coins = Integer.parseInt(values[3]);
            Character side = values.length > 4 ? values[4].charAt(0) : null;

            // Process the player action
            Player player = players.computeIfAbsent(playerId, Player::new);
            player.setMatchId(matchId);
            player.setCoin(coins);
            player.setSide(side);
            for (Object o : arrayList) {
                if (o == player.getPlayerId()) {
                    player.isLegitimateOrNot(false);
                }
            }
            // Check if the player has made any illegal actions
            if (player.isLegitimateOrNot(false)) {
                // Player has made at least one illegal action, skip processing
                System.err.println("Player " + playerId + " has made illegal actions. Skipping further processing.");
            } else {
                switch (operation) {
                    case "DEPOSIT":
                        player.deposit(coins);
                        break;
                    case "BET":
                        Optional<Match> match = Optional.ofNullable(matches.get(matchId));
                        if (match.isPresent()) {
                            {
                                casinoBalance +=
                                        match.get().calculateCasinoBalance(
                                                match.get().getResult(), side, coins,
                                                match.get().getRateA(), match.get().getRateB()
                                        );
//                                System.out.println(casinoBalance);
                                player.placeBet(match, coins, side);
                            }
                        } else {
                            // Handle the case where the match is not present
                            System.err.println("Match not found for BET operation");
                        }
                        break;
                    case "WITHDRAW":
                        player.withdraw(coins);
                        break;

                    default:
                        System.err.println("Unknown operation: " + operation);
                        break;
                    // Handle unknown operation

                }
            }
        }
    }

    private void processMatchResult(String line) {
        // Split the line into individual values
        String[] values = line.split(",");

        // Extract values
        String matchId = values[0];
        double rateA = Double.parseDouble(values[1]);
        double rateB = Double.parseDouble(values[2]);
        char result = values[3].charAt(0);

        // Process the match result
        Match match = new Match(matchId, rateA, rateB, result);
        matches.put(matchId, match);
    }


    private void writeResultsToFile() {

        try (PrintWriter writer = new PrintWriter("result.txt")) {
            // Write legitimate players
            players.values().stream()
                    .filter(Player::isLegitimate)
                    .sorted()
                    .forEach(player -> {
                        double winRate = player.getWinRate();
                        writer.printf("%s %d %.2f%n", player.getPlayerId(), player.getBalance(), winRate);
                    });
            writer.println();


            // Write illegitimate players
            players.values().stream()
                    .filter(player -> !player.isLegitimate())
                    .sorted()
                    .forEach(player -> {
                        String illegalOperation = player.getFirstIllegalOperation();
                        if (Objects.equals(illegalOperation, "WITHDRAW")) {
                            writer.printf("%s %s null %s null%n", player.getPlayerId(),
                                    illegalOperation,player.getCoin(),player.getSide());
                        } else {
                            writer.printf("%s %s %s %s %s%n", player.getPlayerId(),
                                    illegalOperation, player.getMatchId(),player.getCoin(),player.getSide());
                        }
                    });
            writer.println();
            writer.println(casinoBalance);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle file writing errors
        }
    }


}

