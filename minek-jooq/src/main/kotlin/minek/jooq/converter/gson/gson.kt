package minek.jooq.converter.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import org.jooq.Converter
import org.jooq.JSON
import org.jooq.JSONB

internal class JSONBToGsonConverter : Converter<JSONB, JsonElement> {
    private val gson = Gson()

    override fun from(databaseObject: JSONB?): JsonElement {
        return if (databaseObject?.data() == null) {
            JsonNull.INSTANCE
        } else {
            gson.fromJson(databaseObject.data(), JsonElement::class.java)
        }
    }

    override fun to(userObject: JsonElement?): JSONB? {
        return if (userObject == null || userObject === JsonNull.INSTANCE) {
            null
        } else {
            JSONB.valueOf(gson.toJson(userObject))
        }
    }

    override fun fromType(): Class<JSONB> = JSONB::class.java

    override fun toType(): Class<JsonElement> = JsonElement::class.java
}

internal class JSONToGsonConverter : Converter<JSON, JsonElement> {
    private val gson = Gson()

    override fun from(databaseObject: JSON?): JsonElement {
        return if (databaseObject?.data() == null) {
            JsonNull.INSTANCE
        } else {
            gson.fromJson(databaseObject.data(), JsonElement::class.java)
        }
    }

    override fun to(userObject: JsonElement?): JSON? {
        return if (userObject == null || userObject === JsonNull.INSTANCE) {
            null
        } else {
            JSON.valueOf(gson.toJson(userObject))
        }
    }

    override fun fromType(): Class<JSON> = JSON::class.java

    override fun toType(): Class<JsonElement> = JsonElement::class.java
}