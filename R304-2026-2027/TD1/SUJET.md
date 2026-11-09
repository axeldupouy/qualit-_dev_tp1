# TD1 — Un moteur d’échecs KRK qui joue mal, puis bien

R304 FI — 90 minutes — groupes de 3 à 5. Cette séance précède le premier cours. Aucune connaissance de Clean Architecture ni des échecs n’est supposée. Un portable par groupe suffit. Pas d’IA pendant le TD.

**Comment lire ce sujet.** La partie « Contexte » se lit une fois. Ensuite, chaque tranche horaire donne une liste numérotée d’**Actions** : une action par ligne, le verbe en gras. « **Sur la feuille :** » dit ce que vous écrivez sur la feuille de groupe (papier ou fichier). « **Résultat attendu :** » dit ce que vous devez voir. Rien d’autre n’est à rendre.

## Contexte, à lire une fois

### Le projet des TD

Pendant les TD, votre groupe va écrire et surtout concevoir un petit **moteur d’échecs** pour la finale **KRK**, *King, Rook, King* : un roi et une tour blancs contre un roi noir. Un moteur d’échecs, à partir d’une position, liste les coups permis, évalue la position obtenue après chacun, joue le meilleur, et échange les coups avec l’extérieur : il reçoit le coup de l’adversaire et annonce le sien, par une console ou par un logiciel qui affiche l’échiquier.

Aujourd’hui, vous recevez un moteur complet qui fonctionne et qui joue mal. Vous jouez contre lui, vous comprenez comment il est organisé, puis vous le rendez imbattable en changeant **une ligne** (et son import). Aucune classe ni aucun test à écrire.

Au TD suivant, le code fourni aura un défaut, et ce sera à vous de le trouver, de le corriger et de prouver qu’il a disparu. Les séances suivantes construisent le moteur étape par étape ; ce déroulé est prévisionnel.

### La finale roi et tour contre roi

Les lettres indiquent les colonnes, les chiffres les lignes : b6 est la colonne b, ligne 6. ♔ est le roi blanc, ♖ la tour blanche et ♚ le roi noir ; dans la console du programme, ces pièces s’écrivent `K`, `R` et `k`. Un camp est **au trait** quand c’est à lui de jouer.

Trois règles suffisent aujourd’hui :

- la **tour** attaque toutes les cases de sa ligne et de sa colonne, sans traverser une pièce ;
- le **roi** se déplace d’une case dans toutes les directions ; il attaque donc ses cases voisines, et il ne peut pas aller sur une case attaquée par l’adversaire (on dit aussi contrôlée) ;
- un roi attaqué est en **échec** ; un roi en échec est **mat** s’il n’a aucune case sûre, c’est-à-dire aucune case voisine libre et non attaquée, et pas de tour voisine qu’il puisse prendre sans danger. La partie est alors perdue pour son camp.

**Le départ de vos parties, les blancs au trait** : roi blanc e1, tour a1, roi noir e5.

| | a | b | c | d | e | f | g | h |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 8 | | | | | | | | |
| 7 | | | | | | | | |
| 6 | | | | | | | | |
| 5 | | | | | ♚ | | | |
| 4 | | | | | | | | |
| 3 | | | | | | | | |
| 2 | | | | | | | | |
| 1 | ♖ | | | | ♔ | | | |

**Un exemple de mat, les noirs au trait** : le roi noir est attaqué par la tour le long de la ligne 8 ; a7 et b7 sont attaquées par le roi blanc, b8 par la tour ; aucune case sûre.

| | a | b | c | d | e | f | g | h |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 8 | ♚ | | | | | | | ♖ |
| 7 | | | | | | | | |
| 6 | | ♔ | | | | | | |
| 5 | | | | | | | | |
| 4 | | | | | | | | |
| 3 | | | | | | | | |
| 2 | | | | | | | | |
| 1 | | | | | | | | |

