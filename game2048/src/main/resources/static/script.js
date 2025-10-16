document.addEventListener('DOMContentLoaded', () => {
    const boardElement = document.getElementById('game-board');
    const scoreElement = document.getElementById('score');
    const newGameBtn = document.getElementById('new-game-btn');
    const gameOverOverlay = document.getElementById('game-over-overlay');
    const restartBtn = document.getElementById('restart-btn');
    
    const API_BASE_URL = '/api/games';
    let currentGameId = null;

    /**
     * Renders the game board based on the state received from the API.
     */
    const renderBoard = (gameState) => {
        boardElement.innerHTML = ''; // Clear previous state
        const board = gameState.board;
        const boardSize = board.length;
        boardElement.style.gridTemplateColumns = `repeat(${boardSize}, 1fr)`;

        board.forEach(row => {
            row.forEach(cellValue => {
                const tile = document.createElement('div');
                tile.classList.add('tile');
                if (cellValue > 0) {
                    tile.textContent = cellValue;
                    tile.dataset.value = cellValue;
                }
                boardElement.appendChild(tile);
            });
        });

        scoreElement.textContent = gameState.score;
        
        if (gameState.gameOver) {
            gameOverOverlay.classList.remove('hidden');
        } else {
            gameOverOverlay.classList.add('hidden');
        }
    };

    /**
     * Starts a new game by calling the backend API.
     */
    const startNewGame = async () => {
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
            });
            if (!response.ok) {
                throw new Error('Failed to start a new game.');
            }
            const gameState = await response.json();
            currentGameId = gameState.id;
            renderBoard(gameState);
        } catch (error) {
            console.error(error);
            alert('Error: Could not start a new game. Is the server running?');
        }
    };

    /**
     * Sends a move command to the backend API.
     */
    const makeMove = async (direction) => {
        if (!currentGameId || gameOverOverlay.classList.contains('hidden') === false) {
            return; // Don't allow moves if game is over or not started
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${currentGameId}/move?direction=${direction}`, {
                method: 'POST',
            });
            if (!response.ok) {
                throw new Error(`Move failed with status: ${response.status}`);
            }
            const updatedGameState = await response.json();
            renderBoard(updatedGameState);
        } catch (error) {
            console.error('Error making a move:', error);
        }
    };
    
    // Event Listeners
    newGameBtn.addEventListener('click', startNewGame);
    restartBtn.addEventListener('click', startNewGame);

    window.addEventListener('keydown', (e) => {
        switch (e.key) {
            case 'ArrowUp':
                makeMove('UP');
                break;
            case 'ArrowDown':
                makeMove('DOWN');
                break;
            case 'ArrowLeft':
                makeMove('LEFT');
                break;
            case 'ArrowRight':
                makeMove('RIGHT');
                break;
        }
    });

    // Initial game start
    startNewGame();
});