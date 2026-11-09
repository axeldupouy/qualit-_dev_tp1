# R304 TP1 Encapsulation et changements

Mikal Ziane — BUT Informatique — 2026–2027

Durée : 3 heures. Public : FI et FA. Cette séance précède le premier cours.

Dans ce TP, vous allez modifier deux petits programmes Java pour faciliter des changements précis. À la fin, vous devrez pouvoir expliquer quelle partie du programme a changé, quels clients ont été protégés et comment vous avez vérifié que le comportement attendu est conservé.

Les exercices utilisent les sources du TP1 « pile » et « refuge ». Aucune préparation avant la séance n’est nécessaire. Utilisez un JDK 21 ou plus récent, Git et votre compte GitHub personnel. Maven charge JUnit et les autres dépendances lors de la première ouverture ou exécution, avec une connexion Internet. L’IA n’est pas utilisée pendant le TP.

## Démarrage 15 minutes

Sur un poste de l’IUT, créez votre dossier de travail dans votre espace personnel sur **Z:**. Clonez le dépôt dans ce dossier.

1. Dans **IntelliJ IDEA**, choisissez **Get from VCS** (ou **Clone Repository**), collez `https://github.com/mikalziane/R304-2026-2027.git`. Dans **Directory**, choisissez un emplacement dans votre dossier de travail sur Z: à l’IUT, puis cliquez sur **Clone**. Si vous avez déjà cloné le dépôt R304, réutilisez ce clone.
2. Dans IntelliJ IDEA, choisissez **File > Open**. Dans le dépôt cloné, ouvrez le dossier `TP1` et sélectionnez son fichier `pom.xml` (`R304-2026-2027/TP1/pom.xml`). Si IntelliJ vous le propose, cliquez sur **Open as Project**. Ce fichier indique à IntelliJ où trouver les exercices et les bibliothèques de test. Attendez la fin du chargement. Vous devez voir les deux exercices `pile` et `refuge`.
3. Dans **File > Project Structure > Project**, choisissez un **JDK 21** ou plus récent dans **SDK**.
4. Dans `pile/src/test/java/piles/PileTest.java` puis `refuge/src/test/java/refuge/AnimauxTest.java`, cliquez sur le triangle vert à côté du nom de la classe. Au total, deux tests doivent réussir.
5. Créez votre dépôt GitHub personnel **privé** `R304-travaux`, sans fork, puis reliez-y ce clone et effectuez le premier push en suivant [Créer votre dépôt personnel de TP](#créer-votre-dépôt-personnel-de-tp-une-seule-fois). Cette configuration ne se fait qu’une fois pour les TP de l’année. Le résultat attendu est la présence des fichiers du cours dans votre dépôt privé sur GitHub. Si la connexion ou la configuration bloque, signalez-le et commencez l’exercice pendant que l’enseignant vous aide.

**Si vous clonez en ligne de commande**, ouvrez PowerShell dans votre dossier de travail. Tapez chaque commande ci-dessous, puis appuyez sur **Entrée**. Si vous avez déjà les sources, ouvrez un terminal dans leur dossier `TP1` et passez aux tests.

```text
git clone https://github.com/mikalziane/R304-2026-2027.git
cd R304-2026-2027/TP1
```

Si le projet est ouvert dans IntelliJ IDEA, vous pouvez aussi ouvrir son terminal avec **View > Tool Windows > Terminal** (`Alt+F12`). Depuis le dossier `TP1`, lancez `.\mvnw.cmd test` sous PowerShell. Après les tests, vous pouvez lancer la pile avec `java -cp pile/target/classes piles.Main`.

Les deux exercices se trouvent dans les dossiers `pile` et `refuge` du projet ouvert. Dans IntelliJ IDEA, ouvrez `piles.Main` et cliquez sur le triangle vert à côté de `main`. Repérez `Pile` et son client `Main` : un client est ici une classe qui utilise les services d’une autre classe.

À la fin d’une étape vérifiée, faites un **commit** pour enregistrer votre version localement, puis un **push** vers votre dépôt privé pour la sauvegarder sur GitHub. La section [Enregistrer puis sauvegarder une étape de travail](#enregistrer-puis-sauvegarder-une-étape-de-travail) prend comme exemple l’ajout d’un test pour la pile. Elle explique aussi chaque commande si IntelliJ n’est pas disponible. Git gère l’ensemble du clone : ne créez pas un second dépôt dans `TP1`.

## Exercice 1 La pile 45 minutes

Une pile suit la règle « dernier entré, premier sorti ». La capacité de la pile reste fixée à sa création. Dans cet exercice, utilisez uniquement des appels valides : ne dépilez pas une pile vide et n’empilez pas dans une pile pleine.

### Observer les dépendances 10 minutes

1. Exécutez `Main` et retrouvez dans le code les opérations qui produisent chaque affichage.
2. Relevez les accès directs de `Main` aux attributs de `Pile`. Quels rôles jouent `t`, `cpt` et `taille` ?
3. Supposons que le tableau soit remplacé par une `LinkedList<Integer>`, en conservant une capacité maximale. Quelles instructions de `Main` faudra-t-il examiner ou modifier ? Justifiez sur deux exemples.

**Rappel utile.** Encapsuler une représentation consiste à la cacher aux clients et à leur proposer des opérations dont le sens ne dépend pas de cette représentation. Remplacer l’accès à `t` par un `getTableau()` qui retourne ce même tableau ne résout pas le problème.

### Protéger la représentation 15 minutes

4. Rendez les attributs de `Pile` privés. Adaptez `Main` en utilisant les méthodes déjà présentes : `empiler`, `depiler`, `getSommet`, `estVide`, `estPleine`. Gardez le nom de classe `Pile`.
5. Vérifiez que les résultats affichés sont conservés. Expliquez pourquoi il n’est pas nécessaire d’exposer `cpt` au client.

### Vérifier le contrat 15 minutes

Le contrat précise le comportement visible : `getSommet()` consulte sans retirer, `depiler()` retire et retourne le dernier élément empilé, et la capacité maximale reste respectée.

6. Ouvrez le fichier fourni `pile/src/test/java/piles/PileTest.java`. Exécutez ce premier test :

```java
package piles;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PileTest {
    @Test
    void consulterNeRetirePas() {
        Pile p = new Pile(2);
        p.empiler(5);
        assertEquals(5, p.getSommet());
        assertEquals(5, p.getSommet());
        assertFalse(p.estVide());
    }
}
```

Le test fourni doit déjà réussir sur la version initiale. Il vérifie un comportement, pas l’encapsulation : vous devez aussi vérifier les accès dans le code.

7. Écrivez un second test : pile de capacité 2 initialement vide ; empilement de 5 puis 7 ; pile pleine ; dépilement de 7 puis 5 ; pile à nouveau vide. Vérifiez les valeurs et les états intermédiaires. Expliquez le défaut que détecterait l’ordre attendu 7 puis 5.

### Bilan 5 minutes

8. Après encapsulation, quels fichiers devraient changer pour passer du tableau à la liste ? Les tests devraient-ils changer ? Sous quelle condition ? Enregistrez cette étape par un commit, puis poussez-le vers votre dépôt privé et vérifiez-le sur GitHub en suivant la [procédure Git de ce sujet](#enregistrer-puis-sauvegarder-une-étape-de-travail).

**Prolongement si vous avez terminé.** Remplacez effectivement le tableau par une liste en conservant la capacité maximale, le nom `Pile`, les signatures et le comportement des méthodes. Réexécutez les mêmes tests et le même client sans les modifier.

## Exercice 2 Le refuge 110 minutes

Le refuge accueille actuellement au plus 5 chiens et 10 chats. Le programme demande à l’utilisateur quel animal ajouter, puis affiche des messages sur les cris et la nourriture. Ces messages sont des données fictives de l’exercice.

Trois changements sont envisagés : modifier les capacités, ajouter une espèce, remplacer l’interface console. Nous souhaitons que ces changements imposent aussi peu de modifications que possible à la gestion du refuge. L’interface console peut encore connaître les espèces qu’elle propose et construire les animaux : nous ne cherchons pas à rendre tout le programme indépendant de tout.

### Comprendre et prévoir 15 minutes

1. Dans IntelliJ, ouvrez `refuge/src/main/java/Main.java` et lancez sa méthode `main`. En terminal, après les tests et depuis le dossier `TP1`, lancez `java -cp refuge/target/classes Main`. Exécutez le programme avec un chien et un chat. Repérez dans `Refuge` les lectures au clavier, les affichages, le stockage, les capacités et les comportements particuliers aux espèces.
2. Pour chacun des trois changements, relevez deux endroits à examiner. Ne comptez pas seulement les lignes : expliquez pourquoi elles dépendent du choix qui change.

### Séparer la console 30 minutes

**Rappel utile.** Un paquetage regroupe des classes. Le placer « sous » un autre dans son nom ne lui donne pas d’accès privilégié. Une dépendance de code existe notamment lorsqu’une classe nomme une autre classe ou appelle ses méthodes.

3. Créez `refuge.ihm` et placez-y `Main`. Déplacez dans cette classe `session`, les questions au clavier et les affichages. Toutes les utilisations de `Scanner`, `System.in` et `System.out` doivent sortir de `Refuge`.
4. Donnez à `Refuge` des méthodes appelables sans dialogue au clavier : vérifier une place disponible, ajouter un animal et obtenir les résultats à afficher. Procédez par petites étapes en recompilant. Des méthodes provisoires propres aux chiens et aux chats sont acceptées à cette étape.
**Aide par étapes si les déplacements ne compilent pas.** Les tableaux et compteurs restent privés dans `Refuge` : ne les rendez pas publics pour résoudre les erreurs.

- Déplacez d’abord seulement `Main` dans le paquetage `refuge.ihm` et importez `refuge.Refuge`. Le nom de la classe à lancer devient `refuge.ihm.Main`.
- Dans `Main`, créez un objet `Refuge`. Déplacez ensuite la boucle de dialogue dans une méthode `session(Refuge refuge)` de `Main` ; les saisies restent dans cette méthode.
- Lorsqu’une ligne déplacée utilise un tableau ou un compteur privé, remplacez cet accès par une demande à l’objet `refuge`. Vous pouvez d’abord créer dans `Refuge` les méthodes publiques `boolean ajouterChien(Chien chien)` et `boolean ajouterChat(Chat chat)` : elles vérifient le quota, ajoutent si possible et indiquent si l’ajout a réussi. `Main` construit l’animal et affiche le résultat.
- Pour les cris et la nourriture, faites retourner une `String` par `lesAnimauxCrient()` et `nourrirLesAnimaux()`. La boucle qui prépare ces textes reste dans `Refuge`. Construisez une chaîne contenant les messages séparés par des retours à la ligne ; `Main` affiche cette chaîne avec `System.out.print`.
- Compilez après chaque petit changement. Ces méthodes propres aux espèces sont provisoires : l’étape suivante les remplacera par `ajouter(IAnimal animal)`.

5. Exécutez à nouveau le même scénario. Qu’aura-t-on à remplacer pour utiliser une autre interface utilisateur ?

Pour limiter le travail, les méthodes de cris et de nourriture peuvent encore retourner des chaînes prêtes à afficher. Cette simplification conserve une dépendance au format des messages ; elle ne rend pas le refuge indépendant de toute présentation.

### Traiter les espèces par un même contrat 40 minutes

**Rappel utile.** Une interface Java décrit des services communs. Une variable de type `IAnimal` peut désigner un `Chien` ou un `Chat` qui implémente ce contrat. L’appel d’une méthode exécute le comportement de l’objet concerné : c’est le polymorphisme.

6. Créez dans `refuge` l’interface `IAnimal` avec quatre méthodes retournant une chaîne : `getNom()`, `getEspece()`, `getCri()` et `getNourriture()`.
7. Placez `Chien` et `Chat` dans `refuge.animaux` et faites-leur implémenter cette interface. Par exemple, un chien retourne `"chien"` pour son espèce et `"aboie"` pour son cri. Adaptez les imports dans le test fourni `AnimauxTest` après le déplacement des classes. Une classe abstraite commune est facultative : elle peut attendre.
8. Remplacez les deux tableaux et leurs compteurs par une collection de `IAnimal`. Utilisez les méthodes de l’interface pour traiter tous les animaux par la même boucle. `Refuge` ne doit plus nommer `Chien` ou `Chat`, ni tester ces types avec `instanceof`.
9. Faites fournir les capacités au constructeur de `Refuge` sous la forme d’une `Map<String, Integer>`. La configuration `chien = 5`, `chat = 10` est créée dans `Main`. `Refuge` conserve une copie de cette map. Pour une espèce absente de la configuration, la capacité est 0. Une capacité configurée est positive ou nulle. Aide Java : `new HashMap<>(capacites)` copie une map, et `placesMax.getOrDefault(espece, 0)` lit une capacité en utilisant 0 lorsque la clé est absente.
10. Créez dans `Refuge` la méthode `public boolean placeDisponiblePour(String espece)` en comptant les animaux déjà présents de cette espèce. La méthode `boolean ajouter(IAnimal animal)` retourne `false` sans modifier la collection si aucune place n’est disponible ; sinon elle ajoute l’animal et retourne `true`. L’IHM affiche le résultat. Le contrôle doit donc rester effectif même sans console.

**Aide collections.** Une liste contient les animaux présents ; une map associe à chaque nom d’espèce sa capacité. Dans `Main`, créez la configuration avant le refuge :

```java
Map<String, Integer> capacites = new HashMap<>();
capacites.put("chien", 5);
capacites.put("chat", 10);
Refuge refuge = new Refuge(capacites);
```

Importez `java.util.Map` et `java.util.HashMap`. Les valeurs 5 et 10 appartiennent à la configuration, pas au code interne de `Refuge`.

Les noms d’espèces sont des identifiants convenus, identiques dans la configuration et dans `getEspece()` ; ils restent constants pour un animal donné. Il n’est pas demandé de charger un fichier de configuration ni de traiter des valeurs nulles.

### Vérifier le changement 15 minutes

11. Sans appeler `Main`, testez avec JUnit un refuge autorisant un seul chien : premier ajout accepté, second refusé, plus de place disponible pour cette espèce. Aucun clavier ne doit être requis.
Créez le fichier `refuge/src/test/java/refuge/RefugeTest.java`. Vous pouvez partir de ce squelette ; les trois assertions sont à écrire :

```java
package refuge;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import refuge.animaux.Chien;

class RefugeTest {
    @Test void refuseUnSecondChien() {
        Refuge refuge = new Refuge(Map.of("chien", 1));
        // Vérifier que ajouter(new Chien("Rex")) renvoie true.
        // Vérifier que ajouter(new Chien("Milo")) renvoie false.
        // Vérifier que placeDisponiblePour("chien") renvoie false.
    }
}
```

`Map.of("chien", 1)` crée ici une configuration avec une seule association. Utilisez `assertTrue(...)` pour une condition attendue vraie et `assertFalse(...)` pour une condition attendue fausse. Un test sans assertion ne vérifie pas le résultat attendu.

12. Indiquez où ajouter un lapin : nouvelle classe, capacité et choix dans la console. Quelle modification reste nécessaire dans `Refuge` ? Vérifiez votre réponse dans le code. L’implémentation de `Lapin` est un prolongement si le temps le permet.

### Bilan 10 minutes

13. Dessinez les trois paquetages et les classes/interfaces principales. Tracez une flèche de A vers B quand A utilise B, en distinguant le lien d’implémentation d’une interface. Situez les créations `new Chien(...)` et `new Chat(...)`.
14. En quelques phrases, expliquez ce que vous avez caché, de quel client, et pour quels changements. Enregistrez une étape cohérente par un commit, puis poussez-la vers votre dépôt privé.

**Si vous manquez de temps.** Terminez une étape qui compile et notez précisément les dépendances restantes. Priorité à la séparation de la console, puis au contrat commun des animaux. Ne supprimez pas les limites d’accueil pour faire passer le programme. L’enseignant organise la synthèse à partir des étapes atteintes.

## Synthèse de séance 10 minutes

Pour la pile puis le refuge, donnez un exemple de changement désormais localisé et un exemple de changement qui peut encore toucher le client. Expliquez pourquoi une suite de tests réussie ne suffit pas à prouver que les dépendances interdites ont disparu.

Conservez le code, les tests et vos réponses courtes avec les fichiers de travail. Le [dernier contrôle Git](#enregistrer-puis-sauvegarder-une-étape-de-travail) se fait sur votre dépôt privé avant de quitter la séance. Les corrigés seront diffusés sur Moodle. Aucun rapport séparé n’est demandé.

## Créer votre dépôt personnel de TP une seule fois

Le **clone** est votre copie locale du cours, dans votre espace personnel sur Z: à l’IUT ou dans votre dossier de travail sur votre ordinateur. Un **dépôt distant** est une copie hébergée sur GitHub. Vous conserverez deux adresses : `origin` désignera votre dépôt privé, pour sauvegarder votre travail ; `upstream` désignera le dépôt public du cours, pour récupérer les supports suivants. Ces noms sont des repères enregistrés dans Git, pas des dossiers.

Effectuez cette configuration après avoir ouvert `TP1/pom.xml` et lancé les deux tests de départ. Réutilisez le clone déjà créé : ne créez pas un deuxième projet Java ni un deuxième historique Git.

1. Connectez-vous à votre compte sur GitHub. Cliquez sur **+ > New repository**. Comme propriétaire (**Owner**), choisissez votre compte. Nommez le dépôt `R304-travaux`, choisissez **Private**, laissez désactivée l’ajout d’un README et ne choisissez ni fichier `.gitignore` ni licence. Cliquez sur **Create repository**. Il doit être vide car votre clone contient déjà ces fichiers et un historique.
2. Sur la page du dépôt vide, copiez son URL **HTTPS**, de la forme `https://github.com/VOTRE-COMPTE/R304-travaux.git`. Utilisez votre véritable nom de compte, pas le texte `VOTRE-COMPTE`.
3. Dans IntelliJ, ouvrez **Git > Manage Remotes**. Si plusieurs dépôts apparaissent, sélectionnez le dépôt parent `R304-2026-2027`, pas un nouveau dépôt dans `TP1`.
4. Sélectionnez `origin`, cliquez sur **Edit** (crayon) et renommez cette adresse en `upstream`, en conservant l’URL du cours. Validez. Si votre version ne présente pas de crayon, ouvrez la fenêtre **Git**, onglet **Log**, puis faites un clic droit sur une branche distante sous `origin` et choisissez **Edit Remote**.
5. Dans **Git > Manage Remotes**, cliquez sur **+** pour ajouter une nouvelle adresse : nom `origin`, URL HTTPS de **votre dépôt privé** copiée à l’étape 2. Validez. Vérifiez que `origin` pointe vers votre compte et `upstream` vers `mikalziane`.
6. Choisissez **Git > Push**. Après le renommage, la destination proposée peut encore être `upstream/main`. Cliquez sur cette destination et choisissez **origin**, branche **main** (ou **Define remote** si proposé, avec le nom origin et votre URL privée). Vérifiez que l’envoi vise votre compte, puis cliquez sur **Push**. Si une connexion est demandée, utilisez votre compte GitHub personnel via la connexion proposée dans le navigateur. Vérifiez cette destination à chaque push.
7. Actualisez la page de votre dépôt sur GitHub : elle doit afficher la mention **Private** et les fichiers du cours. Ce premier push copie l’historique initial ; vos propres modifications viendront ensuite.

Si le push est refusé ou si la destination est le compte `mikalziane`, arrêtez-vous et demandez de l’aide : n’utilisez pas **Force Push**. Si votre dépôt personnel existe déjà, vérifiez les deux adresses et réutilisez-le sans recréer le dépôt ni réinitialiser Git.

**Sur une autre machine**, clonez votre dépôt privé `R304-travaux`, puis ouvrez `TP1/pom.xml`. Les adresses distantes autres que `origin` ne sont pas copiées par le clonage : ajoutez à nouveau `upstream` si vous en avez besoin. Le dossier local peut alors s’appeler `R304-travaux` au lieu de `R304-2026-2027` ; le dossier `TP1` reste le même.

Pour les séances suivantes, les nouveaux sujets seront publiés sur `upstream`. Un push vers `origin` ne les récupère pas. L’enseignant indiquera comment les intégrer ; ne recopiez pas les fichiers par-dessus votre travail et ne fusionnez pas au hasard.

## Enregistrer puis sauvegarder une étape de travail

**Exemple.** Vous venez d’ajouter un test dans `PileTest.java`. Il passe et vous souhaitez conserver cette étape avant de poursuivre.

Un **commit** enregistre une version dans votre dépôt local. Un **push** transmet les commits à votre dépôt privé sur GitHub. Une modification qui n’a pas été enregistrée dans un commit n’est pas envoyée par un push.

### Dans IntelliJ IDEA

1. Ouvrez la fenêtre **Commit** (menu **View > Tool Windows > Commit**, ou **Git > Commit** selon votre version).
2. Sélectionnez les fichiers modifiés de `TP1/pile/src`, y compris les nouveaux tests. Ouvrez le diff d’un fichier en double-cliquant dessus : il compare son ancienne version et votre version. Vérifiez les ajouts et suppressions. Ne sélectionnez pas les dossiers `target` ni les fichiers de configuration personnels de l’IDE.
3. Saisissez un message qui décrit votre changement, par exemple `Ajouter un test pour la pile`, puis cliquez sur **Commit**. Si l’IDE demande une identité Git, indiquez votre propre nom et votre adresse associée à GitHub (l’adresse de confidentialité GitHub convient aussi). Cette identité identifie l’auteur des commits ; elle ne remplace pas la connexion à GitHub.
4. Choisissez **Git > Push**, vérifiez la destination **origin/main** sur votre compte personnel, puis cliquez sur **Push**.
5. Sur la page GitHub de votre dépôt privé, ouvrez `TP1/pile/src`. Vérifiez que votre dernier message de commit apparaît et ouvrez un fichier modifié pour vérifier son contenu.

Pour le refuge, répétez ce parcours avec ses fichiers et un message adapté. Un commit décrit une étape réelle, pas seulement la fin de la séance.

### En terminal si IntelliJ IDEA n’est pas disponible

La création du dépôt privé vide sur GitHub reste la même. Dans le dossier du clone, configurez les adresses **une seule fois**, en remplaçant `VOTRE-COMPTE` par votre compte :

```text
git remote rename origin upstream
git remote add origin https://github.com/VOTRE-COMPTE/R304-travaux.git
git remote -v
git push -u origin main
```

`remote rename` conserve le lien vers le cours sous le nom `upstream` ; `remote add` ajoute votre dépôt privé sous le nom `origin`. `remote -v` affiche les adresses à vérifier. Le premier `push -u` envoie la branche et établit son suivi vers `origin/main`. Si les deux adresses sont déjà configurées, vérifiez-les avec `git remote -v` et ne relancez pas les commandes rename/add.

Une fois votre test passé, exécutez ces commandes depuis le dossier **TP1**.

```text
git status
git diff
git add pile/src
git diff --cached
git commit -m "Ajouter un test pour la pile"
git push origin main
git log --oneline -3
```

- `git status` indique les fichiers modifiés, nouveaux et déjà sélectionnés pour le commit.
- `git diff` montre les changements non encore sélectionnés dans les fichiers déjà suivis. Il ne montre pas le contenu des nouveaux fichiers non suivis.
- `git add pile/src` sélectionne les modifications, nouveaux fichiers et suppressions de ce dossier pour le prochain commit. Il ne crée pas de commit et n’envoie rien sur GitHub. Depuis la racine du clone, le chemin serait `TP1/pile/src`.
- `git diff --cached` permet de relire ce qui sera enregistré, nouveaux fichiers sélectionnés compris.
- `git commit` enregistre cette sélection localement ; `git push origin main` transmet les commits à votre dépôt personnel.
- `git log --oneline -3` affiche les trois derniers commits locaux, du plus récent au plus ancien, sur une ligne chacun. Cela ne vérifie pas le succès du push : vérifiez aussi la page GitHub.

Si Git demande une identité, configurez-la pour ce dépôt avec `git config user.name "Votre nom"` et `git config user.email "Votre adresse"`, en remplaçant les valeurs par les vôtres, puis relancez le commit.

Avant de quitter la salle, vérifiez sur GitHub que votre dernier commit a été transmis. Gardez aussi votre dossier local sur Z:, y compris `.git` ; sur votre machine personnelle, gardez-le dans votre dossier de travail. Un push ne sauvegarde ni les fichiers ignorés ni les modifications non commitées. Aucune invitation de l’enseignant ni remise évaluée n’est demandée à cette étape.