Avec un roi et une tour contre un roi seul, les blancs peuvent toujours forcer le mat, en seize coups au plus s’ils jouent parfaitement. Sinon la partie est **nulle** : quand la tour est prise, quand le roi noir, sans être en échec, n’a plus aucun coup permis (c’est le **pat**), ou après cinquante coups de chaque camp sans mat.

### Les responsabilités du moteur, et ses couches

Une **responsabilité**, c’est ce dont une partie du programme s’occupe, et qui a sa propre raison de changer. On la nomme par un verbe.

| Responsabilité | Ce qu’elle sait | Où, dans le code |
|---|---|---|
| **Connaître les règles** : cases, positions, coups permis, échec, mat, pat. | Ce qui est légal. | `org.chesskrk.domain` : `Square`, `Position`, `Move`, `Rules`, `GameStatus`, `Color` |
| **Choisir un coup** : parmi les coups permis, prendre celui qui mène à la meilleure position. | Comment décider, à condition qu’on lui dise ce que vaut une position. | `org.chesskrk.application` : `ChooseBestMoveService` ; `ChooseMoveUseCase`, contrat de choix d’un coup ; `EndgameOracle`, contrat d’évaluation d’une position, et `Evaluation`, la réponse de l’évaluateur |
| **Évaluer une position** : dire si elle est gagnée, nulle ou perdue, et en combien de coups. | La valeur des positions, par une méthode ou une autre. | `org.chesskrk.adapter.naive` : `NaiveOracle` ; `org.chesskrk.adapter.tablebase` : `RetrogradeTablebase` |
| **Échanger les coups** : recevoir le coup de l’adversaire, annoncer le sien, afficher l’échiquier. | Comment les coups entrent et sortent : une console, ou le protocole UCI d’un logiciel d’échecs. | `org.chesskrk.adapter.console` : `ConsoleGame` ; `org.chesskrk.adapter.uci` : `UciSession`, `FenCodec`, `UciMoveCodec` |
| **Assembler** : créer les objets et les relier au démarrage. | Quels objets concrets composent le programme. | `org.chesskrk.infrastructure` : `Main` |

Ces responsabilités sont rangées en **couches**, du centre vers l’extérieur : le **domaine** (les règles), l’**application** (le choix, le contrat de choix d’un coup qu’il offre et le contrat d’évaluation d’une position qu’il exige), les **adaptateurs** (évaluer, échanger les coups), l’**infrastructure** (assembler). C’est le découpage appelé **Clean Architecture** ; vous le retrouverez en cours. Sa règle unique : **une couche ne connaît que les couches plus au centre qu’elle**. Les règles ne connaissent personne ; le choix connaît les règles, mais pas la console ni la table ; seul l’assemblage connaît tout le monde.

Entre deux responsabilités passe une **frontière** ; une seule nous occupe aujourd’hui, celle entre la responsabilité de choisir un coup et celle d’évaluer une position. Le reste du moteur, UCI compris, est là pour plus tard. Les pièces, roi et tour, sont supposées ne jamais changer : elles vivent dans le domaine.

## 0–20 min — Ouvrir le projet et jouer un premier coup

Ouvrez le projet sur un portable du groupe pendant la présentation de l’enseignant ; les autres écoutent. Alternez ensuite les rôles de lecteur, pilote du clavier et rédacteur.

**Actions**

