package com.pacman.model.components.algorithms;

import com.pacman.model.components.maze.Cell;
import com.pacman.model.components.maze.Grid;

import java.util.*;

public class TruePrims implements MazeAlgorithms{
    private final Random rand = new Random();
    /**
     * @param grid
     */
    @Override
    public void carve(Grid grid) {
        Cell start = grid.randomCell();
        List<Cell> active = new ArrayList<>();
        active.add(start);

        Map<Cell,Integer> costs = new HashMap<>();
        for(Cell cell : grid.eachCell())
        {
            costs.put(cell, rand.nextInt(100));
        }
        while (!active.isEmpty())
        {
//            Cell cell = null;
//            int minCost = Integer.MAX_VALUE;
//            for(Cell c : active)
//            {
//                int cost = costs.get(c);
//                if(cost < minCost)
//                {
//                    minCost = cost;
//                    cell = c;
//                }
//            }

            // Version 2
            Cell cell = Collections.min(active,Comparator.comparingInt(costs::get));

            List<Cell> availableNeighbors = new ArrayList<>();
            for(Cell neighbor : cell.neighbors())
            {
                if(neighbor.links().isEmpty())
                {
                    availableNeighbors.add(neighbor);
                }

            }

            if(!availableNeighbors.isEmpty())
            {
                Cell neighbor = Collections.min(availableNeighbors,Comparator.comparingInt(costs::get));
                cell.link(neighbor);
                active.add(neighbor);
            }
            else
            {
                active.remove(cell);
            }
        }

    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return "True Prims";
    }

    public static void on(Grid grid)
    {
        TruePrims tp = new TruePrims();
        tp.carve(grid);
    }
}
