package minek.jooq.binding.json.gson

import com.google.gson.JsonElement
import minek.jooq.binding.json.JsonBinding
import minek.jooq.converter.gson.JsonElementConverter
import org.jooq.Converter

class JsonElementBinding : JsonBinding<JsonElement>() {
    override fun converter(): Converter<Any, JsonElement> = JsonElementConverter()
}
