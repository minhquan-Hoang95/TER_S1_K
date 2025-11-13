package com.example.demo.controller;

import com.example.demo.models.components.algorithms.BinaryTree;
import com.example.demo.models.components.algorithms.RecursiveBacktracker;
import com.example.demo.models.components.algorithms.Sidewinder;
import com.example.demo.models.components.algorithms.TruePrims;
import com.example.demo.models.components.maze.Cell;
import com.example.demo.models.components.maze.Grid;
import com.example.demo.models.components.maze.PacmanMaze;
import com.example.demo.models.entities.MazeEntity;
import com.example.demo.repository.MazeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/maze") // Base URL prefix
public class MazeController {

    @Autowired
    private MazeRepository mazeRepository;

    private static final Random random = new Random();

    // ==================== ROUTES PRINCIPALES ====================

    /**
     * Route par défaut : génère un maze Pac-Man optimisé (31x28)
     * Utilise Recursive Backtracker par défaut
     *
     * Usage : GET /api/maze/random
     */
    @GetMapping("/random")
    public ResponseEntity<?> generateRandomMaze() {
        return generatePacmanMaze("rb", 31, 28);
    }

    /**
     * Génère un maze avec un algorithme spécifique (sans adaptation Pac-Man)
     *
     * Usage :
     * - GET /api/maze/generate?rows=21&cols=19&algo=tp
     * - GET /api/maze/generate?algo=bt
     *
     * @param rows nombre de lignes (défaut: 21)
     * @param cols nombre de colonnes (défaut: 19)
     * @param algo algorithme: rb, tp, bt, sw (défaut: rb)
     */
    @GetMapping("")
    public ResponseEntity<?> generateMaze(
        @RequestParam(defaultValue = "21") int rows,
        @RequestParam(defaultValue = "18") int cols,
        @RequestParam(defaultValue = "rb") String algo) {

        try {
            Grid maze = new Grid(rows, cols);

            switch (algo.toLowerCase()) {
                case "rb" -> RecursiveBacktracker.on(maze);
                case "tp" -> TruePrims.on(maze);
                case "bt" -> BinaryTree.on(maze);
                case "sw" -> Sidewinder.on(maze);
                default -> RecursiveBacktracker.on(maze);
            }

            MazeEntity entity = createMazeEntity(maze, algo);
            MazeEntity saved = mazeRepository.save(entity);

            return ResponseEntity.ok(toJsonResponse(saved));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(errorResponse("Maze generation failed: " + e.getMessage()));
        }
    }

    /**
     * ⭐ ROUTE PRINCIPALE : Génère un VRAI maze Pac-Man
     *
     * C'est LA route que tu veux utiliser pour Pac-Man !
     *
     * Usage :
     * - GET /api/maze/pacman-generated
     * - GET /api/maze/pacman-generated?algo=tp
     * - GET /api/maze/pacman-generated?rows=31&cols=28&algo=rb
     *
     * Résultat : Maze 31x28 avec :
     * ✅ Symétrie double
     * ✅ Cycles (imparfait)
     * ✅ Ghost House centrale
     * ✅ Tunnels latéraux
     * ✅ Prêt pour LibGDX
     */
    @GetMapping("/pacman-generated")
    public ResponseEntity<?> generatePacmanMaze(
        @RequestParam(defaultValue = "rb") String algo,
        @RequestParam(defaultValue = "31") int rows,
        @RequestParam(defaultValue = "28") int cols) {

        try {
            // ÉTAPE 1 : Générer maze parfait
            Grid baseMaze = new Grid(rows, cols);


            switch (algo.toLowerCase()) {
                case "rb" -> RecursiveBacktracker.on(baseMaze);
                case "tp" -> TruePrims.on(baseMaze);
                case "bt" -> BinaryTree.on(baseMaze);
                case "sw" -> Sidewinder.on(baseMaze);
                default -> RecursiveBacktracker.on(baseMaze);
            }

            baseMaze.braid(1.0); // Braid 100% pour éliminer tous les dead-ends

            // Rendre imparfait avant adaptation
            //baseMaze.braid(0.5); // Braid 50% pour plus de cycles initiaux

            // ÉTAPE 2 : Adapter pour Pac-Man
            PacmanMaze adapter = new PacmanMaze(baseMaze);
            adapter.braidMaze(1.0); // Braid 100% pour éliminer dead-ends après adaptation
            adapter.applyHorizontalSymmetry();
            adapter.carveMainRooms();
            adapter.carveStraightHallways();
            adapter.carveGhostHouse();
            adapter.addTunnel();
            Grid pacmanMaze = adapter.result();

            // ÉTAPE 3 : Créer l'entité pour MongoDB
            MazeEntity entity = new MazeEntity();
            entity.setRows(pacmanMaze.getRows());
            entity.setCols(pacmanMaze.getColumns());
            entity.setAlgorithm(algo);
            entity.setCells(serializeCells(pacmanMaze.getGrid()));

            // ✅ VÉRIFICATION AVANT SAUVEGARDE
            pacmanMaze.printStats();


            // ÉTAPE 4 : Sauvegarder
            MazeEntity saved = mazeRepository.save(entity);

            System.out.println("✅ Pac-Man maze generated: " + saved.getId());
            System.out.println("   - Dimensions: " + rows + "x" + cols);
            System.out.println("   - Algorithm: " + algo);
            System.out.println("   - Properties: Symmetric, Cycles, GhostHouse, Tunnels");

            return ResponseEntity.ok(toJsonResponse(saved));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(errorResponse("Pac-Man maze generation failed: " + e.getMessage()));
        }
    }

