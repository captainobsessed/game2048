package com.production.game2048.controller;

import com.production.game2048.model.GameState;
import com.production.game2048.model.MoveDirection;
import com.production.game2048.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the 2048 game API.
 * Exposes endpoints for managing and playing the game.
 */
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Starts a new game.
     *
     * @param boardSize The desired size of the board (e.g., 4 for a 4x4 board). Defaults to 4.
     * @return A ResponseEntity containing the initial GameState.
     */
    @PostMapping
    public ResponseEntity<GameState> startNewGame(
            @RequestParam(defaultValue = "4") int boardSize) {
        // Input validation is handled by the service layer, which will throw
        // an IllegalArgumentException for non-positive sizes.
        GameState newGame = gameService.startNewGame(boardSize);
        return ResponseEntity.ok(newGame);
    }

    /**
     * Retrieves the state of an existing game.
     *
     * @param id The unique ID of the game.
     * @return A ResponseEntity containing the current GameState.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameState> getGameState(@PathVariable Long id) {
        GameState gameState = gameService.getGameState(id);
        return ResponseEntity.ok(gameState);
    }

    /**
     * Submits a move for a specific game.
     *
     * @param id The unique ID of the game.
     * @param direction The direction to move the tiles (UP, DOWN, LEFT, RIGHT).
     * @return A ResponseEntity containing the updated GameState after the move.
     */
    @PostMapping("/{id}/move")
    public ResponseEntity<GameState> move(
            @PathVariable Long id,
            @RequestParam MoveDirection direction) {
        // Spring Boot automatically converts the request parameter string (e.g., "UP")
        // to the MoveDirection enum, throwing an error for invalid values.
        GameState updatedGame = gameService.move(id, direction);
        return ResponseEntity.ok(updatedGame);
    }
}