package refuge;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class AnimauxTest {
    @Test void nomsConserves() {
        assertEquals("Rex", new Chien("Rex").getNom());
        assertEquals("Mina", new Chat("Mina").getNom());
    }
}
