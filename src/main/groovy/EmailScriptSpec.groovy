abstract class EmailScriptSpec extends Script {
    def email(Closure cl) {
        println "EmailScriptSpec: email keywork closure delegation start"
        def email = new EmailSpec()
        def code = cl.rehydrate(email, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        println "EmailScriptSpec: email keywork closure delegation ends"
    }
}
