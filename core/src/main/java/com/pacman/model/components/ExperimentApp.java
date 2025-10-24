package com.pacman.model.components;

import com.pacman.model.components.algorithms.*;
import com.pacman.model.components.maze.Cell;
import com.pacman.model.components.maze.Grid;

public class ExperimentApp {
    public static void main(String[] args) {
        System.out.println("ExperimentApp is running...");
        // Set grid
        Grid full = new Grid(10, 15);
        //Grid half = new Grid(10, 15/2);
//        RecursiveBacktracker.on(half);
//        half.braid(0.6);
        TruePrims truePrims = new TruePrims();
        truePrims.carve(full);
        full.braid(0.5);
        //truePrims.carve(half);
       // half.braid(0.5);
        //System.out.println("Half Grid:");
        //System.out.println(half.info());
        //System.out.println(half.toAscii());

        // Mirror left half to right half
        //mirrorGrid(half, full);
        //mirrorLeftToRightV2(half, full);


        System.out.println("--------------------- Full Mirrored Grid:--------------------");
        System.out.println(full.info());
        System.out.println(full.toAscii());


       // Cell cell = grid.getCell(2, 2);
        // Check neighbors
//        System.out.println("Cell at (2,2) neighbors:");
//        System.out.println("North: " + (cell.north != null ? "(" + cell.north.row + "," + cell.north.col + ")" : "null"));
//        System.out.println("South: " + (cell.south != null ? "(" + cell.south.row + "," + cell.south.col + ")" : "null"));
//        System.out.println("East: " + (cell.east != null ? "(" + cell.east.row + "," + cell.east.col + ")" : "null"));
//        System.out.println("West: " + (cell.west != null ? "(" + cell.west.row + "," + cell.west.col + ")" : "null"));


        // Apply Binary Tree algorithm
//        System.out.println("\nApplying Binary Tree Algorithm:");
//        BinaryTree.on(grid);
//        System.out.println(grid.info());
//        System.out.println(grid.toAscii());



        // Reset grid
      //  grid = new Grid(10, 9);
        // Apply Sidewinder algorithm
//        System.out.println("\nApplying Sidewinder Algorithm:");
//        Sidewinder.on(grid);
//        System.out.println(grid.info());
//        System.out.println(grid.toAscii());
//
//        grid = new Grid(10, 15);
//        RecursiveBacktracker.on(grid);
//        System.out.println(grid.info());
//
//        System.out.println(grid.toAscii());
//
//        grid = new Grid(10,15);
        //TruePrims.on(grid);
//        RecursiveBacktracker.on();
//        System.out.println(grid.info());
//        System.out.println(grid.toAscii());
//
//        System.out.println("Applying Braid Algorithm:");
//        grid.braid(0.6); //
//        System.out.println(grid.info());
//        System.out.println(grid.toAscii());

        // prensent int Acsii

    }

    // Mirror the left half of the grid to the right half but no middle
    public static void mirrorGrid(Grid half, Grid full) {
        for(int r = 0; r < full.getRows(); r++) {
            for(int c = 0; c < full.getColumns()/2; c++) {
                Cell leftCell = half.getCell(r, c); // cell from half grid
                Cell fullLeft = full.getCell(r, c); // left side in full grid
                Cell fullRight = full.getCell(r, full.getColumns() - c - 1); // mirrored position

                // Copy left side directly
                if (leftCell.isLinked(leftCell.north)) fullLeft.link(fullLeft.north);
                if (leftCell.isLinked(leftCell.south)) fullLeft.link(fullLeft.south);
                if (leftCell.isLinked(leftCell.east))  fullLeft.link(fullLeft.east);
                if (leftCell.isLinked(leftCell.west))  fullLeft.link(fullLeft.west);

                // Mirror to right side
                if (leftCell.isLinked(leftCell.north)) fullRight.link(fullRight.north);
                if (leftCell.isLinked(leftCell.south)) fullRight.link(fullRight.south);
                if (leftCell.isLinked(leftCell.east))  fullRight.link(fullRight.west);
                if (leftCell.isLinked(leftCell.west))  fullRight.link(fullRight.east);
            }
        }

    }

//    public static void mirrorLeftToRight(Grid half, Grid full) {
//        int rows = full.getRows();
//        int cols = full.getColumns();
//
//        if (cols % 2 != 0)
//            throw new IllegalArgumentException("Grid must have even number of columns.");
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols / 2; c++) {
//                Cell left = half.getCell(r, c);
//                Cell right = full.getCell(r, cols - c - 1);
//                Cell leftFull = full.getCell(r, c);
//                if (left == null || right == null || leftFull == null) continue;
//
//                // Copy left side
//                if (left.isLinked(left.north)) leftFull.link(leftFull.north);
//                if (left.isLinked(left.south)) leftFull.link(leftFull.south);
//                if (left.isLinked(left.east))  leftFull.link(leftFull.east);
//                if (left.isLinked(left.west))  leftFull.link(leftFull.west);
//
//                // Mirror right side
//                if (left.isLinked(left.north)) right.link(right.north);
//                if (left.isLinked(left.south)) right.link(right.south);
//                if (left.isLinked(left.east))  right.link(right.west);
//                if (left.isLinked(left.west))  right.link(right.east);
//            }
//        }
//    }





}
