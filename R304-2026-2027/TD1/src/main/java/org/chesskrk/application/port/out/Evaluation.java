package org.chesskrk.application.port.out;

/**
 * La réponse de l’évaluateur : valeur d’une position du point de vue du camp au trait,
 * gagnée (WIN), nulle (DRAW) ou perdue (LOSS). {@code pliesToMate}, le nombre de demi-coups jusqu’au mat,
 * n’a de sens que pour WIN et LOSS (0 = déjà mat).
 */
public record Evaluation(Result result, int pliesToMate) {

    public enum Result { WIN, DRAW, LOSS }

    public static final Evaluation DRAW = new Evaluation(Result.DRAW, 0);

    public static Evaluation win(int plies) {
        return new Evaluation(Result.WIN, plies);
    }

    public static Evaluation loss(int plies) {
        return new Evaluation(Result.LOSS, plies);
    }

    public boolean isWin() {
        return result == Result.WIN;
    }

    public boolean isLoss() {
        return result == Result.LOSS;
    }

    public boolean isDraw() {
        return result == Result.DRAW;
    }
}
