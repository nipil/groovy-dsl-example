// ceci est un script "externe", détaché du DSL
// il va évaluer explicitement le DSL

// on aurait pu faire un import mais on réfère qu'une fois à ce paquet alors OSEF.

// ici on créé un objet config, qui sert au paramétrage du shell groovy
// un `GroovyShell` permet d'exécuter un `Script`
def config = new org.codehaus.groovy.control.CompilerConfiguration()

// on dit quelle type de classe utiliser pour le shell qu'on va créér
config.scriptBaseClass = 'MasterSpecScript'

// ici, `this` est l'instance qui représente le script `ExternalDsl.groovy`
// cette instance est de type `ExternalDsl` qui hérite de `Script`
// cette classe `ExternalDsl` est celle qui est auto-générée

def shell = new GroovyShell(this.class.classLoader, config)

// pour finir on lit le fichier contenant le DSL
// et on l'évalue dans le **contexte** du shell qu'on vient de configurer

// ici le script courant est une instance de ExternalDsl (cf nom du fichier)

assert this.class == ExternalDsl

shell.evaluate new File("example.dsl")
