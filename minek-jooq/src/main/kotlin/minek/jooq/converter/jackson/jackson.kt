package minek.jooq.converter.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.node.NullNode
import org.jooq.Converter
import org.jooq.JSON
import org.jooq.JSONB

internal class JSONBToJacksonConverter : Converter<JSONB, JsonNode> {
    private val mapper: ObjectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun from(databaseObject: JSONB?): JsonNode {
        return if (databaseObject?.data() == null) {
            NullNode.instance
        } else {
            mapper.valueToTree(databaseObject.data())
        }
    }

    override fun to(userObject: JsonNode?): JSONB? {
        return if (userObject == null || userObject === NullNode.instance) {
            null
        } else {
            JSONB.valueOf(mapper.writeValueAsString(userObject))
        }
    }

    override fun fromType(): Class<JSONB> = JSONB::class.java

    override fun toType(): Class<JsonNode> = JsonNode::class.java
}

internal class JSONToJacksonConverter : Converter<JSON, JsonNode> {
    private val mapper: ObjectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun from(databaseObject: JSON?): JsonNode {
        return if (databaseObject?.data() == null) {
            NullNode.instance
        } else {
            mapper.valueToTree(databaseObject.data())
        }
    }

    override fun to(userObject: JsonNode?): JSON? {
        return if (userObject == null || userObject === NullNode.instance) {
            null
        } else {
            JSON.valueOf(mapper.writeValueAsString(userObject))
        }
    }

    override fun fromType(): Class<JSON> = JSON::class.java

    override fun toType(): Class<JsonNode> = JsonNode::class.java
}
