import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Player implements Comparable<Player> {

    public String getPlayerId() {
        return playerId;
    }

    private final String playerId;

    private long balance;
    private int betsPlaced;
    private int betsWon;
    private String matchId;
    private Integer coin;


    private Character side;
    private boolean hasIllegalAction = false;

    private Optional<String> firstIllegalOperation = Optional.empty();
    public long getBalance() {
        return balance;
    }



    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }


    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public void setSide(Character side) {
        this.side = side;
    }



    public Integer getCoin() {
        return coin;
    }

    public Character getSide() {
        return side;
    }





    public Player(String playerId) {
        this.playerId = playerId;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {

        if (amount <= balance) {
            balance -= amount;
        } else {
            // Handle illegal withdrawal
            firstIllegalOperation = Optional.of("WITHDRAW");
            hasIllegalAction = true; // Set the flag to true

        }
    }

    public void placeBet(Optional<Match> match, int amount, Character side) {
        if (amount <= balance && match.isPresent() && match.get().isValidSide(side)) {
            betsPlaced++;
            if (match.get().isWinningSide(side)) {
                balance += match.get().calculateWinningAmount(side, amount);
                betsWon++;
            } else {
                balance -= amount;
            }
        } else {
            // Handle illegal bet
            firstIllegalOperation = Optional.of("BET");
            hasIllegalAction = true; // Set the flag to true

        }
    }

    public boolean isLegitimate() {
        return !hasIllegalAction;
    }

    public boolean isLegitimateOrNot(boolean illegal) {
        return illegal;
    }

    public String getFirstIllegalOperation() {
        return firstIllegalOperation.orElse("No illegal operation");
    }

    public double getWinRate() {
        return betsPlaced == 0 ? 0 : (double) betsWon / betsPlaced;
    }

    @Override
    public int compareTo(Player o) {
        return 0;
    }
}
