package minek.activity.variable

class VariableStore(
    val type: String,
    val name: String,
    var textValue: String? = null,
    var longValue: Long? = null,
    var doubleValue: Double? = null,
    var byteaValue: ByteArray? = null
)

interface VariableType {
    fun typeName(): String
    fun setValue(value: Any, store: VariableStore)
    fun getValue(store: VariableStore): Any
    fun isAbleToStore(value: Any): Boolean
}
