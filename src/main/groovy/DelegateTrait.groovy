trait DelegateTrait {
    def delegate(Closure cl, Object to) {
        println "DelegateAbility: delegating closure to ${to} starts"
        def code = cl.rehydrate(to, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        println "DelegateAbility: delegating closure to ${to} ends"
    }
}