1. **Clonez** le dépôt dans IntelliJ IDEA, sauf si vous l’avez déjà fait en TP1 : **Get from VCS** (ou **Clone Repository** selon la version) sur l’écran d’accueil, URL `https://github.com/mikalziane/R304-2026-2027.git`, dans votre dossier personnel ou, sur un poste IUT, dans votre espace `Z:`.
2. **Mettez à jour** le clone s’il existait déjà : **File > Open** sur son dossier, puis **Git > Update Project** (`Ctrl+T`). **Résultat attendu :** `TD1/src/main/java/org/chesskrk` contient `domain`, `application`, `adapter` et `infrastructure` ; sinon, appelez l’enseignant.
3. **Ouvrez** le projet de la séance : **File > Open**, dossier `TD1`, fichier `pom.xml`, puis **Open as Project** et **This Window**. Maven, l’outil qui compile le projet et télécharge ses bibliothèques, se met au travail ; Internet est nécessaire la première fois. **Résultat attendu :** la barre de progression en bas de la fenêtre a disparu.
4. **Choisissez** le JDK : **File > Project Structure > Project**, **SDK** : un **JDK 21** ou plus récent ; s’il n’y en a pas, **Add SDK > Download JDK**.
5. **Lancez** `src/main/java/org/chesskrk/infrastructure/Main.java` avec le triangle vert à côté de `main`. **Résultat attendu :** dans la fenêtre **Run**, deux lignes de consigne, un échiquier en lettres, le coup du moteur, puis `Votre roi (e5) va en :`.
6. **Jouez** un coup : tapez la case d’arrivée de votre roi, par exemple `d4`, puis **Entrée**. **Résultat attendu :** le moteur rejoue aussitôt. Un coup illégal est refusé ; `q` abandonne ; pour rejouer, relancez `Main`.

**Terminal, uniquement en secours.** Un terminal est la fenêtre dans laquelle on tape des commandes.

1. **Ouvrez** le terminal d’IntelliJ IDEA : **View > Tool Windows > Terminal** (`Alt+F12` sous Windows).
2. **Vérifiez** que l’invite se termine par `TD1`, le dossier qui contient `pom.xml` et `mvnw.cmd` ; si elle se termine par `R304-2026-2027`, tapez `cd TD1` puis **Entrée** ; sinon, appelez l’enseignant.
3. **Tapez** les trois lignes ci-dessous, **Entrée** après chacune ; la première évite que les accents s’affichent mal.

   ```powershell
   chcp 65001
   .\mvnw.cmd -q compile
   java -cp target/classes org.chesskrk.infrastructure.Main
   ```

Si le démarrage bloque, signalez-le et passez à la lecture : les fichiers se lisent aussi dans un navigateur, à `https://github.com/mikalziane/R304-2026-2027/tree/main/TD1/src`, et l’enseignant projette une partie.

## 20–35 min — Jouer et observer

Le moteur part toujours de la position roi blanc e1, tour a1, roi noir e5, les blancs au trait.

**Actions**

1. **Jouez** deux ou trois parties, en changeant de pilote. **Défi : tenez cinquante coups sans vous faire mater.**
2. **Observez** le moteur : que fait sa tour ? donne-t-il des échecs ? laisse-t-il sa tour en prise ? vous menace-t-il un jour sérieusement de mat ?

**Sur la feuille :** vos quatre observations, puis votre réponse à cette question : ce qui manque au moteur, est-ce la connaissance des règles (joue-t-il un coup interdit ?) ou le jugement sur les positions ? Justifiez par ce que vous avez vu.

## 35–55 min — Les classes par couche et le trajet d’un coup

Tous les chemins sont relatifs à `TD1/src/main/java/org/chesskrk`. Les fichiers `package-info.java` décrivent chaque dossier en une phrase.

**Repères de lecture.** Le **type déclaré** d’un champ est le type écrit devant son nom dans sa déclaration. Une **interface** se reconnaît au mot-clé `interface` : des méthodes promises, sans leur code. `Optional<Move>` se lit « un coup, ou rien s’il n’y a aucun coup permis ». L’écriture `stream()…min(...)` parcourt une liste et garde le plus petit score ; inutile de la détailler. Un **demi-coup** est le coup d’un seul camp. L’évaluateur répond du point de vue du camp au trait ; après un coup des blancs, ce sont les noirs : ce qui est perdu pour eux est bon pour le moteur, et les nombres 2 et 50 sont des ordres de grandeur, pas un vrai compte de demi-coups.

**Actions**

