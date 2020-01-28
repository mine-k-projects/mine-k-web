package minek.activity.model

abstract class Event : FlowNode() {

}

class StartEvent : Event() {
    var initiator: String? = null
}

class EndEvent : Event() {
}