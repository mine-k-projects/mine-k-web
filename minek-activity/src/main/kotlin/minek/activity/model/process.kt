package minek.activity.model

import java.util.*

class SubProcess : Activity(), FlowElementsContainer {
    private val flowElementList: MutableList<FlowElement> = mutableListOf()
    private val flowElementMap: MutableMap<UUID, FlowElement> = mutableMapOf()

    override fun getFlowElement(id: UUID): FlowElement? = flowElementMap[id]

    override fun getFlowElements(): Collection<FlowElement> = flowElementList

    override fun addFlowElement(element: FlowElement) {
        flowElementList.add(element)
        flowElementMap[element.id] = element
    }

    override fun removeFlowElement(elementId: UUID) {
        flowElementList.removeIf { it.id == elementId }
        flowElementMap.remove(elementId)
    }
}

class Process : BaseElement(), FlowElementsContainer {
    var name: String? = null
    private val flowElementList: MutableList<FlowElement> = mutableListOf()
    private val flowElementMap: MutableMap<UUID, FlowElement> = mutableMapOf()

    override fun getFlowElement(id: UUID): FlowElement? = flowElementMap[id]

    override fun getFlowElements(): Collection<FlowElement> = flowElementList

    override fun addFlowElement(element: FlowElement) {
        flowElementList.add(element)
        flowElementMap[element.id] = element
    }

    override fun removeFlowElement(elementId: UUID) {
        flowElementList.removeIf { it.id == elementId }
        flowElementMap.remove(elementId)
    }

    fun initialFlowElement(): FlowElement = flowElementList.first { it is StartEvent }
}

class CallActivity : Activity() {

}