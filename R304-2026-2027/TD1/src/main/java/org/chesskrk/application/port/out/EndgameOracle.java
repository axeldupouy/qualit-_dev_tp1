package org.chesskrk.application.port.out;

import org.chesskrk.domain.Position;

/** Contrat d’évaluation d’une position : dit si elle est gagnée, nulle ou perdue pour le camp au trait, et en combien de demi-coups. */
public interface EndgameOracle {
    Evaluation evaluate(Position position);
}
