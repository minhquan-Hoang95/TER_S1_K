package com.pacman.model.components.algorithms;

import com.pacman.model.components.maze.Cell;
import com.pacman.model.components.maze.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BinaryTree implements  MazeAlgorithms {

    @Override
    public void carve(Grid grid) {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                List<Cell> neighbors = new ArrayList<>();
                Cell cell = grid.getCell(i, j);
                if (cell.north != null) {
                    neighbors.add(cell.north);
                }
                if (cell.east != null) {
                    neighbors.add(cell.east);
                }

                if (!neighbors.isEmpty()) {
                    Random rand = new Random();
                    Cell neighbor = neighbors.get(rand.nextInt(neighbors.size()));
                    cell.link(neighbor, true);
                }

            }

        }
    }

    @Override
    public String getName() {
        return "Binary Tree";
    }

    public static void on(Grid grid) {
        BinaryTree binaryTree = new BinaryTree();
        binaryTree.carve(grid);
    }
}
