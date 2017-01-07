package com.archer.thought_works_got.util;

/**
 * Created by Swastik on 07-01-2017.
 */
public class EloRatingSystem {

    // Score constants
    public final static double WIN = 1.0;
    public final static double DRAW = 0.5;
    public final static double LOSS = 0.0;
    private static final double K_FACTOR = 32.0;
    private static final int GAME_DRAW = 103;


    public static double getNewRating (double rating, double opponentRating, int resultType) {
        switch (resultType) {
            case GOT_Util.OUTCOME_WIN:
                return getNewRating (rating, opponentRating, WIN);
            case GOT_Util.OUTCOME_LOSS:
                return getNewRating (rating, opponentRating, LOSS);
            case GAME_DRAW:
                return getNewRating (rating, opponentRating, DRAW);
        }
        return -1;  // no score this time.
    }

    private static double getNewRating(double rating, double opponentRating, double score) {
        double kFactor       = K_FACTOR;
        double expectedScore = getExpectedScore(rating, opponentRating);

        return calculateNewRating(rating, score, expectedScore, kFactor);
    }

    private static double calculateNewRating(double oldRating, double score, double expectedScore, double kFactor) {
        return oldRating +  (kFactor * (score - expectedScore));
    }

    private static double getExpectedScore(double rating, double opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ( (opponentRating - rating) / 400.0)));
    }
}
