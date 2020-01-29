package minek.activity.variable

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class JsonType(private val objectMapper: ObjectMapper) : VariableType {

    override fun typeName(): String = "json"

    override fun setValue(value: Any, store: VariableStore) {
        store.textValue = objectMapper.writeValueAsString(value)
    }

    override fun getValue(store: VariableStore): JsonNode = objectMapper.readTree(store.textValue)

    override fun isAbleToStore(value: Any): Boolean = value is JsonNode
}
