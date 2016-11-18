// cette class donne la spec du DSL
// une fonction = un mot clé
// le nom de la fonction est le mot clé

// les arguments de la fonction sont les éléments suivant le mot clé dans le DSL

class EmailSpec {
    void from(String from) { println "From: $from" }

    void to(String... to) { println "To: $to" }

    void subject(String subject) { println "Subject: $subject" }

    // le mot clé body prend une {} derrière (qui est un objet Closure)
    // on délègue l'analyse de cette closure, qui se chargera de voir
    // ce qu'il y a dedans

    // ici le code est explicite pour 'body', se référer au README.md pour
    // une comparaison avec ce qui est fait dans MasterSpecScript pour 'email'

    void body(Closure body) {
        println "EmailSpec: email keywork closure delegation start"
        def bodySpec = new BodySpec()
        def code = body.rehydrate(bodySpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        println "EmailSpec: email keywork closure delegation ends"
    }
}
