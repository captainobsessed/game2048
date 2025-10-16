package com.production.game2048.repository;

import com.production.game2048.model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link GameState} entity.
 *
 * This interface provides all standard CRUD (Create, Read, Update, Delete) operations
 * for GameState entities out-of-the-box. Spring Data JPA automatically implements
 * these methods at runtime.
 */
@Repository
public interface GameStateRepository extends JpaRepository<GameState, Long> {
    // No method declarations are needed for standard CRUD operations.
    // We can add custom query methods here later if required.
}