package com.pacman;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pacman.model.components.ExperimentApp;
import com.pacman.model.components.algorithms.RecursiveBacktracker;
import com.pacman.model.components.algorithms.TruePrims;
import com.pacman.model.components.maze.Cell;
import com.pacman.model.components.maze.Grid;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private ShapeRenderer shapeRenderer;
    private Grid grid;
    private final int CELL_SIZE = 32; // Size of each cell in pixels

    @Override
    public void create() {
        // Prepare your application here.
        shapeRenderer = new ShapeRenderer(); // Initialize the ShapeRenderer for drawing shapes

        // Example: Create a grid of 10x10 cells
        grid = new Grid(10, 15);
       // Grid half = new Grid(10, 15/2);
        //RecursiveBacktracker recursiveBacktracker = new RecursiveBacktracker();
        //recursiveBacktracker.carve(half);
        TruePrims truePrims = new TruePrims();
        truePrims.carve(grid);
        grid.braid(0.5);
       // truePrims.carve(half);
        //half.braid(1.0);

      //  ExperimentApp.mirrorGrid(half, grid);

    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear the screen with black color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the color buffer

        // 1️⃣ Draw filled cell colors
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        drawCellBackgrounds();
//        shapeRenderer.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1); // Set color to white

        drawMaze();
        shapeRenderer.end();
    }

    private void drawCellBackgrounds() {
        int mazeWidth = grid.getColumns() * CELL_SIZE;
        int mazeHeight = grid.getRows() * CELL_SIZE;

        int offsetX = (Gdx.graphics.getWidth() - mazeWidth) / 2;
        int offsetY = (Gdx.graphics.getHeight() - mazeHeight) / 2;

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell == null) continue;

                int x = offsetX + j * CELL_SIZE;
                int y = offsetY + i * CELL_SIZE;

                // Example color: random or based on position
                float hue = (float) j / grid.getColumns();     // from 0 to 1
                float brightness = (float) i / grid.getRows(); // from 0 to 1
                shapeRenderer.setColor(hue, brightness, 1 - hue, 1); // RGB values 0–1

                // Draw filled square for the cell
                shapeRenderer.rect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }


    private void drawMaze() {
        int mazeWidth = grid.getColumns() * CELL_SIZE;
        int mazeHeight = grid.getRows() * CELL_SIZE;

        int offsetX = (Gdx.graphics.getWidth() - mazeWidth) / 2;
        int offsetY = (Gdx.graphics.getHeight() - mazeHeight) / 2;

        for(int i = 0; i < grid.getRows(); i++) {
            for(int j = 0; j < grid.getColumns(); j++) {

                Cell cell = grid.getCell(i, j);
                if (cell == null) continue;

                int x1 = offsetX + j * CELL_SIZE;
                int y1 = offsetY + i * CELL_SIZE;
                int x2 = x1 + CELL_SIZE;
                int y2 = y1 + CELL_SIZE;

                // Draw walls only where there is NO link
                if (cell.north == null || !cell.isLinked(cell.north))
                    shapeRenderer.line(x1, y2, x2, y2); // top
                if (cell.west == null || !cell.isLinked(cell.west))
                    shapeRenderer.line(x1, y1, x1, y2); // left
                if (cell.east == null || !cell.isLinked(cell.east))
                    shapeRenderer.line(x2, y1, x2, y2); // right
                if (cell.south == null || !cell.isLinked(cell.south))
                    shapeRenderer.line(x1, y1, x2, y1); // bottom
            }
        }
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.

    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
        shapeRenderer.dispose();
    }
}
