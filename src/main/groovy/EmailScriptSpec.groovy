abstract class EmailScriptSpec extends Script implements DelegateTrait {
    def email_spec_obj = new EmailSpec()

    def email(Closure cl) {
        this.delegate(cl, email_spec_obj)
    }
}
