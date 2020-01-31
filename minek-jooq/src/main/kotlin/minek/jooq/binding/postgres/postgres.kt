package minek.jooq.binding.postgres

import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.JsonElement
import minek.jooq.converter.gson.JSONBToGsonConverter
import minek.jooq.converter.gson.JSONToGsonConverter
import minek.jooq.converter.jackson.JSONBToJacksonConverter
import minek.jooq.converter.jackson.JSONToJacksonConverter
import org.jooq.*
import org.jooq.impl.DSL
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.util.*

abstract class JsonBinding<T, U> : Binding<T, U> {

    abstract fun valueOf(): (String) -> T

    abstract fun coerce(): String

    @Throws(SQLException::class)
    override fun sql(ctx: BindingSQLContext<U>) {
        ctx.render().visit(DSL.`val`(ctx.convert<Any>(converter()).value())).sql(coerce())
    }

    @Throws(SQLException::class)
    override fun register(ctx: BindingRegisterContext<U>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<U>) {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<U>) {
        ctx.convert(converter()).value(valueOf().invoke(ctx.resultSet().getString(ctx.index())))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<U>) {
        ctx.convert(converter()).value(valueOf().invoke(ctx.statement().getString(ctx.index())))
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<U>) {
        throw SQLFeatureNotSupportedException()
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<U>) {
        throw SQLFeatureNotSupportedException()
    }
}

// json

class JSONToJacksonBinding : JsonBinding<JSON, JsonNode>() {
    override fun valueOf(): (String) -> JSON = JSON::valueOf
    override fun coerce(): String = "::json"
    override fun converter(): Converter<JSON, JsonNode> = JSONToJacksonConverter()
}

class JSONToGsonBinding : JsonBinding<JSON, JsonElement>() {
    override fun valueOf(): (String) -> JSON = JSON::valueOf
    override fun coerce(): String = "::json"
    override fun converter(): Converter<JSON, JsonElement> = JSONToGsonConverter()
}

// jsonb

class JSONBToJacksonBinding : JsonBinding<JSONB, JsonNode>() {
    override fun valueOf(): (String) -> JSONB = JSONB::valueOf
    override fun coerce(): String = "::jsonb"
    override fun converter(): Converter<JSONB, JsonNode> = JSONBToJacksonConverter()
}

class JSONBToGsonBinding : JsonBinding<JSONB, JsonElement>() {
    override fun valueOf(): (String) -> JSONB = JSONB::valueOf
    override fun coerce(): String = "::jsonb"
    override fun converter(): Converter<JSONB, JsonElement> = JSONBToGsonConverter()
}