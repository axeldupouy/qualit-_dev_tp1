package org.chesskrk.adapter.naive;

import org.chesskrk.application.port.out.EndgameOracle;
import org.chesskrk.application.port.out.Evaluation;
import org.chesskrk.domain.GameStatus;
import org.chesskrk.domain.Position;
import org.chesskrk.domain.Rules;

/**
 * Évaluateur naïf. Il tient le contrat d’évaluation d’une position avec trois règles de débutant,
 * du point de vue des noirs au trait : un mat sur l’échiquier est perdu pour eux ; une tour blanche
 * qu’ils peuvent prendre est, croit-il, gagnée pour eux ; un échec est un peu mauvais pour eux.
 * Tout le reste lui paraît nul. Il ne voit jamais plus loin que la position qu’on lui montre.
 * Les nombres 2 et 50 sont des ordres de grandeur, pas un vrai compte de demi-coups.
 */
public final class NaiveOracle implements EndgameOracle {
    @Override
    public Evaluation evaluate(Position position) {
        GameStatus status = Rules.status(position);
        if (status == GameStatus.CHECKMATE) {
            return Evaluation.loss(0);
        }
        if (position.hasRook() && Rules.isAttackedByBlack(position, position.rook())
                && !position.whiteKing().isAdjacentTo(position.rook())) {
            return Evaluation.win(50);
        }
        if (status == GameStatus.ONGOING && Rules.isInCheck(position)) {
            return Evaluation.loss(2);
        }
        return Evaluation.DRAW;
    }
}
