package minek.activity.behavior

import minek.activity.ActivityStatus
import minek.activity.Instance
import minek.activity.model.*

object BehaviorFactory {
    fun build(flowNode: FlowNode): Behavior {
        if (flowNode.behavior != null) {
            return flowNode.behavior!!
        }
        val behavior: Behavior = when (flowNode) {
            is StartEvent -> StartEventBehavior(flowNode)
            is EndEvent -> EndEventBehavior(flowNode)
            is UserTask -> UserTaskBehavior(flowNode)
            is ParallelGateway -> ParallelGatewayBehavior(flowNode)
            else -> FlowNodeActivityBehavior(flowNode)
        }
        flowNode.behavior = behavior
        return behavior
    }
}

interface Behavior {
    fun execute(instance: Instance)
    fun leave(instance: Instance)
    fun fireComplete(instance: Instance)
}

open class FlowNodeActivityBehavior(val flowNode: FlowNode) : Behavior {

    override fun execute(instance: Instance) {
        println("execute... ${flowNode::class.simpleName}(${flowNode.id})")
        instance.activityStatus(flowNode.id, ActivityStatus.RUNNING)
        leave(instance)
    }

    override fun leave(instance: Instance) {
        fireComplete(instance)

        flowNode.outgoingFlows
            .forEach { instance.execute(it.targetRef) }
    }

    override fun fireComplete(instance: Instance) {
        println("fireComplete... ${flowNode::class.simpleName}(${flowNode.id})")
        instance.activityStatus(flowNode.id, ActivityStatus.COMPLETED)
    }
}

class StartEventBehavior(flowNode: StartEvent) : FlowNodeActivityBehavior(flowNode)

class EndEventBehavior(flowNode: EndEvent) : FlowNodeActivityBehavior(flowNode) {

    override fun leave(instance: Instance) {
        fireComplete(instance)
    }

    override fun fireComplete(instance: Instance) {
        super.fireComplete(instance)
        instance.status(ActivityStatus.COMPLETED)
        println("process end")
    }
}

class UserTaskBehavior(flowNode: UserTask) : FlowNodeActivityBehavior(flowNode) {
    override fun execute(instance: Instance) {
        instance.activityStatus(flowNode.id, ActivityStatus.RUNNING)

        newTask(instance)
    }

    private fun newTask(instance: Instance) {
        println("new task... ${flowNode::class.simpleName}(${flowNode.id})")
    }

    fun completeTask(instance: Instance, isBreak: Boolean = false) {
        println("complete task... ${flowNode::class.simpleName}(${flowNode.id})")

        if (!isBreak) {
            leave(instance)
        } else {
            // 강제 종료
            fireComplete(instance)
            instance.status(ActivityStatus.COMPLETED)
        }
    }
}

abstract class GatewayActivityBehavior(flowNode: FlowNode) : FlowNodeActivityBehavior(flowNode)

class ParallelGatewayBehavior(flowNode: FlowNode) : GatewayActivityBehavior(flowNode) {
    override fun execute(instance: Instance) {
        instance.activityStatus(flowNode.id, ActivityStatus.RUNNING)

        val isPrevActivityCompleted = flowNode.incomingFlows
            .map { instance.process.getFlowElement(it.sourceRef) }
            .filter { instance.activityStatus(it!!.id) != ActivityStatus.COMPLETED }
            .count() == 0

        if (isPrevActivityCompleted) {
            leave(instance)
        }
    }
}

class ExclusiveGatewayBehavior(flowNode: FlowNode) : GatewayActivityBehavior(flowNode) {

}