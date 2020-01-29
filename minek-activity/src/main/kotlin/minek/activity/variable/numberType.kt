package minek.activity.variable

class ShortType : VariableType {

    override fun typeName(): String = "short"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = (value as Short).toLong()
    }

    override fun getValue(store: VariableStore): Short = store.longValue!!.toShort()

    override fun isAbleToStore(value: Any): Boolean = value is Short

}

class IntType : VariableType {

    override fun typeName(): String = "int"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = (value as Int).toLong()
    }

    override fun getValue(store: VariableStore): Int = store.longValue!!.toInt()

    override fun isAbleToStore(value: Any): Boolean = value is Int

}

class LongType : VariableType {

    override fun typeName(): String = "long"

    override fun setValue(value: Any, store: VariableStore) {
        store.longValue = value as Long
    }

    override fun getValue(store: VariableStore): Long = store.longValue!!

    override fun isAbleToStore(value: Any): Boolean = value is Long

}

class FloatType : VariableType {

    override fun typeName(): String = "float"

    override fun setValue(value: Any, store: VariableStore) {
        store.doubleValue = (value as Float).toDouble()
    }

    override fun getValue(store: VariableStore): Float = store.doubleValue!!.toFloat()

    override fun isAbleToStore(value: Any): Boolean = value is Float

}

class DoubleType : VariableType {

    override fun typeName(): String = "double"

    override fun setValue(value: Any, store: VariableStore) {
        store.doubleValue = value as Double
    }

    override fun getValue(store: VariableStore): Double = store.doubleValue!!

    override fun isAbleToStore(value: Any): Boolean = value is Double

}