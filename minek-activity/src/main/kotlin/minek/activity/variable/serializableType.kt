package minek.activity.variable

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.io.Serializable

class SerializableType(private val xmlMapper: XmlMapper) : VariableType {

    override fun typeName(): String = "serializable"

    override fun setValue(value: Any, store: VariableStore) {
        store.byteaValue = xmlMapper.writeValueAsBytes(value)
    }

    override fun getValue(store: VariableStore): Serializable =
        TODO("using getValue(store: VariableStore, type: Class<T>)")

    fun <T> getValue(store: VariableStore, type: Class<T>): T {
        return xmlMapper.readValue(store.byteaValue, type)
    }

    override fun isAbleToStore(value: Any): Boolean = value is Serializable

}