    /**
     * Récupérer un maze par son ID
     *
     * Usage : GET /api/maze/60d5ec4f1234567890abcdef
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMazeById(@PathVariable String id) {
        try {
            Optional<MazeEntity> maze = mazeRepository.findById(id);
            if (maze.isPresent()) {
                return ResponseEntity.ok(toJsonResponse(maze.get()));
            } else {
                return ResponseEntity.status(404)
                    .body(errorResponse("Maze not found with id: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(errorResponse("Error retrieving maze: " + e.getMessage()));
        }
    }

    /**
     * Récupérer TOUS les mazes
     *
     * Usage : GET /api/maze/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMazes() {
        try {
            List<MazeEntity> mazes = mazeRepository.findAll();
            return ResponseEntity.ok(Map.of(
                "count", mazes.size(),
                "mazes", mazes.stream().map(this::toJsonResponse).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(errorResponse("Error retrieving mazes: " + e.getMessage()));
        }
    }

    /**
     * Récupérer mazes par algorithme
     *
     * Usage : GET /api/maze/by-algorithm?algo=rb
     */
    @GetMapping("/by-algorithm")
    public ResponseEntity<?> getMazesByAlgorithm(@RequestParam String algo) {
        try {
            List<MazeEntity> mazes = mazeRepository.findByAlgorithm(algo);
            return ResponseEntity.ok(Map.of(
                "algorithm", algo,
                "count", mazes.size(),
                "mazes", mazes.stream().map(this::toJsonResponse).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(errorResponse("Error retrieving mazes: " + e.getMessage()));
        }
    }


    /**
     * Noter un maze (rating 0-5)
     *
     * Usage : POST /api/maze/rate/60d5ec4f1234567890abcdef?rating=4
     */
    @PostMapping("/rate/{id}")
    public ResponseEntity<?> rateMaze(
        @PathVariable String id,
        @RequestParam int rating) {

        if (rating < 0 || rating > 5) {
            return ResponseEntity.status(400)
                .body(errorResponse("Rating must be between 0 and 5"));
        }

        try {
            Optional<MazeEntity> mazeOpt = mazeRepository.findById(id);
            if (mazeOpt.isPresent()) {
                MazeEntity maze = mazeOpt.get();
                maze.setRating(rating);
                MazeEntity updated = mazeRepository.save(maze);
                return ResponseEntity.ok(Map.of(
                    "message", "Maze rated successfully",
                    "id", id,
                    "rating", rating
                ));
            } else {
                return ResponseEntity.status(404)
                    .body(errorResponse("Maze not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(errorResponse("Error rating maze: " + e.getMessage()));
        }
    }

    // ==================== UTILITAIRES ====================

    /**
     * Convertir Cell[][] en List<List<Map<String, Boolean>>> pour MongoDB
     *
     * Conversion :
     * - true = MUR (pas de lien)
     * - false = PASSAGE (lien existe)
     */
    private List<List<Map<String, Boolean>>> serializeCells(Cell[][] cells) {
        List<List<Map<String, Boolean>>> result = new ArrayList<>();

        for (Cell[] row : cells) {
            List<Map<String, Boolean>> rowList = new ArrayList<>();
            for (Cell cell : row) {
                Map<String, Boolean> cellMap = new HashMap<>();
                cellMap.put("north", cell.north == null || !cell.isLinked(cell.north));
                cellMap.put("south", cell.south == null || !cell.isLinked(cell.south));
                cellMap.put("east", cell.east == null || !cell.isLinked(cell.east));
                cellMap.put("west", cell.west == null || !cell.isLinked(cell.west));
                rowList.add(cellMap);
            }
            result.add(rowList);
        }

        return result;
    }

    /**
     * Créer une MazeEntity à partir d'un Maze
     */
    private MazeEntity createMazeEntity(Grid maze, String algorithm) {
        MazeEntity entity = new MazeEntity();
        entity.setRows(maze.getRows());
        entity.setCols(maze.getColumns());
        entity.setAlgorithm(algorithm);
        entity.setCells(serializeCells(maze.getGrid()));
        return entity;
    }

    /**
     * Convertir MazeEntity en JSON response
     */
    private Map<String, Object> toJsonResponse(MazeEntity entity) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", entity.getId());
        response.put("rows", entity.getRows());
        response.put("cols", entity.getCols());
        response.put("algorithm", entity.getAlgorithm());
        response.put("rating", entity.getRating());
        response.put("createdAt", entity.getCreatedAt());
        response.put("totalCells", entity.getRows() * entity.getCols());
        response.put("cells", entity.getCells());
        return response;
    }

    /**
     * Créer une réponse d'erreur standardisée
     */
    private Map<String, String> errorResponse(String message) {
        return Map.of("error", message, "timestamp", System.currentTimeMillis() + "");
    }


}

