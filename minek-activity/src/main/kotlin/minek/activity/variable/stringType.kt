package minek.activity.variable

class StringType : VariableType {

    override fun typeName(): String = "string"

    override fun setValue(value: Any, store: VariableStore) {
        store.textValue = value as String
    }

    override fun getValue(store: VariableStore): String = store.textValue!!

    override fun isAbleToStore(value: Any): Boolean = value is String

}