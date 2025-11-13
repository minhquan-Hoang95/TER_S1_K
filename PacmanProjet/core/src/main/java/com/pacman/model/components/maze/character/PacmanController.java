package com.pacman.model.components.maze.character;

import com.pacman.game.model.Direction;
import com.pacman.model.components.maze.Grid;

/**
 * Strategy interface for deciding Pacman's next direction.
 * Could be a human controller (keyboard) or an AI.
 */
public interface PacmanController {

    /**
     * Decide where Pacman wants to go next.
     *
     * @param pacman the Pacman instance (position, state, etc.)
     * @param grid   the maze grid
     * @return desired direction (can be NONE)
     */
    Direction decideDirection(Pacman pacman, Grid grid);
}
