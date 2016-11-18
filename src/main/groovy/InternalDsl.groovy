// version courte :
// - ici on fait en une ligne, ce qu'on a fait en 4 dans ExternalDsl
// - on dit que la classe à utiliser pour le script est `MasterSpecScript`
// - on balance le DSL directement
// - une seule ligne est obligatoire par rapport à la version "externe"

// version détaillée :
//
// ici on aurait encore pu faire un import :
// import groovy.transform.BaseScript
// @BaseScript MasterSpecScript baseScript
// mais comme on le référence qu'une fois, OSEF on met le chemin complet
//
// rappel: les annotations injectent du code _avant_ la compilation (préprocesseur)
// cette annotation indique que pour la compilation de ce script, la classe
// générée sera du type InternalDsl (cf le nom du script) **mais** cette classe
// générée héritera de la classe demandée (MasterSpecScript) pour qu'elle contienne
// ce qu'on veut qu'elle contienne

@groovy.transform.BaseScript MasterSpecScript baseScript

// le nom donné permet d'accéder au script courant, en l'utilisant, plutôt qu'en
// utilisant 'this' (cf ExternalDsl, où on utilise 'this' car on a pas de nom à dispo)

assert this == baseScript

// le DSL, directement dans le code

email {
    from 'dsl-guru@mycompany.com'
    to 'john.doe@waitaminute.com'
    subject 'The pope has resigned!'
    body {
        p 'Really, the pope has resigned!'
    }
}
