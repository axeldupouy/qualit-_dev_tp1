# Origine du moteur du TD1

Le moteur provient de PuckSearchTeam/chessKRK, dossier After, commit ef5fc12d7999898ad3133f48ea608bec91072737, licence Apache 2.0 conservée dans LICENSE : domaine, cas d’utilisation, ports, table rétrograde, protocole UCI et test d’architecture.

Adaptation R304 du 8 novembre 2026 : deux adaptateurs ajoutés, `adapter.naive.NaiveOracle` (évaluation naïve, pour voir le moteur jouer mal) et `adapter.console.ConsoleGame` (partie en console contre le moteur) ; `infrastructure.Main` assemble la console et l’évaluateur naïf, et garde UCI derrière l’argument `uci` ; annotations jMolecules retirées des paquetages ; lanceur Maven 3.9.9 ; JDK 21 minimum.

Du 6 au 8 novembre 2026, le TD1 a utilisé un exemple de cinq fichiers (`krk.*`), conservé dans l’historique Git.
