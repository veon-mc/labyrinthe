package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Images {

    private Images() {}

    private static final Map<String, Image> CACHE = new HashMap<>();

    /** Charge si existe, sinon retourne null (pas d'exception). */
    private static Image loadOrNull(String path) {
        return CACHE.computeIfAbsent(path, p -> {
            InputStream is = Images.class.getResourceAsStream(p);
            return (is == null) ? null : new Image(is);
        });
    }

    /** Charge et throw si introuvable. */
    private static Image loadOrThrow(String path) {
        Image img = loadOrNull(path);
        if (img == null) throw new IllegalStateException("Image introuvable: " + path);
        return img;
    }

    public static Image baseTile(Tile tile, Position pos) {
        int r = pos.getRow(), c = pos.getCol();

        if (r == 0 && c == 0) return loadOrThrow("/images/Corners/fixed_tile_upleft_corner.jpg");
        if (r == 0 && c == 6) return loadOrThrow("/images/Corners/fixed_tile_upright_corner.jpg");
        if (r == 6 && c == 0) return loadOrThrow("/images/Corners/fixed_tile_downleft_corner.jpg");
        if (r == 6 && c == 6) return loadOrThrow("/images/Corners/fixed_tile_downright_corner.jpg");

        if (tile.getObjective() != null) {
            return objectiveImage(tile.getObjective());
        }

        return switch (tile.getType()) {
            case I -> loadOrThrow("/images/I_tiles/I_Shape.jpg");
            case L -> loadOrThrow("/images/L_tiles/L_tile.jpg");
            case T -> throw new IllegalStateException("Tuile T sans objectif");
        };
    }

    public static Image hudObjective(Objective obj) {
        return objectiveImage(obj);
    }

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


    private static String mapObjectiveKey(String key) {
        return switch (key) {
            // mobiles (racine /images)
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

    public static int fixedGoalOffsetDegrees(Objective obj) {
        if (obj == null) return 0;

        String k = norm(obj.getName());
        k = mapObjectiveKey(k);

        return switch (k) {
            case "book" -> 0;
            case "candleholder" -> 0;
            case "coffre" -> 0;
            case "crown" -> 90;
            case "helmet" -> 180;
            case "keys" -> 180;
            case "map" -> 90;
            case "money" -> 0;
            case "ring" -> 270;
            case "saphir" -> 270;
            case "skull" -> 90;
            case "sword" -> 90;
            default -> 0;
        };
    }

    public static Image spareTileImage(Tile tile) {
        return switch (tile.getType()) {
            case I -> loadOrThrow("/images/I_tiles/I_Shape.jpg");
            case L -> loadOrThrow("/images/L_tiles/L_tile.jpg");
            case T -> {

                yield loadOrThrow("/images/L_tiles/L_tile.jpg");
            }
        };
    }

    private static String norm(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }
}