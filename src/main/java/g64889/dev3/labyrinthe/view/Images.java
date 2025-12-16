package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;

import g64889.dev3.labyrinthe.model.enumeration.TileType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class used to load and cache game images (tiles and objectives).
 * <p>
 * This class provides:
 * <ul>
 *   <li>a small cache to avoid reloading the same resource multiple times</li>
 *   <li>methods to resolve the correct image depending on tile type and objective</li>
 *   <li>mapping utilities between objective names and file names</li>
 *   <li>rotation corrections for some objectives to match expected orientation</li>
 * </ul>
 * Images are loaded from the classpath resources (e.g. {@code /images/...}).
 */
public final class Images {

    /** Utility class: no instances allowed. */
    private Images() {}

    /** Cache for already loaded images (key = resource path). */
    private static final Map<String, Image> CACHE = new HashMap<>();

    /**
     * Loads an image from a resource path if it exists, otherwise returns null.
     * <p>
     * The result is cached.
     *
     * @param path the resource path
     * @return the loaded image, or null if not found
     */
    private static Image loadOrNull(String path) {
        return CACHE.computeIfAbsent(path, p -> {
            InputStream is = Images.class.getResourceAsStream(p);
            return (is == null) ? null : new Image(is);
        });
    }

    /**
     * Loads an image from a resource path and throws an exception if not found.
     *
     * @param path the resource path
     * @return the loaded image
     * @throws IllegalStateException if the resource does not exist
     */
    private static Image loadOrThrow(String path) {
        Image img = loadOrNull(path);
        if (img == null) throw new IllegalStateException("Image introuvable: " + path);
        return img;
    }

    // =========================================================
    //      Separate maps for goal images on T tiles and L tiles
    // =========================================================

    /** Cache for goal images used on T tiles (loaded from /images/...). */
    private static final Map<String, Image> T_GOALS = new HashMap<>();

    /** Cache for goal images used on L tiles (loaded from /images/L_tiles/...). */
    private static final Map<String, Image> L_GOALS = new HashMap<>();

    static {
        // =========================
        //      T tiles (goal)
        // =========================
        T_GOALS.put("chauve-souris", loadOrThrow("/images/goal_bat.jpg"));
        T_GOALS.put("dragon", loadOrThrow("/images/goal_dragon.jpg"));
        T_GOALS.put("fantôme", loadOrThrow("/images/goal_ghost.jpg"));
        T_GOALS.put("génie", loadOrThrow("/images/goal_ghost2.jpg"));
        T_GOALS.put("gnome", loadOrThrow("/images/goal_pig.jpg"));
        T_GOALS.put("fée", loadOrThrow("/images/goal_witch.jpg"));

        // =========================
        //      L tiles (goal)
        // =========================
        L_GOALS.put("papillon", loadOrThrow("/images/goal_butteryfly.jpg"));
        L_GOALS.put("hibou", loadOrThrow("/images/goal_hibou.jpg"));
        L_GOALS.put("scarabée", loadOrThrow("/images/goal_insecte.jpg"));
        L_GOALS.put("lézard", loadOrThrow("/images/goal_lezard.jpg"));
        L_GOALS.put("rat", loadOrThrow("/images/goal_mouse.jpg"));
        L_GOALS.put("araignée", loadOrThrow("/images/goal_spider.jpg"));
    }

    /**
     * Returns the goal image for a given tile type, to avoid mismatching
     * T-goals and L-goals directories.
     * <p>
     * If the goal image is not found for the expected directory, this method
     * returns null so the caller can use a fallback strategy.
     *
     * @param type the tile type (typically T or L)
     * @param obj  the objective on the tile
     * @return the corresponding goal image, or null if not found
     */
    private static Image goalByType(TileType type, Objective obj) {
        if (obj == null) return null;

        String mapped = mapObjectiveKey(norm(obj.getName()));
        String file = "goal_" + mapped + ".jpg";

        if (type == TileType.T) {
            return T_GOALS.computeIfAbsent(mapped, k -> loadOrNull("/images/" + file));
        }

        if (type == TileType.L) {
            return L_GOALS.computeIfAbsent(mapped, k -> loadOrNull("/images/L_tiles/" + file));
        }

        return null;
    }

