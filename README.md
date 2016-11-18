# groovy-dsl-example

Un exemple minimaliste de DSL en Groovy, et le lien avec le script (interne au script groovy en lui-même), ou bien dans un fichier externe au script

# construction

Par le wrapper, qui va récupérer toutes les dépendances

    ./gradlew build

# on localise le jar groovy

    export GROOVY_JAR=$(find ~/.gradle/caches -name 'groovy-all-2.4.7.jar')

# example 1 : dsl qui est situé un script externe

    java -cp $GROOVY_JAR:build/libs/groovy-dsl-example.jar ExternalDsl

Se référer aux commentaires dans le code pour les éléments d'_implémentation_ pertinents

A regarder en premier, car le suivant masque le comportement détaillé vu ici

# example 2 : dsl qui est inclus dans le script

Pour lancer l'exemple :

    java -cp $GROOVY_JAR:build/libs/groovy-dsl-example.jar InternalDsl

Se référer aux commentaires dans le code pour les éléments d'_implémentation_ pertinents

A regarder après la version "externe"

# Résultat attendu (dans les deux cas)

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
