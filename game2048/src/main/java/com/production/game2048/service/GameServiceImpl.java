package com.production.game2048.service;

import com.production.game2048.exception.GameNotFoundException;
import com.production.game2048.model.GameState;
import com.production.game2048.model.MoveDirection;
import com.production.game2048.repository.GameStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementation of the GameService interface.
 * Contains all core game logic for 2048.
 */
@Service
public class GameServiceImpl implements GameService {

    private static final int WINNING_TILE = 2048;

    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    @Autowired
    public GameServiceImpl(GameStateRepository gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    @Override
    @Transactional
    public GameState startNewGame(int boardSize) {
        if (boardSize <= 0) {
            throw new IllegalArgumentException("Board size must be positive.");
        }
        GameState newGame = new GameState();
        newGame.setBoard(new int[boardSize][boardSize]);
        newGame.setScore(0);
        newGame.setGameOver(false);
        newGame.setWon(false);

        // Start with two random tiles
        addRandomTile(newGame.getBoard());
        addRandomTile(newGame.getBoard());

        return gameStateRepository.save(newGame);
    }

    @Override
    @Transactional(readOnly = true)
    public GameState getGameState(Long id) {
        return gameStateRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game with ID " + id + " not found."));
    }

    @Override
    @Transactional
    public GameState move(Long id, MoveDirection direction) {
        GameState gameState = getGameState(id);

        if (gameState.isGameOver()) {
            return gameState; // No moves allowed if the game is over.
        }

        int[][] board = gameState.getBoard();
        int boardSize = board.length;
        // Create a deep copy to check if any tile has moved.
        int[][] boardBeforeMove = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
        
        int scoreFromMove = 0;

        // Rationale: To keep the code DRY, all moves are transformed to a "LEFT" move.
        // For example, a "RIGHT" move is a reversed board, moved "LEFT", then reversed back.
        // An "UP" move is a transposed board, moved "LEFT", then transposed back.
        switch (direction) {
            case LEFT:
                scoreFromMove = moveLeft(board);
                break;
            case RIGHT:
                reverseRows(board);
                scoreFromMove = moveLeft(board);
                reverseRows(board);
                break;
            case UP:
                transpose(board);
                scoreFromMove = moveLeft(board);
                transpose(board);
                break;
            case DOWN:
                transpose(board);
                reverseRows(board);
                scoreFromMove = moveLeft(board);
                reverseRows(board);
                transpose(board);
                break;
        }

        // Only add a new tile and update score if the board has changed.
        if (!Arrays.deepEquals(board, boardBeforeMove)) {
            gameState.setScore(gameState.getScore() + scoreFromMove);
            addRandomTile(board);

            // Check for win/loss conditions
            if (!gameState.isWon() && hasTile(board, WINNING_TILE)) {
                gameState.setWon(true);
                // In classic 2048, you can continue playing after winning.
            }

            if (!isMovePossible(board)) {
                gameState.setGameOver(true);
            }
        }
        
        gameState.setBoard(board);
        return gameStateRepository.save(gameState);
    }

    /**
     * Adds a new tile (either 2 or 4) to a random empty cell on the board.
     */
    private void addRandomTile(int[][] board) {
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == 0) {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
            // 90% chance of 2, 10% chance of 4
            board[cell[0]][cell[1]] = random.nextInt(10) == 0 ? 4 : 2;
        }
    }

    /**
     * Handles the core logic for sliding and merging tiles to the left for all rows.
     * @return The score obtained from this move.
     */
    private int moveLeft(int[][] board) {
        int score = 0;
        for (int r = 0; r < board.length; r++) {
            int[] row = board[r];
            // 1. Filter out zeros
            List<Integer> nonZeroTiles = Arrays.stream(row).filter(tile -> tile != 0).boxed().collect(Collectors.toList());

            // 2. Merge adjacent tiles
            List<Integer> mergedTiles = new ArrayList<>();
            for (int i = 0; i < nonZeroTiles.size(); i++) {
                if (i + 1 < nonZeroTiles.size() && nonZeroTiles.get(i).equals(nonZeroTiles.get(i + 1))) {
                    int mergedValue = nonZeroTiles.get(i) * 2;
                    mergedTiles.add(mergedValue);
                    score += mergedValue;
                    i++; // Skip the next tile as it has been merged
                } else {
                    mergedTiles.add(nonZeroTiles.get(i));
                }
            }

            // 3. Create the new row and copy it back to the board
            int[] newRow = new int[board.length];
            for (int i = 0; i < mergedTiles.size(); i++) {
                newRow[i] = mergedTiles.get(i);
            }
            board[r] = newRow;
        }
        return score;
    }

    /**
     * Transposes the board (rows become columns and vice-versa).
     * This is an in-place operation.
     */
    private void transpose(int[][] board) {
        for (int r = 0; r < board.length; r++) {
            for (int c = r + 1; c < board.length; c++) {
                int temp = board[r][c];
                board[r][c] = board[c][r];
                board[c][r] = temp;
            }
        }
    }

    /**
     * Reverses each row of the board.
     * This is an in-place operation.
     */
    private void reverseRows(int[][] board) {
        for (int r = 0; r < board.length; r++) {
            int left = 0;
            int right = board[r].length - 1;
            while (left < right) {
                int temp = board[r][left];
                board[r][left] = board[r][right];
                board[r][right] = temp;
                left++;
                right--;
            }
        }
    }

    /**
     * Checks if any valid moves are possible on the board.
     * A move is possible if there is an empty cell or if there are adjacent tiles with the same value.
     */
    private boolean isMovePossible(int[][] board) {
        int size = board.length;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] == 0) {
                    return true; // An empty cell exists
                }
                // Check for horizontal merge
                if (c < size - 1 && board[r][c] == board[r][c + 1]) {
                    return true;
                }
                // Check for vertical merge
                if (r < size - 1 && board[r][c] == board[r + 1][c]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasTile(int[][] board, int tileValue) {
        return Arrays.stream(board).flatMapToInt(Arrays::stream).anyMatch(tile -> tile == tileValue);
    }
}