1. **Dépliez**, dans le panneau **Project** d’IntelliJ, les quatre dossiers `domain`, `application`, `adapter` et `infrastructure`. **Sur la feuille :** quatre cercles emboîtés, domaine au centre, puis application, adaptateurs, infrastructure, et chaque classe dans son cercle, à l’aide du tableau des responsabilités.
2. **Ouvrez** `adapter/console/ConsoleGame.java` et **repérez**, dans la méthode `whiteMoves` de ce fichier, la ligne `engine.chooseMove(position)`, où la console demande un coup au moteur. **Sur la feuille :** le type déclaré du champ `engine` ; est-ce une classe ou une interface ?
3. **Ouvrez** `application/usecase/ChooseBestMoveService.java` et **suivez** `chooseMove` pour un coup : `Rules.legalMoves` donne les coups permis, `Rules.apply` calcule la position après chaque coup, `oracle.evaluate(...)` demande la valeur de cette position, et `min` garde le meilleur. **Sur la feuille :** quelle responsabilité ce fichier exerce ; laquelle il délègue, et à qui.
4. **Ouvrez** `application/port/out/EndgameOracle.java` en entier. C’est une interface Java, c’est-à-dire un contrat, ici le **contrat d’évaluation d’une position** : une méthode `evaluate` qui reçoit une position et rend une `Evaluation`, gain, nulle ou perte pour le camp au trait, et nombre de demi-coups jusqu’au mat. **Sur la feuille :** qui a besoin de ce contrat ; qui le tient.
5. **Ouvrez** `adapter/naive/NaiveOracle.java` et **lisez** `evaluate` ; le mot `implements` signifie que cette classe tient le contrat d’évaluation d’une position. **Sur la feuille :** ses trois règles de débutant ; ce qu’elle répond pour toute autre position ; le lien avec ce que vous avez observé en jouant.
6. **Écrivez** le trajet d’un coup en quatre lignes, un fichier par ligne, de la console à l’évaluateur naïf.
7. **Faites expliquer** ce trajet au groupe par une autre personne que le pilote.

**Indice de lecture volontaire :** une variable dont le type déclaré est une interface peut désigner un objet de n’importe quelle classe qui l’implémente, comme l’exemple `Tarif` et `TarifFixe` de la présentation de l’enseignant. Ce qui est écrit dans le fichier, le type déclaré, et ce qui s’exécute, la classe de l’objet, sont deux choses différentes.

## 55–70 min — Le graphe de dépendances, et l’impact d’un changement

Une flèche de dépendance va de la classe ou interface qui **utilise un type** vers ce **type utilisé** : type d’un champ, d’un paramètre, d’une variable ou d’un résultat, objet créé avec `new`, ou interface implémentée. On regarde les noms écrits dans le code, pas l’ordre des appels à l’exécution.

**Actions**

1. **Recopiez** ces cinq noms et **tracez** les flèches de dépendance entre eux, en cherchant chaque flèche dans les déclarations Java ; vous pouvez ignorer les flèches vers le domaine, que tout le monde utilise. **Sur la feuille :** pour chaque flèche, la déclaration qui la justifie.

   ```text
   [ ConsoleGame ]   [ ChooseBestMoveService ]   [ EndgameOracle ]

                     [ NaiveOracle ]   [ RetrogradeTablebase ]
   ```

2. **Comparez** `ChooseBestMoveService` et `NaiveOracle` : y a-t-il une flèche entre eux, dans un sens ou dans l’autre ? Et à l’exécution ? **Sur la feuille :** votre explication, avec l’indice de lecture.
3. **Estimez** l’impact d’un changement, d’après votre graphe. Le changement : on veut que le moteur évalue les positions avec `RetrogradeTablebase`, une table qui connaît la valeur exacte de toutes les positions KRK, au lieu de `NaiveOracle`. **Sur la feuille, avant de continuer :** quels fichiers devront être modifiés, lesquels resteront intacts, et `ChooseBestMoveService` doit-il changer ?
4. **Ouvrez** `infrastructure/Main.java`, une fois l’estimation écrite, et **ajoutez-le** au graphe avec ses flèches. **Sur la feuille :** ce que vous remarquez ; votre estimation tient-elle toujours ?

