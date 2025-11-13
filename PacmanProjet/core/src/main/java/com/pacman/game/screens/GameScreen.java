package com.pacman.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.pacman.game.model.Maze;
import com.pacman.game.renderers.MazeRenderer;
import com.pacman.game.service.MazeApiClient;

/**
 * Écran principal du jeu
 * Gère le cycle de vie:
 * 1. Chargement maze depuis API
 * 2. Rendu chaque frame
 * 3. Logique jeu
 */
public class GameScreen implements Screen {

    // ===== COMPOSANTS =====

    // Caméra pour transformation coordonnées (LibGDX)
    private OrthographicCamera camera;

    // Renderer pour dessiner le maze
    private MazeRenderer mazeRenderer;

    // Données du maze (charger au démarrage)
    private Maze currentMaze;

    // Service HTTP pour récupérer maze
    private MazeApiClient apiClient;

    // Status texte (chargement, erreurs)
    private String loadingStatus = "Loading maze...";

    /**
     * Constructeur: initialiser tous les composants
     */
    public GameScreen() {
        // Créer caméra orthographique (2D)
        // Coordonnées: (0,0) en bas-gauche, (800, 600) en haut-droite
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1000, 800);

        // Créer renderer pour dessiner maze
        mazeRenderer = new MazeRenderer();

        // Créer client API pour récupérer maze
        apiClient = new MazeApiClient();

        // Charger un maze depuis API (asynchrone)
        apiClient.fetchMaze(new MazeApiClient.MazeCallback() {
            @Override
            public void onSuccess(Maze maze) {
                // Succès: sauvegarder maze et log
                currentMaze = maze;
                loadingStatus = "Maze loaded: " + maze.rows + "x" + maze.cols;
                Gdx.app.log("GameScreen", loadingStatus);
            }

            @Override
            public void onError(String error) {
                // Erreur: sauvegarder message et log
                loadingStatus = "Error: " + error;
                Gdx.app.error("GameScreen", loadingStatus);
            }
        });
    }

    /**
     * Appelé chaque frame du jeu
     * @param delta temps écoulé depuis dernière frame (en secondes)
     */
    @Override
    public void render(float delta) {
        // Effacer écran (noir)
        Gdx.gl.glClearColor(0, 0, 0, 1);  // Noir + opaque
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  // Appliquer couleur

        // Mettre à jour caméra (position, rotation, zoom)
        camera.update();

        // Dessiner maze si chargé
        if (currentMaze != null) {
            // Appliquer matrice de caméra au renderer
            // (pour respecter coordonnées caméra)
            mazeRenderer.render(currentMaze);
        } else {
            // Afficher status si maze pas encore chargé
            Gdx.app.log("GameScreen", "Status: " + loadingStatus);
        }
    }

    /**
     * Appelé quand écran devient visible
     */
    @Override
    public void show() {
        // Initialisation si besoin
    }

    /**
     * Appelé quand taille fenêtre change
     */
    @Override
    public void resize(int width, int height) {
        // Ajuster caméra au nouvelle taille
        camera.setToOrtho(false, width, height);
    }

    /**
     * Appelé quand jeu est en pause (Alt+Tab, etc)
     */
    @Override
    public void pause() {
        // Pause logique jeu si besoin
    }

    /**
     * Appelé quand jeu reprend après pause
     */
    @Override
    public void resume() {
        // Reprendre logique jeu
    }

    /**
     * Appelé quand écran n'est plus visible
     */
    @Override
    public void hide() {
        // Nettoyer si besoin
    }

    /**
     * Appelé quand écran est fermé
     * IMPORTANT: libérer ressources!
     */
    @Override
    public void dispose() {
        // Libérer les ressources GPU
        mazeRenderer.dispose();  // ShapeRenderer, etc.
    }
}
