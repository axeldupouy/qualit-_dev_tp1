package org.chesskrk.adapter.console;

import org.chesskrk.application.port.in.ChooseMoveUseCase;
import org.chesskrk.domain.Color;
import org.chesskrk.domain.GameStatus;
import org.chesskrk.domain.Move;
import org.chesskrk.domain.Position;
import org.chesskrk.domain.Rules;
import org.chesskrk.domain.Square;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Adaptateur d’entrée : une partie en console. Le moteur joue les blancs (roi et tour),
 * la personne au clavier joue le roi noir en tapant la case d’arrivée, par exemple {@code d4}.
 * La partie est nulle si la tour est prise, si le roi noir est pat, ou après 50 coups de chaque camp.
 * Les lignes lues et écrites passent par un {@link Supplier} et un {@link Consumer} : la classe
 * ne dépend pas de la console réelle et se teste avec des entrées préparées.
 */
public final class ConsoleGame {

    /** Cinquante coups de chaque camp, comptés en demi-coups. */
    public static final int MAX_PLIES = 100;

    private final ChooseMoveUseCase engine;
    private final Supplier<String> input;
    private final Consumer<String> output;

    private Position position;
    private int plies;

    public ConsoleGame(ChooseMoveUseCase engine, Position start, Supplier<String> input, Consumer<String> output) {
        if (start.sideToMove() != Color.WHITE) {
            throw new IllegalArgumentException("La partie commence avec les blancs au trait");
        }
        this.engine = engine;
        this.position = start;
        this.input = input;
        this.output = output;
    }

    /** Joue la partie jusqu’à son terme ou jusqu’à ce que la personne tape {@code q}. */
    public void play() {
        output.accept("Vous jouez le roi noir. Tapez la case d’arrivée de votre roi (par exemple d4), ou q pour quitter.");
        output.accept("Le moteur joue les blancs. Tenez 50 coups et la partie est nulle.");
        while (true) {
            if (!whiteMoves()) {
                return;
            }
            if (!blackMoves()) {
                return;
            }
        }
    }

    public Position position() {
        return position;
    }

    /** Le moteur joue ; renvoie faux si la partie est terminée. */
    private boolean whiteMoves() {
        Optional<Move> chosen = engine.chooseMove(position);
        if (chosen.isEmpty()) {
            output.accept(render());
            output.accept("Les blancs n’ont aucun coup : nulle.");
            return false;
        }
        Move move = chosen.get();
        position = Rules.apply(position, move);
        plies++;
        output.accept(render());
        output.accept("Coup " + moveNumber() + " des blancs : " + describe(move));
        return !announceEnd();
    }

    /** La personne joue le roi noir ; renvoie faux si la partie est terminée ou abandonnée. */
    private boolean blackMoves() {
        while (true) {
            output.accept("Votre roi (" + position.blackKing() + ") va en :");
            String line = input.get();
            if (line == null || line.trim().equalsIgnoreCase("q")) {
                output.accept("Partie abandonnée.");
                return false;
            }
            Square to;
            try {
                to = Square.parse(line.trim().toLowerCase());
            } catch (IllegalArgumentException e) {
                output.accept("Tapez une case comme d4, ou q.");
                continue;
            }
            Move move = new Move(position.blackKing(), to);
            if (position.blackKing().equals(to) || !Rules.isLegalMove(position, move)) {
                output.accept("Coup illégal : le roi avance d’une case et ne peut pas aller sur une case attaquée.");
                continue;
            }
            position = Rules.apply(position, move);
            plies++;
            return !announceEnd();
        }
    }

    /** Affiche le résultat si la partie est finie ; renvoie vrai dans ce cas. */
    private boolean announceEnd() {
        GameStatus status = Rules.status(position);
        switch (status) {
            case CHECKMATE -> {
                output.accept(render());
                output.accept("Échec et mat en " + moveNumber() + " coups : les blancs gagnent.");
                return true;
            }
            case STALEMATE -> {
                output.accept(render());
                output.accept("Pat : nulle.");
                return true;
            }
            case INSUFFICIENT_MATERIAL -> {
                output.accept(render());
                output.accept("La tour est prise : nulle.");
                return true;
            }
            default -> {
                if (plies >= MAX_PLIES) {
                    output.accept("Cinquante coups sans mat : nulle. Vous avez tenu.");
                    return true;
                }
                return false;
            }
        }
    }

    private int moveNumber() {
        return (plies + 1) / 2;
    }

    private String describe(Move move) {
        String piece = move.to().equals(position.whiteKing()) ? "roi" : "tour";
        return piece + " de " + move.from() + " en " + move.to();
    }

    /** L’échiquier, ligne 8 en haut, colonnes a à h. */
    String render() {
        StringBuilder sb = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            sb.append(rank + 1).append(' ');
            for (int file = 0; file < 8; file++) {
                Square sq = Square.of(file, rank);
                char c = '.';
                if (sq.equals(position.whiteKing())) {
                    c = 'K';
                } else if (sq.equals(position.blackKing())) {
                    c = 'k';
                } else if (position.hasRook() && sq.equals(position.rook())) {
                    c = 'R';
                }
                sb.append(c).append(' ');
            }
            sb.append('\n');
        }
        sb.append("  a b c d e f g h   (K roi blanc, R tour blanche, k roi noir)");
        return sb.toString();
    }
}
