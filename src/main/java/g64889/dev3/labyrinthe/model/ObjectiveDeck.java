package g64889.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of objective cards.
 * <p>
 * The deck is initialized with a list of {@link ObjectiveCard} objects,
 * shuffled at creation time. Cards can then be drawn one by one
 * until the deck becomes empty.
 */
public class ObjectiveDeck {

    /** List of objective cards contained in the deck. */
    private List<ObjectiveCard> cards;

    /**
     * Creates a new deck with the given initial cards.
     * <p>
     * The cards are copied and shuffled to ensure random order.
     *
     * @param initialCards the initial list of objective cards
     * @throws IllegalArgumentException if the list is null or empty
     */
    public ObjectiveDeck(List<ObjectiveCard> initialCards) {
        if (initialCards == null || initialCards.isEmpty()) {
            throw new IllegalArgumentException("The deck is empty");
        }

        List<ObjectiveCard> copy = new ArrayList<>(initialCards);
        Collections.shuffle(copy);
        this.cards = new ArrayList<>(copy);
    }

    /**
     * Indicates whether the deck is empty.
     *
     * @return true if there are no more cards in the deck
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Returns the number of remaining cards in the deck.
     *
     * @return the deck size
     */
    public int size() {
        return cards.size();
    }

    /**
     * Draws and removes the top card of the deck.
     *
     * @return the drawn objective card
     * @throws IllegalStateException if the deck is empty
     */
    public ObjectiveCard draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.removeFirst();
    }
}