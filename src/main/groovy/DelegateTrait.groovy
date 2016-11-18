// ici on "factorise" le code nécessaire pour déléguer

// pourquoi un trait plutôt qu'une interface ou un héritage ?
// voir le README.md pour l'explication

trait DelegateTrait {

    // ici on retrouve le même type de code que ce qui est
    // utilisé dans la méthode 'body' de EmailSpec

    def delegate(Closure cl, Object to) {
        println "DelegateAbility: delegating closure to ${to} starts"
        def code = cl.rehydrate(to, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        println "DelegateAbility: delegating closure to ${to} ends"
    }
}
