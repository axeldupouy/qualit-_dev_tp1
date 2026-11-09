package org.chesskrk.application.usecase;

import org.chesskrk.application.port.in.ChooseMoveUseCase;
import org.chesskrk.application.port.out.EndgameOracle;
import org.chesskrk.application.port.out.Evaluation;
import org.chesskrk.domain.Move;
import org.chesskrk.domain.Position;
import org.chesskrk.domain.Rules;

import java.util.Comparator;
import java.util.Optional;

/**
 * Choisir un coup : parmi les coups légaux, celui dont la position résultante est la meilleure
 * pour le camp qui joue, d’après l’évaluateur : le mat forcé le plus rapide d’abord, sinon la nulle,
 * sinon la défaite la plus lente. À égalité, l’ordre de génération des coups décide : le choix est déterministe.
 */
public final class ChooseBestMoveService implements ChooseMoveUseCase {

    private final EndgameOracle oracle;

    public ChooseBestMoveService(EndgameOracle oracle) {
        this.oracle = oracle;
    }

    @Override
    public Optional<Move> chooseMove(Position position) {
        return Rules.legalMoves(position).stream()
                .min(Comparator.comparingLong(move -> score(oracle.evaluate(Rules.apply(position, move)))));
    }

    /** Plus petit est meilleur pour le camp qui joue. L’évaluation est du point de vue de l’adversaire, qui sera au trait. */
    private static long score(Evaluation opponentView) {
        return switch (opponentView.result()) {
            case LOSS -> opponentView.pliesToMate();               // on mate : le plus tôt est le mieux
            case DRAW -> 1_000_000L;
            case WIN -> 2_000_000L - opponentView.pliesToMate();   // on se fait mater : le plus tard est le mieux
        };
    }
}
