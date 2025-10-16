package com.production.game2048.service;

import com.production.game2048.model.GameState;
import com.production.game2048.model.MoveDirection;

/**
 * Service interface defining the core business logic for the 2048 game.
 * This contract separates the API definition from its implementation.
 */
public interface GameService {

    /**
     * Starts a new game with a board of the given size.
     *
     * @param boardSize The dimension (YxY) of the new game board. Must be > 0.
     * @return The initial state of the newly created game.
     */
    GameState startNewGame(int boardSize);

    /**
     * Retrieves the current state of a game by its unique ID.
     *
     * @param id The ID of the game to retrieve.
     * @return The current GameState.
     * @throws com.production.game2048.exception.GameNotFoundException if no game with the given ID is found.
     */
    GameState getGameState(Long id);

    /**
     * Processes a player's move for a given game.
     * This is the core game mechanic, handling tile sliding, merging, and spawning new tiles.
     *
     * @param id The ID of the game to apply the move to.
     * @param direction The direction of the move (UP, DOWN, LEFT, RIGHT).
     * @return The updated GameState after the move.
     * @throws com.production.game2048.exception.GameNotFoundException if no game with the given ID is found.
     */
    GameState move(Long id, MoveDirection direction);
}