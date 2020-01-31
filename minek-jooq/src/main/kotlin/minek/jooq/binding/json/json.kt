package minek.jooq.binding.json

import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.util.*
import org.jooq.*
import org.jooq.conf.ParamType
import org.jooq.impl.DSL

abstract class JsonBinding<T> : Binding<Any, T> {

    @Throws(SQLException::class)
    override fun sql(ctx: BindingSQLContext<T>) {
        if (ctx.render().paramType() == ParamType.INLINED) {
            ctx.render().visit(DSL.inline(ctx.convert(converter()).value())).sql("::json")
        } else {
            ctx.render().sql("?::json")
        }
    }

    @Throws(SQLException::class)
    override fun register(ctx: BindingRegisterContext<T>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<T>) {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<T>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<T>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<T>) {
        throw SQLFeatureNotSupportedException()
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<T>) {
        throw SQLFeatureNotSupportedException()
    }
}
