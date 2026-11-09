package org.chesskrk.adapter.naive;

import org.chesskrk.application.port.out.Evaluation;
import org.chesskrk.domain.Color;
import org.chesskrk.domain.Position;
import org.chesskrk.domain.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NaiveOracleTest {
    private final NaiveOracle oracle = new NaiveOracle();

    private static Position noirsAuTrait(String roiBlanc, String tour, String roiNoir) {
        return Position.of(Square.parse(roiBlanc), Square.parse(tour), Square.parse(roiNoir), Color.BLACK);
    }

    @Test
    void reconnaitLeMatDejaSurLEchiquier() {
        assertEquals(Evaluation.loss(0), oracle.evaluate(noirsAuTrait("b6", "h8", "a8")));
    }

    @Test
    void croitPerdueUnePositionOuSaTourEstEnPrise() {
        assertEquals(Evaluation.win(50), oracle.evaluate(noirsAuTrait("e1", "d5", "e5")));
    }

    @Test
    void aimeDonnerEchecQuandLaTourEstProtegee() {
        assertEquals(Evaluation.loss(2), oracle.evaluate(noirsAuTrait("d4", "e4", "e6")));
    }

    @Test
    void trouveNulleToutePositionOrdinaire() {
        assertEquals(Evaluation.DRAW, oracle.evaluate(noirsAuTrait("b6", "h6", "a8")));
    }
}
