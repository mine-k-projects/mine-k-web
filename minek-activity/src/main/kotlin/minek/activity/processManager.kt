package minek.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import minek.activity.expression.ExpressionManager
import minek.activity.expression.JavaScriptExpressionManager
import minek.activity.variable.*

class ProcessManager(
    val expressionManager: ExpressionManager = JavaScriptExpressionManager(),
    val objectMapper: ObjectMapper = jacksonObjectMapper(),
    val xmlMapper: XmlMapper = XmlMapper().apply { registerKotlinModule() }
) {

    val variableTypes by lazy {
        arrayOf(
            StringType(), IntType(), LongType(), DoubleType(), BooleanType(), UUIDType(), FloatType(), ShortType(), DateType(), LocalTimeType(), LocalDateTimeType(), LocalDateType(), ByteArrayType(), JsonType(objectMapper), SerializableType(xmlMapper)
        )
    }
}
