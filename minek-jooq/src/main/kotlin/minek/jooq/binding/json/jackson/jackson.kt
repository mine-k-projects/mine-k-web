package minek.jooq.binding.json.jackson

import com.fasterxml.jackson.databind.JsonNode
import minek.jooq.binding.json.JsonBinding
import minek.jooq.converter.jackson.JsonNodeConverter
import org.jooq.Converter

class JsonNodeBinding : JsonBinding<JsonNode>() {
    override fun converter(): Converter<Any, JsonNode> = JsonNodeConverter()
}
