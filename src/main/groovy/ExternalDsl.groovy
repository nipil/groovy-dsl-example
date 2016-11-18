import org.codehaus.groovy.control.CompilerConfiguration

def config = new CompilerConfiguration()

// here we use the spec for the outmost element (single element)
config.scriptBaseClass = 'EmailScriptSpec'

def shell = new GroovyShell(this.class.classLoader, config)

shell.evaluate new File("example.dsl")
