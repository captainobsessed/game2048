package com.production.game2048.model;

/**
 * Represents the four possible move directions in the 2048 game.
 * Using an enum prevents "magic string" bugs and ensures type safety.
 */
public enum MoveDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}