package com.pacman.model.components.algorithms;

import com.pacman.model.components.maze.Cell;
import com.pacman.model.components.maze.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RecursiveBacktracker implements MazeAlgorithms{
    private static final Random rand = new Random();
    /**
     * to carve the maze using Recursive Backtracker algorithm
     * @param grid
     */
    @Override
    public void carve(Grid grid) {
        Stack<Cell> stack = new Stack<>(); // stack to hold the cells
        Cell start = grid.randomCell(); // start from a random cell
        stack.push(start); // push the start cell to the stack

        while (!stack.isEmpty()) { // while stack is not empty
            Cell current = stack.peek(); // get the top cell from the stack

            // get unvisited neighbors
            List<Cell> unvisited = new ArrayList<>();
            for(Cell neighbor : current.neighbors() )
            {
                if(neighbor.links().isEmpty())
                {
                    unvisited.add(neighbor);
                }
            }

            if(!unvisited.isEmpty())
            {
                // choose a random unvisited neighbor
                Cell neighbor = unvisited.get(rand.nextInt(unvisited.size()));
                // link the current cell with the chosen neighbor
                current.link(neighbor);
                // push the neighbor to the stack
                stack.push(neighbor);
            }
            else
            {
                // backtrack
                stack.pop();
            }
        }

    }

    /**
     * @return the name of the algorithm
     */
    @Override
    public String getName() {
        return "Recursive Backtracker";
    }

    public  static void on(Grid grid)
    {
        RecursiveBacktracker rb = new RecursiveBacktracker();
        rb.carve(grid);
    }
}
