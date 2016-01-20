package de.uulm.mal.fancyquartett.controller;

import java.io.Serializable;

import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.enums.GameMode;

/**
 * Created by Lukas on 14.01.2016.
 */
public class PlayerController implements Serializable {

   transient private GameActivity.GameEngine engine;

    /**
     *
     * @param engine
     */
    public PlayerController(GameActivity.GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Changes current player.
     */
    public void changeCurrentPlayer() {
        int curPlayer = engine.getCurPlayer();
        if(curPlayer == engine.PLAYER1) {
            engine.setCurPlayer(engine.PLAYER2);
        } else {
            engine.setCurPlayer(engine.PLAYER1);
        }
    }

    /**
     * Checks if any player won. Returns -1 if no player has yet won.
     * @return
     */
    public int checkPlayerWon() {
        GameMode gameMode = engine.getGameMode();
        int maxPoints = engine.getMaxPoints();
        Player p1 = engine.getP1();
        Player p2 = engine.getP2();
        // win after maxRounds
        if(engine.getHasMaxRounds()) {
            if(engine.getMaxRounds() == engine.getCurRound()) {
                if(gameMode == GameMode.Points) {
                    if(p1.getPoints() > p2.getPoints()) return engine.PLAYER1;
                    if(p2.getPoints() > p1.getPoints()) return engine.PLAYER2;
                } else {
                    if(p1.getCards().size() > p2.getCards().size()) return engine.PLAYER1;
                    if(p2.getCards().size() > p1.getCards().size()) return engine.PLAYER2;
                    if(p1.getCards().size() == p2.getCards().size()) engine.increaseMaxRounds();
                }
            }
        }
        // regular win by GameMode-End
        if(gameMode == GameMode.Points) {
            if(p1.getPoints() >= maxPoints) return engine.PLAYER1;
            if(p2.getPoints() >= maxPoints) return engine.PLAYER2;
        } else if(gameMode == GameMode.Time) {
            if(engine.getCurTime() >= engine.getGameTime()) {
                if(p1.getCards().size() > p2.getCards().size()) return engine.PLAYER1;
                if(p2.getCards().size() > p1.getCards().size()) return engine.PLAYER2;
            }
        } else {
            if(p1.getCards().size() == 0) return engine.PLAYER2;
            if(p2.getCards().size() == 0) return engine.PLAYER1;
        }

        return -1;
    }

    /**
     * Adds points to players current points.
     * @param player
     * @param points
     */
    public void addPoints(Player player, int points) {
        player.setPoints(player.getPoints() + points);
    }

    /**
     * Removes points from players current points. Points can't be negative.
     * @param player
     * @param points
     */
    public void removePoints(Player player, int points) {
        int pointsNew = player.getPoints() - points;
        if(pointsNew < 0) {
            pointsNew = 0;
        }
        player.setPoints(pointsNew);
    }

    /**
     * Increases roundsWon from player.
     * @param player
     */
    public void increaseRoundsWon(Player player) {
        player.setRoundsWon(player.getRoundsWon() + 1);
    }


    public void setEngine(GameActivity.GameEngine engine) {
        this.engine = engine;
    }
}
