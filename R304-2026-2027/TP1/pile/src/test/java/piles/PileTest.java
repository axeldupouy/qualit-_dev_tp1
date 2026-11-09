package piles;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PileTest {
    @Test void consulterNeRetirePas() {
        Pile p = new Pile(2);
        p.empiler(5);
        assertEquals(5, p.getSommet());
        assertEquals(5, p.getSommet());
        assertFalse(p.estVide());
    }
    // À ajouter : un test de l'ordre de dépilement et des états intermédiaires.
}
