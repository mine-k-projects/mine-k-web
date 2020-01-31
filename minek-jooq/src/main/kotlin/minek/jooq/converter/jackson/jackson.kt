package minek.jooq.converter.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.node.NullNode
import org.jooq.Converter

internal class JsonNodeConverter : Converter<Any, JsonNode> {
    private val mapper: ObjectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun from(databaseObject: Any?): JsonNode {
        return if (databaseObject == null) NullNode.instance else mapper.valueToTree("" + databaseObject)
    }

    override fun to(userObject: JsonNode?): Any? {
        return if (userObject == null || userObject === NullNode.instance) null else mapper.writeValueAsString(
            userObject
        )
    }

    override fun fromType(): Class<Any> = Any::class.java

    override fun toType(): Class<JsonNode> = JsonNode::class.java
}
