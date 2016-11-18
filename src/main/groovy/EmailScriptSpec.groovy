abstract class EmailScriptSpec extends Script {
    def email(Closure cl) {
        def email = new EmailSpec()
        def code = cl.rehydrate(email, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
