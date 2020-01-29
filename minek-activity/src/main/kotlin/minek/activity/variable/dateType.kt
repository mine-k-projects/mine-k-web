package minek.activity.variable

import java.time.*
import java.util.*

class DateType : VariableType {

    override fun typeName(): String = "date"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = (value as Date).time
    }

    override fun getValue(store: VariableStore): Date = Date(store.longValue!!)

    override fun isAbleToStore(value: Any): Boolean = value::class.java.isAssignableFrom(Date::class.java)

}

class LocalTimeType : VariableType {

    override fun typeName(): String = "local_ime"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = (value as LocalTime).toNanoOfDay()
    }

    override fun getValue(store: VariableStore): LocalTime = LocalTime.ofNanoOfDay(store.longValue!!)

    override fun isAbleToStore(value: Any): Boolean = value is LocalTime

}

class LocalDateTimeType : VariableType {

    override fun typeName(): String = "local_date_time"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = (value as LocalDateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    override fun getValue(store: VariableStore): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(store.longValue!!), ZoneId.systemDefault())

    override fun isAbleToStore(value: Any): Boolean = value is LocalDateTime

}

class LocalDateType : VariableType {

    override fun typeName(): String = "local_date"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = (value as LocalDate).toEpochDay()
    }

    override fun getValue(store: VariableStore): LocalDate = LocalDate.ofEpochDay(store.longValue!!)

    override fun isAbleToStore(value: Any): Boolean = value is LocalDate

}