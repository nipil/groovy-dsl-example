# groovy-dsl-example

Un exemple minimaliste de DSL en Groovy, et le lien avec le script (interne au script groovy en lui-même), ou bien dans un fichier externe au script

# construction

Par le wrapper, qui va récupérer toutes les dépendances

    ./gradlew build

# on localise le jar groovy

    export GROOVY_JAR=$(find ~/.gradle/caches -name 'groovy-all-2.4.7.jar')

# invocations directes de la jvm (après compilation `groovyc` faite par Gradle)

Ce paragraphe montre qu'on peut lancer un programme groovy compilé sans rien avoir de dispo de gradle/groovy à part le jar du langage.

## example 1 : dsl qui est situé un script externe

On lance, en utilisant les classes compilées :

    java -cp $GROOVY_JAR:build/libs/groovy-dsl-example.jar ExternalDsl

Se référer aux commentaires dans le code pour les éléments d'_implémentation_ pertinents

A regarder en premier, car le suivant masque le comportement détaillé vu ici

## example 2 : dsl qui est inclus dans le script

Pour lancer l'exemple :

    java -cp $GROOVY_JAR:build/libs/groovy-dsl-example.jar InternalDsl

Se référer aux commentaires dans le code pour les éléments d'_implémentation_ pertinents

A regarder après la version "externe"

# invocations indirecte de la jvm par appel de `groovy`

Si vous avez `groovy` installé, il y a une autre méthode pour lancer le programme.

Attention, quand on invoquait en java, le fait de spécifier le jar contenant la version *souhaitée* de groovy (utilisée pour la compilation) définissait la version utilisée pour l'exécution.

En lançant nativement en groovy, on utilisera la version du language groovy qui est utilisé *par la commande* utilisée pour l'invocation (qu'elle soit la version installé par la distribution, via sdkman, autre...)

## example 1 : dsl qui est situé un script externe

On peut aussi lancer 'nativement' en groovy, en indiquat le script principal, et en indiquant le chemin vers les autres classes groovy qui seront compilés à la volée :

    groovy -cp src/main/groovy src/main/groovy/ExternalDsl.groovy

## example 2 : dsl qui est inclus dans le script

Idem pour la version "interne" :

    groovy -cp src/main/groovy src/main/groovy/InternalDsl.groovy

## example 3 : dsl externe et pas de script intermédiaire !

Ici on peut simuler l'exemple 1 sans script intermédiaire.

On lance par groovy, en externe, en effectuant l'équivalent de `@BaseScript` par les paramètres de la ligne de commande `groovy` :

- le paramètre `-b` donne la classe qu'on avait donné à l'annotation `@BaseScript`
- l'argument de la ligne de commande n'est plus un script qui évalue le DSL (quelle que soit la méthode), mais le DSL lui même (tout seul) et l'analyse sera faite par la classe spécifiée dans `-b`

Ça donne la commande suivante :

    groovy -cp src/main/groovy -b MasterSpecScript example.dsl

Du coup, on a "juste" écrit le code minimum dont on a besoin (pas de script intermédiares en plus), le dsl est "pur" (ie tout seul dans son fichier)

Mais on a mis de l'intelligence hors du code ce qui n'est pas une excellente idée (couplage interne-externe)

Cette forme d'invocation permet néamoins de remplacer facilement la classe `MasterSpecScript` par une autre pour traiter plusieurs types de dsl dans le cadre d'une automatisation (mais il faut connaitre le nom de la classe dans le code)

Mas on pourrait faire la même chose reprennant le script de la version de l'exemple 2, et en y ajoutant par exemple des paramètres de la ligne de commande (cf `CliBuilder`) pour choisir dynamiquement à la fois le fichier contenant le dsl, et la classe `*SpecScript` qu'on voudrait utiliser pour analyser ce dsl.

On obtiendrait alors le même résultat que cet exemple 3, mais en ayant conservé l'intelligence *dans* notre programme, ce qui nous rend indépendant de l'interface d'appel de groovy.

# Résultat attendu (dans tous les deux cas)

    DelegateAbility: delegating closure to EmailSpec@1563da5 starts
    From: dsl-guru@mycompany.com
    To: [john.doe@waitaminute.com]
    Subject: The pope has resigned!
    EmailSpec: email keywork closure delegation start
    Para: Really, the pope has resigned!
    EmailSpec: email keywork closure delegation ends
    DelegateAbility: delegating closure to EmailSpec@1563da5 ends

# Explication : La classe spec pour le "plus haut niveau"

Le DSL de l'exemple ne contient qu'un mot clé maître `email`. Dans l'absolu, on pourrait avoir plusieurs mots clés qui se suivent !

En conséquence, au niveau le plus élevé, notre DSL est une closure qui contient un ou plusieurs mots clés (ici, un seul : `email`)

Du coup, il faut une classe "spec" qui va contenir le nécessaire pour renvoyer vers les classes "spec" de chaque mot clé

Ici on l'aurait appelée `MasterSpec`, et elle contiendrait une seule méthode `email` pour le mot clé de niveau max

# Explication : la classe `MasterSpec` est en fait `MasterSpecScript`

Le DSL _est en fait un script groovy_ et du coup il faut un point d'entrée qui permette son évaluation

Il faut donc un objet, de classe `Script` qui prenne en main son évaluation (quelle que soit la manière)

C'est la raison pour laquelle la classe `MasterSpec` est une classe `MasterSpecScript` : on la fait hériter de `Script` pour qu'elle puisse être directement utilisée pour l'évaluation du DSL

# Explication : la classe `MasterSpecScript` est abstraite

D'un côté, la classe Script est une classe abstraite. Elle n'a pas sa méthode `run()` définie.

D'un autre côté, un script groovy autogénère une classe de type `Script` _dont la méthode `run()` contient le texte du script_ !

Du coup, si la classe `MasterSpecScript` n'était pas abstraite mais concrête, on _serait obligés_ de fournir une implémentation de `run()`

Sauf que si on fournit une implémentation, le fonctionnement normal (et automatique) qui est de mettre le contenu du script dans `run()` ne serait plus possible

En conséquence, on conserve une classe abstraite, et on laisse `run()` indéfinie, et la mécanique de définition du contenu de `run()` peut fonctionner

# Explication : délégation de la closure à un autre objet

Une classe a des méthodes qui servent de mots clés, et ces méthodes prennent comme argument ce qui vient après dans le DSL (jusqu'à la fin du statement)

Si ce qui vient derrière est une closure, il est habituel de _déléguer_ la gestion de cette closure à un objet de la classe spec qui saura gérer son contenu.

Du coup la méthode qui correspond au mot clé qui utilise une closure ressemble à la méthode `EmailSpec:Body`

Cependant, si (et c'est généralement le cas) on a plusieurs classes et/ou fonctions qui font de la délégation de closure, on va répéter ce code pleins de fois... ce qui n'est pas génial.

On peut donc vouloir factoriser ce code.

Si on utilise une `Interface`, on ne factoriserait que la déclaration du fait qu'il y a une méthode "réutilisable" : chaque objet devrait réimplémenter concrêtement cette méthode déclarée.

Par contre, si on utilise un `Trait` on factorise à la fois la déclaration, mais aussi le comportement par défaut associé à cette déclaration.

C'est ce qui est fait dans la classe `DelegateTrait` et sa méthode `delegate`, qui est utilisée dans `MasterSpecScript` à la méthode `email`

A titre d'exercice, on peut appliquer le même principe pour éviter d'avoir le code "redondant" dans `EmailSpec:body`