> **Indice volontaire : comment estimer l’impact d’un changement.** Ce n’est pas si simple, et c’est la compétence de tout le parcours.
>
> 1. Nommez précisément ce qui change, et ce qui le remplace.
> 2. Dans le graphe, cherchez toutes les flèches qui **arrivent** sur ce qui change. Chaque fichier d’où part une telle flèche écrit le nom de ce qui change : il devra être relu, et sans doute modifié.
> 3. Si ce qui change est un contrat, une interface, remontez encore : tout ce qui pointe vers ce contrat est touché à son tour. Si le contrat ne change pas, l’impact s’arrête là.
> 4. Ajoutez le fichier qui devra écrire le nom de ce qui remplace : quelqu’un doit créer le nouvel objet.
> 5. Ce qui n’a aucune flèche vers ce qui change reste intact, même si, à l’exécution, il l’appelle.

**Faites un point avec l’enseignant** avant de continuer, et changez de pilote. La section suivante est repliée : ne l’ouvrez qu’après ce point.

## 70–85 min — Faire le changement, vérifier l’impact

<details>
<summary>À ouvrir après le point avec l’enseignant</summary>

**Actions**

1. **Remplacez**, dans `infrastructure/Main.java`, méthode `buildUseCase`, la ligne

   ```java
   EndgameOracle oracle = new NaiveOracle();
   ```

   par

   ```java
   EndgameOracle oracle = RetrogradeTablebase.build();
   ```

2. **Importez** la classe : IntelliJ souligne `RetrogradeTablebase` en rouge ; curseur dessus, `Alt+Entrée`, **Import class**. L’import de `NaiveOracle` devient inutile et apparaît en gris ; laissez-le ou supprimez-le.
3. **Relancez** `Main` et **rejouez**. Le calcul de la table prend une seconde au démarrage.

**Résultat attendu :** depuis la position de départ, le moteur mate en quatorze coups au plus.

**Sur la feuille :**

1. combien de coups vous avez tenu cette fois, et si le moteur fait encore des échecs sans suite ;
2. combien de fichiers et combien de lignes ont changé, comparés à votre estimation, et si votre graphe a changé ;
3. quel objet reçoit maintenant l’appel `oracle.evaluate(...)`, et si le nom de sa classe apparaît dans `ChooseBestMoveService.java` ; montrez le code.

**Si vous avez le temps.** **Lancez** les tests : clic droit sur le dossier `src/test/java`, **Run 'All Tests'**. **Résultat attendu :** 41 tests réussis. Parmi eux, `CleanArchitectureTest` vérifie automatiquement la règle des couches : si une classe du choix nommait la console ou la table, ce test échouerait.

</details>

## 85–90 min — Bilan et préparation de la suite

**Sur la feuille**, en une ligne chacune :

1. ce qui a rendu le moteur imbattable : un changement des règles, du choix, de l’évaluation, ou de l’assemblage ;
2. pourquoi une seule ligne a suffi, avec les mots « contrat d’évaluation d’une position » et « interface » ;
3. quels fichiers changeraient si demain on écrivait une troisième façon d’évaluer.

**Conservez** la feuille de groupe, avec le trajet du coup et le graphe de dépendances : le TD2 repart de là. Chacun doit pouvoir expliquer une flèche, sans soutenance individuelle. Au TD suivant, le code fourni aura un défaut de ce genre, une classe qui connaît ce qu’elle ne devrait pas, et vous le ferez disparaître.

**Si le temps manque :** une seule partie, le trajet du coup et l’estimation de l’impact avec l’enseignant, puis la ligne de `Main`. Le bilan peut être discuté collectivement ; aucun travail à domicile n’est imposé.
