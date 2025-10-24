package com.pacman.model.components.algorithms;

import com.pacman.model.components.maze.Cell;
import com.pacman.model.components.maze.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sidewinder implements MazeAlgorithms{

    private static final Random rand = new Random();
    /**
     * Carve passages in the grid using the Sidewinder algorithm.
     * @param grid
     */
    @Override
    public void carve(Grid grid) {
        for(int i = 0; i < grid.getRows(); i++){
            List<Cell> run = new ArrayList<>();
            for(int j = 0; j < grid.getColumns(); j++){
                Cell cell = grid.getCell(i, j);
                run.add(cell);

                boolean atEasternBoundary = (cell.east == null);
                boolean atNorthernBoundary = (cell.north == null);

                // Decide whether to close out the run
                boolean shouldCloseOut = atEasternBoundary || (!atNorthernBoundary && rand.nextBoolean() );

                if(shouldCloseOut){
                    // pick one random member from the run and link north
                    Cell member = run.get(rand.nextInt(run.size()));
                    if(member.north != null){
                        member.link(member.north);
                    }
                    run.clear();
                } else {
                    cell.link(cell.east);
                }
            }
        }
    }

    /**
     * @return the name of the algorithm
     */
    @Override
    public String getName() {
        return "Sidewinder";
    }

    public static void on(Grid grid){
        Sidewinder sidewinder = new Sidewinder();
        sidewinder.carve(grid);
    }
}
