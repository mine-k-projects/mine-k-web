package minek.activity.model

import minek.activity.behavior.Behavior
import java.util.*

interface FlowElementsContainer {
    fun getFlowElement(id: UUID): FlowElement?
    fun getFlowElements(): Collection<FlowElement>
    fun addFlowElement(element: FlowElement)
    fun removeFlowElement(elementId: UUID)
}

abstract class BaseElement {
    val id: UUID = UUID.randomUUID()
    val attributes: MutableMap<String, String> = mutableMapOf()
}

//interface ActivityListener {
//
//}

abstract class FlowElement : BaseElement() {
    var name: String? = null
//    val executionListeners: Set<ActivityListener> = mutableSetOf()
}

data class SequenceFlow(
    val conditionExpression: String? = null,
    val sourceRef: UUID,
    val targetRef: UUID,
    val skipExpression: String? = null
) {
    companion object {
        fun of(source: FlowNode, target: FlowNode): SequenceFlow {
            val sequenceFlow = SequenceFlow(sourceRef = source.id, targetRef = target.id)
            source.outgoingFlows.add(sequenceFlow)
            target.incomingFlows.add(sequenceFlow)
            return sequenceFlow
        }
    }
}

abstract class FlowNode : FlowElement() {
    //    var asynchronous: Boolean = false
    internal val incomingFlows: MutableList<SequenceFlow> = mutableListOf()
    internal val outgoingFlows: MutableList<SequenceFlow> = mutableListOf()
    var behavior: Behavior? = null
}

abstract class Activity : FlowNode() {

}
