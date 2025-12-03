package g64889.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectiveDeck {
    private List<ObjectiveCard> cards;


    public ObjectiveDeck(List<ObjectiveCard> initialCards) {
        if (initialCards == null || initialCards.isEmpty()) {
            throw new IllegalArgumentException("The deck is empty");
        }
        List<ObjectiveCard> copy = new ArrayList<>(initialCards);
        Collections.shuffle(copy);
        this.cards = new ArrayList<>(copy);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public ObjectiveCard draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.removeFirst();
    }
}
