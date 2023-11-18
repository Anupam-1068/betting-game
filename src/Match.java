public class Match {

    //First value is the matchId – A random UUID.
    //Second value is the return rate for A side.
    //Third value is the return rate for B side.
    //Fourth value is the result of the match.
    private String matchId;
    private double rateA;
    private double rateB;
    private char result;

    public String getMatchId() {
        return matchId;
    }

    public double getRateA() {
        return rateA;
    }

    public double getRateB() {
        return rateB;
    }

    public char getResult() {
        return result;
    }

    public Match(String matchId, double rateA, double rateB, char result) {
        this.matchId = matchId;
        this.rateA = rateA;
        this.rateB = rateB;
        this.result = result;
    }

    public boolean isValidSide(Character side) {
        return side != null && (side == 'A' || side == 'B');
    }

    public boolean isWinningSide(Character side) {
        return isValidSide(side) && result == side;
    }

    public int calculateWinningAmount(Character side, int amount) {
        if (isValidSide(side)) {
            return (int) Math.floor(amount * (side == 'A' ? rateA : rateB));
        }
        return 0;
    }

    public int calculateCasinoBalance(char result, char betSide, int betAmount, double rateA, double rateB) {
        if (result == betSide) {
            // Player won the bet
            return -(int) (betAmount * (betSide == 'A' ? rateA : rateB));
        } else if (result == 'D') {
            // Draw, return 0
            return 0;
        } else {
            // Player lost the bet
            return betAmount;
        }
    }


}
