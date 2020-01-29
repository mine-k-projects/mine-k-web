package minek.activity.variable

class ByteArrayType : VariableType {

    override fun typeName(): String = "byte_array"

    override fun setValue(value: Any, store: VariableStore) {
        store.byteaValue = value as ByteArray
    }

    override fun getValue(store: VariableStore): ByteArray = store.byteaValue!!

    override fun isAbleToStore(value: Any): Boolean = value is ByteArray
}
