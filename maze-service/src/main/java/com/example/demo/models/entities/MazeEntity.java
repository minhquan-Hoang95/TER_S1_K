package com.example.demo.models.entities;

import com.example.demo.models.components.maze.Cell;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entité JPA/MongoDB pour stocker les labyrinthe en base de données
 *
 * Utilisation : Sauvegarde et récupération des mazes générés
 * Collection MongoDB : "mazes"
 *
 * Chaque document contient :
 * - Métadonnées (id, dimensions, algo utilisé, rating)
 * - Structure complète du maze (toutes les cellules et murs)
 */
@Document(collection = "mazes")
public class MazeEntity {

    /**
     * ID unique du document MongoDB
     * Généré automatiquement si non fourni
     */
    @Id
    private String id;

    private int rows;
    private int cols;

    /**
     * Algorithme utilisé pour générer le maze
     * Valeurs possibles : "rb" (RecursiveBacktracker), "tp" (TruePrims),
     * "bt" (BinaryTree), "sw" (Sidewinder)
     */
    private String algorithm;
    /**
     * Structure complète du maze
     * Format : List<List<Map<String, Boolean>>>
     *
     * Structure détaillée :
     * cells[row][col] = {
     *   "north": false,    // false = passage, true = mur
     *   "south": true,
     *   "east": false,
     *   "west": true
     * }
     *
     * Exemple pour cellule (0,0) :
     * cells[0][0] = {"north": true, "south": false, "east": false, "west": true}
     * => Mur au nord, passage au sud, passage à l'est, mur à l'ouest
     */
    private List<List<Map<String, Boolean>>> cells = new ArrayList<>();
    /**
     * Rating/Note du maze
     * Valeurs possibles : 0 à 5 (null = non noté)
     *
     * Utilisé pour :
     * - Évaluer la qualité/difficulté du maze
     * - Trier/filtrer les mazes par qualité
     * - Statistiques pour le prof/administrateur
     */
    private Integer rating; // 0..5 ou null

    /**
     * Timestamp de création
     * Optionnel : utile pour tracer quand le maze a été créé
     */
    private Long createdAt;


    /**
     * Constructeur par défaut (requis par MongoDB)
     */
    public MazeEntity() {
        this.rating = null;

        this.createdAt = System.currentTimeMillis();
    }
    /**
     * Constructeur avec paramètres principaux
     */
    public MazeEntity(int rows, int cols, String algorithm, List<List<Map<String, Boolean>>> cells) {
        this.rows = rows;
        this.cols = cols;
        this.algorithm = algorithm;
        this.cells = cells;
        this.rating = null;
        this.createdAt = System.currentTimeMillis();
    }

    // GETTERS
    public String getId() { return id; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public String getAlgorithm() { return algorithm; }
    public List<List<Map<String, Boolean>>> getCells() { return cells; }
    public Integer getRating() { return rating; }
    public Long getCreatedAt() { return createdAt; }

    // SETTERS
    public void setId(String id) { this.id = id; }
    public void setRows(int rows) { this.rows = rows; }
    public void setCols(int cols) { this.cols = cols; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    public void setCells(List<List<Map<String, Boolean>>> cells) { this.cells = cells; }
    public void setRating(Integer rating) { this.rating = rating; }
    // ==================== UTILITAIRES ====================

    /**
     * Obtenir les informations du maze sous forme de string
     */
    @Override
    public String toString() {
        return String.format(
            "MazeEntity{id='%s', rows=%d, cols=%d, algorithm='%s', rating=%s}",
            id, rows, cols, algorithm, rating
        );
    }

    /**
     * Obtenir la taille totale du maze
     */
    public int getTotalCells() {
        return rows * cols;
    }



}

