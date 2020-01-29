package minek.activity.expression

import minek.activity.instance.Instance
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression
import org.apache.commons.jexl3.MapContext

interface ExpressionManager {
    fun <T> evaluate(instance: Instance, expression: String): T
}

@Suppress("SpellCheckingInspection")
class JexlExpressionManager : ExpressionManager {
    private val engine: JexlEngine = JexlBuilder().create()

    override fun <T> evaluate(instance: Instance, expression: String): T =
        evaluate(instance, engine.createExpression(expression))

    @Suppress("UNCHECKED_CAST")
    fun <T> evaluate(instance: Instance, expression: JexlExpression): T {
        print("expression : $expression")

        val context = MapContext(instance.getVariables())
        val value = expression.evaluate(context) as T

        println(", result : $value")

        return value
    }
}
