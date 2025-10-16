package com.production.game2048.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.production.game2048.util.IntArrayConverter;

/**
 * Represents the state of a single 2048 game session.
 * This entity is persisted to the database to allow for game state
 * to be saved and loaded across application restarts.
 */
@Entity
@Table(name = "game_state")
public class GameState {

    /**
     * The unique identifier for the game session.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The game board, stored as a 2D integer array.
     * The state of this board is converted to a database-friendly format (a String)
     * using the {@link IntArrayConverter}.
     */
    @Column(nullable = false, length = 1024)
    @Convert(converter = IntArrayConverter.class)
    private int[][] board;

    /**
     * The current score of the game.
     */
    @Column(nullable = false)
    private int score;

    /**
     * A flag indicating whether the game is over.
     * The game is over when the player reaches 2048 or no more moves are possible.
     */
    @Column(nullable = false)
    private boolean gameOver;

    /**
     * A flag indicating whether the player has won the game.
     * The game is won when a tile with the value 2048 is created.
     */
    @Column(nullable = false)
    private boolean won;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}