    /**
     * Returns the base image for a tile located at a given position.
     * <p>
     * Corners use dedicated fixed tile images. If a tile contains an objective,
     * a goal image is returned (with a type-based lookup to avoid mismatches).
     * Otherwise, a default image is returned based on tile type.
     *
     * @param tile the tile to render
     * @param pos  the position of the tile
     * @return the base image for the tile
     * @throws IllegalStateException if a required image is missing
     */
    public static Image baseTile(Tile tile, Position pos) {
        int r = pos.getRow(), c = pos.getCol();

        if (r == 0 && c == 0) return loadOrThrow("/images/Corners/fixed_tile_upleft_corner.jpg");
        if (r == 0 && c == 6) return loadOrThrow("/images/Corners/fixed_tile_upright_corner.jpg");
        if (r == 6 && c == 0) return loadOrThrow("/images/Corners/fixed_tile_downleft_corner.jpg");
        if (r == 6 && c == 6) return loadOrThrow("/images/Corners/fixed_tile_downright_corner.jpg");

        if (tile.getObjective() != null) {
            Image byType = goalByType(tile.getType(), tile.getObjective());
            if (byType != null) return byType;

            return objectiveImage(tile.getObjective());
        }

        return switch (tile.getType()) {
            case I -> loadOrThrow("/images/I_tiles/I_Shape.jpg");
            case L -> loadOrThrow("/images/L_tiles/L_tile.jpg");
            case T -> throw new IllegalStateException("Tuile T sans objectif");
        };
    }

    /**
     * Returns the objective image used in the HUD.
     *
     * @param obj the objective
     * @return the objective image, or null if obj is null
     */
    public static Image hudObjective(Objective obj) {
        return objectiveImage(obj);
    }

    /**
     * Loads the objective image by testing multiple directories.
     *
     * @param obj the objective
     * @return the corresponding image, or null if obj is null
     * @throws IllegalArgumentException if the image cannot be found
     */
    private static Image objectiveImage(Objective obj) {
        if (obj == null) return null;

        String key = norm(obj.getName());
        String fileKey = mapObjectiveKey(key);
        String file = "goal_" + fileKey + ".jpg";

        Image img = loadOrNull("/images/Fixed_tiles/" + file);
        if (img != null) return img;

        img = loadOrNull("/images/" + file);
        if (img != null) return img;

        img = loadOrNull("/images/L_tiles/" + file);
        if (img != null) return img;

        throw new IllegalArgumentException(
                "Image objectif introuvable pour : " + obj.getName()
                        + " (essayé Fixed_tiles/, /images/, L_tiles/ avec " + file + ")"
        );
    }

    /**
     * Maps a normalized objective name to a file key.
     * <p>
     * Example: "chauvesouris" becomes "bat".
     *
     * @param key normalized objective name
     * @return mapped key used in file names
     */
    private static String mapObjectiveKey(String key) {
        return switch (key) {
            case "fantome" -> "ghost";
            case "dragon" -> "dragon";
            case "chauvesouris" -> "bat";
            case "hibou" -> "hibou";
            case "scarabee" -> "insecte";
            case "rat" -> "mouse";
            case "araignee" -> "spider";
            case "papillon" -> "butteryfly";
            case "lezard" -> "lezard";

            case "gnome" -> "pig";
            case "fee" -> "witch";
            case "genie" -> "ghost2";

            case "grimoire" -> "book";
            case "boursedor" -> "money";
            case "carte" -> "map";
            case "couronne" -> "crown";
            case "clefs" -> "keys";
            case "ossements" -> "skull";
            case "bague" -> "ring";
            case "coffreautresor" -> "coffre";
            case "emeraude" -> "saphir";
            case "epee" -> "sword";
            case "chandelier" -> "candleholder";
            case "heaume" -> "helmet";

            default -> key;
        };
    }

    /** Rotation corrections for objective images (file key -> angle). */
    private static final Map<String, Double> CORRECTIONS = new HashMap<>();

    static {
        CORRECTIONS.put("goal_hibou", 270.0);
        CORRECTIONS.put("goal_helmet", 180.0);
        CORRECTIONS.put("goal_insecte", 270.0);
        CORRECTIONS.put("goal_crown", 90.0);
        CORRECTIONS.put("goal_coffre", 0.0);
        CORRECTIONS.put("goal_lezard", 90.0);
        CORRECTIONS.put("goal_candleholder", 0.0);
        CORRECTIONS.put("goal_butteryfly", 90.0);
        CORRECTIONS.put("goal_map", 90.0);
        CORRECTIONS.put("goal_ring", 270.0);
        CORRECTIONS.put("goal_skull", 90.0);
        CORRECTIONS.put("goal_sword", 90.0);
        CORRECTIONS.put("goal_money", 0.0);
        CORRECTIONS.put("goal_keys", 180.0);
        CORRECTIONS.put("goal_saphir", 270.0);
        CORRECTIONS.put("goal_book", 0.0);
        CORRECTIONS.put("goal_mouse", 180.0);
        CORRECTIONS.put("goal_spider", 90.0);

        double mobileTDefault = 180.0;
        CORRECTIONS.put("goal_pig", mobileTDefault);
        CORRECTIONS.put("goal_dragon", mobileTDefault);
        CORRECTIONS.put("goal_ghost", mobileTDefault);
        CORRECTIONS.put("goal_ghost2", mobileTDefault);
        CORRECTIONS.put("goal_witch", mobileTDefault);
        CORRECTIONS.put("goal_bat", mobileTDefault);
    }

