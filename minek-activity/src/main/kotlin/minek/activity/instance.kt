package minek.activity

import minek.activity.behavior.BehaviorFactory
import minek.activity.model.FlowNode
import minek.activity.model.Process
import java.util.*

enum class ActivityStatus {
    READY, RUNNING, COMPLETED, SUSPENDED, STOPPED
}

abstract class Instance(val process: Process) {
    fun execute() {
        println("process start... ${process.name}")

        val flowNode = process.initialFlowElement() as FlowNode
        execute(flowNode.id)
    }

    fun execute(elementId: UUID) {
        if (status() == ActivityStatus.COMPLETED) {
            throw RuntimeException("instance 가 종료인데 실행 하려고 하지 마라.")
        }
        val flowNode: FlowNode = process.getFlowElement(elementId) as? FlowNode ?: throw RuntimeException()
        val behavior = BehaviorFactory.build(flowNode)
        behavior.execute(this)
    }

    abstract fun status(): ActivityStatus
    abstract fun status(status: ActivityStatus)
    abstract fun activityStatus(id: UUID, status: ActivityStatus)
    abstract fun activityStatus(id: UUID): ActivityStatus
}

class StatefulInstance(process: Process) : Instance(process) {

    override fun status(): ActivityStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun status(status: ActivityStatus) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activityStatus(id: UUID, status: ActivityStatus) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activityStatus(id: UUID): ActivityStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class StatelessInstance(process: Process) : Instance(process) {
    private var status = ActivityStatus.READY
    val activityStatus = mutableMapOf<UUID, ActivityStatus>()

    override fun status(): ActivityStatus = status

    override fun status(status: ActivityStatus) {
        if (this.status != status) {
            println("instance status change : ${this.status} -> $status ")
        }
        this.status = status
    }

    override fun activityStatus(id: UUID, status: ActivityStatus) {
        activityStatus[id] = status
        if (status == ActivityStatus.RUNNING) {
            status(status)
        }
    }

    override fun activityStatus(id: UUID): ActivityStatus = activityStatus[id]!!
}