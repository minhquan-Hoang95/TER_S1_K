
package com.pacman;

import com.badlogic.gdx.Game;
import com.pacman.game.screens.GameScreen;

/**
 * ===== CLASSE PRINCIPALE DU JEU =====
 *
 * Point d'entrée et contrôleur global du jeu Pac-Man
 *
 * Hiérarchie LibGDX:
 * ┌─────────────────────┐
 * │   PacmanGame        │ ← Point d'entrée (extends Game)
 * │  (Main Application) │
 * └──────────┬──────────┘
 *            │
 *            └─→ ┌──────────────────┐
 *                │  GameScreen      │ ← Écran principal du jeu
 *                │  (Screen impl)   │
 *                └──────────┬───────┘
 *                           │
 *                ┌──────────┴──────────┐
 *                │                     │
 *        ┌──────▼────────┐    ┌─────▼─────────┐
 *        │ MazeRenderer  │    │ MazeApiClient │
 *        │ (Affichage)   │    │ (HTTP/Data)   │
 *        └───────────────┘    └───────────────┘
 *
 *
 * Cycle de vie LibGDX:
 *
 *   JVM lancée
 *       │
 *       ├─→ LibGDX initialise
 *       │
 *       ├─→ PacmanGame.create() appelé UNE FOIS
 *       │   └─→ crée GameScreen et le définit actif
 *       │
 *       ├─→ GameScreen.show() appelé
 *       │
 *       └─→ Boucle infinie:
 *           ├─→ GameScreen.render(delta) appelé ~60 fois/sec
 *           ├─→ GameScreen.resize() si redimensionnement fenêtre
 *           ├─→ GameScreen.pause() si Alt+Tab
 *           └─→ ... (60 FPS)
 *
 *           Quand fermeture:
 *           ├─→ GameScreen.hide()
 *           ├─→ GameScreen.dispose()
 *           └─→ JVM termine
 *
 *
 * Responsabilités de PacmanGame:
 * 1. Initialiser le jeu au démarrage
 * 2. Créer l'écran principal
 * 3. Gérer les changements d'écrans (menus, game over, etc)
 * 4. Nettoyer avant fermeture
 *
 * Note: La plupart de la logique est dans GameScreen
 *       PacmanGame = juste orchestration haut-niveau
 */
public class PacmanGame extends Game {

    /**
     * ===== CRÉATION DU JEU =====
     *
     * Appelé UNE SEULE FOIS au démarrage du jeu
     * AVANT d'afficher la première frame
     *
     * Point d'entrée principal
     *
     * C'est ici qu'on:
     * 1. Crée les ressources initiales
     * 2. Initialise les écrans
     * 3. Lance le jeu
     *
     * IMPORTANT:
     * - Ne pas faire d'opérations longues ici!
     * - Éviter les allocations excessives
     * - Les ressources GPU doivent être créées ici (context graphique actif)
     */
    @Override
    public void create() {
        // Créer l'écran de jeu (contient GameScreen + tous ses composants)
        GameScreen gameScreen = new GameScreen();

        // Définir cet écran comme écran ACTIF
        // LibGDX va maintenant appeler render(), resize(), etc. sur cet écran
        this.setScreen(gameScreen);

        // Logs de démarrage (optionnel mais utile)
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║  🎮 Pac-Man Game Started 🎮    ║");
        System.out.println("║  LibGDX Version 1.x            ║");
        System.out.println("║  Screen: 800x600               ║");
        System.out.println("╚════════════════════════════════╝");
    }

    /**
     * ===== CLEANUP / NETTOYAGE =====
     *
     * Appelé quand le jeu se ferme
     * Avant la fermeture complète de l'application
     *
     * Responsabilités:
     * 1. Libérer les ressources GPU (textures, buffers, etc)
     * 2. Fermer les connexions (fichiers, HTTP)
     * 3. Sauvegarder l'état du jeu si besoin
     * 4. Faire du nettoyage général
     *
     * ⚠️ IMPORTANT: Ne pas appeler de code qui dépend du contexte graphique!
     * Le contexte est sur le point d'être fermé
     *
     * Résultat si on ne le fait pas:
     * - Fuite mémoire GPU
     * - Ressources non libérées
     * - Application peut crash
     */
    @Override
    public void dispose() {
        // Obtenir l'écran actuel
        // (il peut y avoir plusieurs écrans dans un jeu complet)
        if (this.getScreen() != null) {
            // Appeler dispose() sur l'écran actif
            // (qui lui-même va appeler dispose() sur ses composants)
            this.getScreen().dispose();
        }

        // Logs de fermeture (optionnel)
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║  Game Closed                   ║");
        System.out.println("║  Resources cleaned             ║");
        System.out.println("╚════════════════════════════════╝");
    }

    // ===== METHODS OPTIONNELLES (avancé) =====

    /**
     * Optionnel: Changer d'écran à la volée
     *
     * Usage: game.setActiveScreen(new MenuScreen());
     *
     * Cas d'usage:
     * - Écran de menu → Écran de jeu
     * - Écran de jeu → Écran de pause
     * - Écran de jeu → Écran de game over
     * - Etc.
     *
     * @param screen le nouvel écran à afficher
     */
    public void switchScreen(com.badlogic.gdx.Screen screen) {
        // Nettoyer l'écran précédent
        if (this.getScreen() != null) {
            this.getScreen().dispose();
        }

        // Définir le nouvel écran
        this.setScreen(screen);
    }

    /**
     * Optionnel: Obtenir la taille de la fenêtre
     * Utile pour adapter le rendu selon la résolution
     *
     * @return hauteur en pixels
     */
    public int getGameHeight() {
        return 600;  // Hardcodé pour cette démo
    }

    /**
     * Optionnel: Obtenir la largeur de la fenêtre
     *
     * @return largeur en pixels
     */
    public int getGameWidth() {
        return 800;  // Hardcodé pour cette démo
    }
}
