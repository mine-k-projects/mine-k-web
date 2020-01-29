package minek.activity.variable

class BooleanType : VariableType {

    override fun typeName(): String = "boolean"

    override fun setValue(value: Any, store: VariableStore) {
        store.textValue = (value as Boolean).toString()
    }

    override fun getValue(store: VariableStore): Boolean = store.textValue!!.toBoolean()

    override fun isAbleToStore(value: Any): Boolean = value is Boolean
}
