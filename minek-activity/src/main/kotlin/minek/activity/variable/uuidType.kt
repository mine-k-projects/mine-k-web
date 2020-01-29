package minek.activity.variable

import java.util.*

class UUIDType : VariableType {

    override fun typeName(): String = "uuid"

    override fun setValue(value: Any, store: VariableStore) {
        store.textValue = (value as UUID).toString()
    }

    override fun getValue(store: VariableStore): UUID = UUID.fromString(store.textValue)

    override fun isAbleToStore(value: Any): Boolean = value is UUID
}
