
// cette classe est abstraite, voir le README.md pour l'explication
// cette classe implémente un trait, voir le README.md pour l'explication
// cette classe hérite de script, voir le README.md pour l'explication

abstract class MasterSpecScript extends Script implements DelegateTrait {
    def email_spec_obj = new EmailSpec()

    // on a une méthode appelée 'email', parce qu'on a un mot clé 'email' dans le DSL
    def email(Closure cl) {
        this.delegate(cl, email_spec_obj)
    }
}
