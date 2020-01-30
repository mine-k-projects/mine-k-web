package minek.activity.expression

import minek.activity.instance.Instance
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.MapContext
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext

interface ExpressionManager {
    fun <T> evaluate(instance: Instance, expression: String): T
}

abstract class ScriptEngineExpressionManager : ExpressionManager {
    protected abstract val extension: String

    @Suppress("UNCHECKED_CAST")
    override fun <T> evaluate(instance: Instance, expression: String): T {
        print("expression : $expression")

        val engine = ScriptEngineManager().getEngineByExtension(extension)!!
        val context = SimpleScriptContext()
        val bindings = context.getBindings(ScriptContext.ENGINE_SCOPE)
        bindings.putAll(instance.getVariables())

        val value = engine.eval(expression, context) as T

        println(", result : $value")

        return value
    }
}

class KotlinScriptExpressionManager : ScriptEngineExpressionManager() {
    override val extension: String = "kts"
}

class JavaScriptExpressionManager : ScriptEngineExpressionManager() {
    override val extension: String = "js"
}

@Suppress("SpellCheckingInspection")
class JexlExpressionManager : ExpressionManager {
    private val engine: JexlEngine = JexlBuilder().create()

    @Suppress("UNCHECKED_CAST")
    override fun <T> evaluate(instance: Instance, expression: String): T {
        print("expression : $expression")

        val context = MapContext(instance.getVariables())
        val value = engine.createExpression(expression).evaluate(context) as T

        println(", result : $value")

        return value
    }
}
