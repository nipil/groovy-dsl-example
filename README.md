# groovy-dsl-example

A minimalist example of doing DSL in groovy

# build

./gradlew build

# find groovy jar

export GROOVY_JAR=$(find ~/.gradle/caches -name 'groovy-all-2.4.7.jar')

# example 1 : dsl included in script

java -cp $GROOVY_JAR:build/libs/groovy-dsl-example.jar InternalDsl

# example 2 : dsl in external file

java -cp $GROOVY_JAR:build/libs/groovy-dsl-example.jar ExternalDsl

# output (in both cases)

     EmailScriptSpec: email keywork closure delegation start
     From: dsl-guru@mycompany.com
     To: [john.doe@waitaminute.com]
     Subject: The pope has resigned!
     EmailSpec: email keywork closure delegation start
     Para: Really, the pope has resigned!
     EmailSpec: email keywork closure delegation ends
     EmailScriptSpec: email keywork closure delegation ends

# comments

## top-most script spec class

The script class (representing the "outer-level" dsl closure holding the top-most keywords) *has to be* an abstract class :

    abstract class EmailScriptSpec extends Script {

This is required as the "concrete class" for a groovy script is always generated dynamically (creating its `run` method)

## re-implementing the "delegate closure"

a class spec may delegate a closure to another class

if you have multiple classes doing that, you have redundant code (see `EmailScriptSpec.email` and `EmailSpec.Body`)

ideally you could refactor that _behaviour_ and share it among classes using a Trait implementation
