package minek.activity.instance

import minek.activity.ProcessManager
import minek.activity.behavior.BehaviorFactory
import minek.activity.extension.initialFlowElement
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.instance.FlowNode
import org.camunda.bpm.model.bpmn.instance.Process

enum class ActivityStatus {
    READY, RUNNING, COMPLETED, SUSPENDED, STOPPED
}

abstract class Instance(val bpmnModelInstance: BpmnModelInstance, val processManager: ProcessManager) {

    fun execute() {
        val process = bpmnModelInstance.getModelElementsByType(Process::class.java).first()
        println("process start... ${process.name}")
        execute(process.initialFlowElement())
    }

    fun execute(elementId: String) {
        val flowNode = bpmnModelInstance.getModelElementById(elementId) as? FlowNode ?: throw RuntimeException()
        execute(flowNode)
    }

    fun execute(flowNode: FlowNode) {
        if (getStatus() == ActivityStatus.COMPLETED) {
            throw RuntimeException("instance 가 종료인데 실행 하려고 하지 마라.")
        }
        val behavior = BehaviorFactory.build(flowNode)
        behavior.execute(this)
    }

    abstract fun getStatus(): ActivityStatus
    abstract fun setStatus(status: ActivityStatus)
    abstract fun setActivityStatus(id: String, status: ActivityStatus)
    abstract fun getActivityStatus(id: String): ActivityStatus

    abstract fun addVariable(name: String, value: Any)
    abstract fun setVariables(variables: Map<String, Any>)
    abstract fun getVariables(): Map<String, Any>
}

class StatefulInstance(
    bpmnModelInstance: BpmnModelInstance,
    processManager: ProcessManager
) : Instance(bpmnModelInstance, processManager) {

    override fun getStatus(): ActivityStatus {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setStatus(status: ActivityStatus) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setActivityStatus(id: String, status: ActivityStatus) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityStatus(id: String): ActivityStatus {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun addVariable(name: String, value: Any) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setVariables(variables: Map<String, Any>) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getVariables(): Map<String, Any> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}

class StatelessInstance(
    bpmnModelInstance: BpmnModelInstance,
    processManager: ProcessManager
) : Instance(bpmnModelInstance, processManager) {
    private var status = ActivityStatus.READY
    val activityStatus = mutableMapOf<String, ActivityStatus>()
    private val variables = mutableMapOf<String, Any>()

    override fun getStatus(): ActivityStatus = status

    override fun setStatus(status: ActivityStatus) {
        if (this.status != status) {
            println("instance status change : ${this.status} -> $status ")
        }
        this.status = status
    }

    override fun setActivityStatus(id: String, status: ActivityStatus) {
        activityStatus[id] = status
        if (status == ActivityStatus.RUNNING) {
            setStatus(status)
        }
    }

    override fun getActivityStatus(id: String): ActivityStatus {
        return activityStatus[id] ?: ActivityStatus.READY
    }

    override fun addVariable(name: String, value: Any) {
        variables[name] = value
    }

    override fun setVariables(variables: Map<String, Any>) {
        this.variables.clear()
        this.variables.putAll(variables)
    }

    override fun getVariables(): Map<String, Any> = variables
}