    /**
     * Returns the rotation correction to apply for an objective image.
     *
     * @param obj the objective
     * @return angle in degrees (0.0 if no correction is needed)
     */
    public static double correctionDegrees(Objective obj) {
        if (obj == null) return 0.0;

        String key = norm(obj.getName());
        String mapped = mapObjectiveKey(key);
        String fileKey = "goal_" + mapped;

        return CORRECTIONS.getOrDefault(fileKey, 0.0);
    }

    /** Additional offsets for T goal images (file key -> angle). */
    private static final Map<String, Double> T_GOAL_OFFSETS = new HashMap<>();

    static {
        T_GOAL_OFFSETS.put("goal_bat", 180.0);
        T_GOAL_OFFSETS.put("goal_dragon", 180.0);
        T_GOAL_OFFSETS.put("goal_pig", 180.0);
        T_GOAL_OFFSETS.put("goal_witch", 180.0);
        T_GOAL_OFFSETS.put("goal_ghost", 180.0);
        T_GOAL_OFFSETS.put("goal_ghost2", 180.0);
    }

    /**
     * Returns a rotation offset for T goal images.
     *
     * @param obj the objective
     * @return angle in degrees (0.0 if no offset is defined)
     */
    public static double tGoalOffsetDegrees(Objective obj) {
        if (obj == null) return 0.0;
        String mapped = mapObjectiveKey(norm(obj.getName()));
        return T_GOAL_OFFSETS.getOrDefault("goal_" + mapped, 0.0);
    }

    /**
     * Returns the image for the spare tile (the tile outside the board).
     * <p>
     * If the tile has an objective, the corresponding goal image is loaded
     * and an objective-specific correction angle is applied.
     *
     * @param tile the spare tile
     * @return the image to display, or null if tile is null
     */
    public static Image spareTileImage(Tile tile) {
        if (tile == null) return null;

        // Si la tuile libre a un objectif → image de l'objectif
        if (tile.getObjective() != null) {
            return loadGoalForObjective(tile.getObjective());
        }

        // Sinon image standard selon le type
        return switch (tile.getType()) {
            case I -> loadOrThrow("/images/I_tiles/I_Shape.jpg");
            case L -> loadOrThrow("/images/L_tiles/L_tile.jpg");
            case T -> loadOrThrow("/images/T_tiles/T_tile.jpg");
        };
    }

    /**
     * Loads an image only if the resource exists (returns null otherwise).
     * The result is cached.
     *
     * @param path the resource path
     * @return the loaded image, or null if missing
     */
    private static Image loadIfExists(String path) {
        InputStream is = Images.class.getResourceAsStream(path);
        if (is == null) return null;
        return CACHE.computeIfAbsent(path, p -> new Image(is));
    }

    /**
     * Loads the goal image for an objective from a limited set of directories.
     *
     * @param obj the objective
     * @return the corresponding goal image
     * @throws IllegalArgumentException if no image can be found
     */
    private static Image loadGoalForObjective(Objective obj) {
        if (obj == null) return null;

        String key = norm(obj.getName());
        String mapped = mapObjectiveKey(key);

        Image img = loadIfExists("/images/Fixed_tiles/goal_" + mapped + ".jpg");
        if (img != null) return img;

        img = loadIfExists("/images/goal_" + mapped + ".jpg");
        if (img != null) return img;

        throw new IllegalArgumentException("Image objectif introuvable pour : " + obj.getName()
                + " (mapped=" + mapped + ")");
    }

    /**
     * Normalizes a string for file name matching:
     * <ul>
     *   <li>lowercase</li>
     *   <li>removes accents</li>
     *   <li>keeps only alphanumeric characters</li>
     * </ul>
     *
     * @param s the input string
     * @return a normalized key (never null)
     */
    private static String norm(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }
}