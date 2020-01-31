package minek.jooq.converter.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import org.jooq.Converter

internal class JsonElementConverter : Converter<Any, JsonElement> {
    private val gson = Gson()

    override fun from(databaseObject: Any?): JsonElement {
        return if (databaseObject == null) JsonNull.INSTANCE else gson.fromJson(
            "" + databaseObject,
            JsonElement::class.java
        )
    }

    override fun to(userObject: JsonElement?): Any? {
        return if (userObject == null || userObject === JsonNull.INSTANCE) null else gson.toJson(userObject)
    }

    override fun fromType(): Class<Any> = Any::class.java

    override fun toType(): Class<JsonElement> = JsonElement::class.java
}
