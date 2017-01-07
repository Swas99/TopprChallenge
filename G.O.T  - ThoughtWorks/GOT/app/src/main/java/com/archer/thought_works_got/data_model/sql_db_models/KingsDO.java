package com.archer.thought_works_got.data_model.sql_db_models;

/**
 * Created by Swastik on 07-01-2017.
 */
public class KingsDO
{

    private String name;
    private String currentRank;
    private String topRank;
    private String worstRank;
    private String currentRating;
    private String highestRating;
    private String lowestRating;
    private String totalBattles;
    private String battlesWon;
    private String battlesLost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(String currentRank) {
        this.currentRank = currentRank;
    }

    public String getTopRank() {
        return topRank;
    }

    public void setTopRank(String topRank) {
        this.topRank = topRank;
    }

    public String getWorstRank() {
        return worstRank;
    }

    public void setWorstRank(String worstRank) {
        this.worstRank = worstRank;
    }

    public String getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(String currentRating) {
        this.currentRating = currentRating;
    }

    public String getHighestRating() {
        return highestRating;
    }

    public void setHighestRating(String highestRating) {
        this.highestRating = highestRating;
    }

    public String getLowestRating() {
        return lowestRating;
    }

    public void setLowestRating(String lowestRating) {
        this.lowestRating = lowestRating;
    }

    public String getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(String totalBattles) {
        this.totalBattles = totalBattles;
    }

    public String getBattlesWon() {
        return battlesWon;
    }

    public void setBattlesWon(String battlesWon) {
        this.battlesWon = battlesWon;
    }

    public String getBattlesLost() {
        return battlesLost;
    }

    public void setBattlesLost(String battlesLost) {
        this.battlesLost = battlesLost;
    }
}
