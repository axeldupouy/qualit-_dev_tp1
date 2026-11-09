package org.chesskrk.infrastructure;

import org.chesskrk.adapter.console.ConsoleGame;
import org.chesskrk.adapter.naive.NaiveOracle;
import org.chesskrk.adapter.uci.UciSession;
import org.chesskrk.application.port.in.ChooseMoveUseCase;
import org.chesskrk.application.port.out.EndgameOracle;
import org.chesskrk.application.usecase.ChooseBestMoveService;
import org.chesskrk.domain.Color;
import org.chesskrk.domain.Position;
import org.chesskrk.domain.Square;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Assemblage : crée les objets concrets et les relie.
 * Sans argument, une partie en console contre le moteur. Avec l’argument {@code uci}, le protocole UCI
 * pour une interface d’échecs.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        if (args.length > 0 && args[0].equals("uci")) {
            UciSession session = new UciSession(Main::buildUseCase, out::println);
            while (!session.isQuitRequested()) {
                session.handle(in.readLine());
            }
            return;
        }
        Position start = Position.of(Square.parse("e1"), Square.parse("a1"), Square.parse("e5"), Color.WHITE);
        ConsoleGame game = new ConsoleGame(buildUseCase(), start, () -> readLine(in), out::println);
        game.play();
    }

    private static ChooseMoveUseCase buildUseCase() {
        EndgameOracle oracle = new NaiveOracle();
        return new ChooseBestMoveService(oracle);
    }

    private static String readLine(BufferedReader in) {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
