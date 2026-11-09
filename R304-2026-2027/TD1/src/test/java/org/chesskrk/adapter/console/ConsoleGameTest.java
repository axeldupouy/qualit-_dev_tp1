package org.chesskrk.adapter.console;

import org.chesskrk.adapter.naive.NaiveOracle;
import org.chesskrk.adapter.tablebase.RetrogradeTablebase;
import org.chesskrk.application.port.in.ChooseMoveUseCase;
import org.chesskrk.application.usecase.ChooseBestMoveService;
import org.chesskrk.domain.Color;
import org.chesskrk.domain.Move;
import org.chesskrk.domain.Position;
import org.chesskrk.domain.Rules;
import org.chesskrk.domain.Square;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleGameTest {

    private static Position depart() {
        return Position.of(Square.parse("e1"), Square.parse("a1"), Square.parse("e5"), Color.WHITE);
    }

    @Test
    void refuseUnCoupIllegalPuisAccepteUnCoupLegal() {
        Deque<String> saisies = new ArrayDeque<>(List.of("zz", "e8", "q"));
        List<String> sorties = new ArrayList<>();
        ChooseMoveUseCase moteurImmobile = p -> Optional.of(new Move(p.whiteKing(), Square.parse("d1")));
        ConsoleGame partie = new ConsoleGame(moteurImmobile, depart(), saisies::poll, sorties::add);

        partie.play();

        assertTrue(sorties.stream().anyMatch(l -> l.startsWith("Tapez une case")));
        assertTrue(sorties.stream().anyMatch(l -> l.startsWith("Coup illégal")));
        assertTrue(sorties.stream().anyMatch(l -> l.startsWith("Partie abandonnée")));
    }

    @Test
    void avecLaTableLeMoteurMateUnRoiNoirQuiFuitAuHasard() {
        ChooseMoveUseCase moteur = new ChooseBestMoveService(RetrogradeTablebase.build());
        List<String> sorties = new ArrayList<>();
        // Le roi noir joue toujours le premier coup légal : un adversaire faible mais régulier.
        ConsoleGame[] partie = new ConsoleGame[1];
        partie[0] = new ConsoleGame(moteur, depart(), premierCoupLegal(partie), sorties::add);

        partie[0].play();

        assertTrue(sorties.stream().anyMatch(l -> l.startsWith("Échec et mat")), String.join("\n", sorties));
    }

    @Test
    void avecLEvaluateurNaifLaPartieEstNulleAuBoutDeCinquanteCoups() {
        ChooseMoveUseCase moteur = new ChooseBestMoveService(new NaiveOracle());
        List<String> sorties = new ArrayList<>();
        ConsoleGame[] partie = new ConsoleGame[1];
        partie[0] = new ConsoleGame(moteur, depart(), premierCoupLegal(partie), sorties::add);

        partie[0].play();

        assertTrue(sorties.stream().anyMatch(l -> l.startsWith("Cinquante coups") || l.startsWith("Pat")),
                String.join("\n", sorties));
    }

    @Test
    void lEchiquierPlaceLesPiecesSurLeursCases() {
        ConsoleGame partie = new ConsoleGame(p -> Optional.empty(), depart(), () -> "q", s -> { });
        String[] lignes = partie.render().split("\n");
        assertEquals("1 R . . . K . . . ", lignes[7]);
        assertEquals("5 . . . . k . . . ", lignes[3]);
    }

    /** Une saisie qui joue toujours le premier coup légal du roi noir dans la partie donnée. */
    private static java.util.function.Supplier<String> premierCoupLegal(ConsoleGame[] partie) {
        return () -> {
            Position p = partie[0].position();
            return Rules.legalMoves(p).get(0).to().algebraic();
        };
    }
}
