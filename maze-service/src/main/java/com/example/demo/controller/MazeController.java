package com.example.demo.controller;

import com.example.demo.models.components.algorithms.BinaryTree;
import com.example.demo.models.components.algorithms.RecursiveBacktracker;
import com.example.demo.models.components.algorithms.Sidewinder;
import com.example.demo.models.components.algorithms.TruePrims;
import com.example.demo.models.components.maze.Cell;
import com.example.demo.models.components.maze.Grid;
import com.example.demo.models.entities.MazeEntity;
import com.example.demo.repository.MazeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * REST Controller for Maze generation.
 *
 * Provides an endpoint to generate maze structures dynamically.
 * Example request:
 *   GET /api/maze?rows=20&cols=20&algo=rb
 *
 * Returns:
 *   JSON structure representing the maze grid and walls.
 */
@RestController
@RequestMapping("/api") // Base URL prefix
public class MazeController {

    private final MazeRepository mazeRepository;

    public MazeController(MazeRepository mazeRepository) {
        this.mazeRepository = mazeRepository;
    }

    /**
     * Generate a maze based on given parameters.
     *
     * @param rows  number of rows in the maze
     * @param cols  number of columns in the maze
     * @param algo  algorithm to use ("bt" = Binary Tree, "rb" = Recursive Backtracker)
     * @return      ResponseEntity containing the maze data or an error message
     */
    @GetMapping("/maze")
    public ResponseEntity<?> getMaze(
            @RequestParam(defaultValue = "10") int rows,
            @RequestParam(defaultValue = "10") int cols,
            @RequestParam(defaultValue = "rb") String algo) {

        // ✅ Validation
        if (rows <= 0 || cols <= 0) {
            Map<String, Object> error = Map.of(
                    "error", "Invalid parameters",
                    "message", "rows and cols must be positive integers",
                    "status", HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(error);
        }

        // ✅ Generate maze
        Grid grid = new Grid(rows, cols);
        switch (algo.toLowerCase()) {
            case "bt" -> BinaryTree.on(grid);
            case "rb" -> RecursiveBacktracker.on(grid);
            case "tprims" -> TruePrims.on(grid);
            case "swinder" -> Sidewinder.on(grid);
            default -> {
                Map<String, Object> error = Map.of(
                        "error", "Unknown algorithm",
                        "message", "Supported: 'bt', 'rb'",
                        "status", HttpStatus.BAD_REQUEST.value()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }



        // ✅ Serialize maze
        List<List<Map<String, Boolean>>> cells = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<Map<String, Boolean>> rowList = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                Cell cell = grid.getCell(r, c);
                Map<String, Boolean> cellMap = Map.of(
                        "north", !cell.isLinked(cell.north),
                        "east",  !cell.isLinked(cell.east),
                        "south", !cell.isLinked(cell.south),
                        "west",  !cell.isLinked(cell.west)
                );
                rowList.add(cellMap);
            }
            cells.add(rowList);
        }

        // After generating the maze, save it in MongoDB and return the ID
        MazeEntity entity = new MazeEntity(grid, algo);
        // Here you would typically save the entity using a repository, e.g. mazeRepository.save
        mazeRepository.save(entity);


        // ✅ Build JSON response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", entity.getId()); // Return the generated maze ID
        response.put("rows", rows);
        response.put("cols", cols);
        response.put("cells", cells);
        response.put("meta", Map.of(
                "algorithm", algo,
                "timestamp", new Date().toString(),
                "author", "Pacman Project - Groupe K"
        ));



        return ResponseEntity.ok(response);
    }

    /**
     * Simple health check endpoint.
     * Example: GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Maze Generator API",
                "version", "1.0.0"

        ));
    }


    /** Endpoint for system rating
     * *  Example: GET /api/maze/{id}/rating
     */
    public ResponseEntity<?> rateMaze(@RequestParam String id, @RequestParam Integer rating) {
        if (rating < 0 || rating > 5) return ResponseEntity.badRequest().body("Invalid score");

        return mazeRepository.findById(id)
            .map(maze -> {
                maze.setRating(rating);
                mazeRepository.save(maze);
                return ResponseEntity.ok(Map.of("id", id, "rating", rating));
            })
            .orElse(ResponseEntity.notFound().build());



    }
}
