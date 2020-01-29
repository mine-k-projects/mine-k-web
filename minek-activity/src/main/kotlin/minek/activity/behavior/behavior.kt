package minek.activity.behavior

import minek.activity.instance.ActivityStatus
import minek.activity.instance.Instance
import org.camunda.bpm.model.bpmn.instance.*

object BehaviorFactory {
    fun build(flowNode: FlowNode): Behavior {
        return when (flowNode) {
            is StartEvent -> StartEventBehavior(flowNode)
            is EndEvent -> EndEventBehavior(flowNode)
            is UserTask -> UserTaskBehavior(flowNode)
            is ParallelGateway -> ParallelGatewayBehavior(flowNode)
            is ExclusiveGateway -> ExclusiveGatewayBehavior(flowNode)
            else -> FlowNodeActivityBehavior(flowNode)
        }
    }
}

interface Behavior {
    fun execute(instance: Instance)
    fun leave(instance: Instance)
    fun fireComplete(instance: Instance)
}

open class FlowNodeActivityBehavior(open val flowNode: FlowNode) : Behavior {

    override fun execute(instance: Instance) {
        println("execute... ${flowNode::class.simpleName}(${flowNode.id})")
        instance.setActivityStatus(flowNode.id, ActivityStatus.RUNNING)
        leave(instance)
    }

    override fun leave(instance: Instance) {
        fireComplete(instance)

        flowNode.outgoing
            .forEach { instance.execute(it.target) }
    }

    override fun fireComplete(instance: Instance) {
        println("fireComplete... ${flowNode::class.simpleName}(${flowNode.id})")
        instance.setActivityStatus(flowNode.id, ActivityStatus.COMPLETED)
    }
}

class StartEventBehavior(flowNode: StartEvent) : FlowNodeActivityBehavior(flowNode)

class EndEventBehavior(flowNode: EndEvent) : FlowNodeActivityBehavior(flowNode) {

    override fun leave(instance: Instance) {
        fireComplete(instance)
    }

    override fun fireComplete(instance: Instance) {
        super.fireComplete(instance)
        instance.setStatus(ActivityStatus.COMPLETED)
        println("process end")
    }
}

class UserTaskBehavior(flowNode: UserTask) : FlowNodeActivityBehavior(flowNode) {
    override fun execute(instance: Instance) {
        instance.setActivityStatus(flowNode.id, ActivityStatus.RUNNING)

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
            instance.setStatus(ActivityStatus.STOPPED)
        }
    }
}

abstract class GatewayActivityBehavior(flowNode: FlowNode) : FlowNodeActivityBehavior(flowNode)

class ParallelGatewayBehavior(override val flowNode: ParallelGateway) : GatewayActivityBehavior(flowNode) {
    override fun execute(instance: Instance) {
        instance.setActivityStatus(flowNode.id, ActivityStatus.RUNNING)

        val isPrevActivityCompleted = flowNode.incoming
            .map { it.source }
            .filter {
                instance.getActivityStatus(it!!.id) != ActivityStatus.COMPLETED
            }
            .count() == 0

        if (isPrevActivityCompleted) {
            leave(instance)
        }
    }
}

class ExclusiveGatewayBehavior(override val flowNode: ExclusiveGateway) : GatewayActivityBehavior(flowNode) {
    override fun leave(instance: Instance) {
        fireComplete(instance)

        val defaultSequenceFlow = flowNode.default
        val outgoingSequenceFlow = flowNode.outgoing
            .firstOrNull {
                instance.processManager.expressionManager.evaluate(instance, it.conditionExpression.textContent!!)
            }

        when {
            outgoingSequenceFlow != null -> instance.execute(outgoingSequenceFlow.target)
            defaultSequenceFlow != null -> instance.execute(defaultSequenceFlow.target)
            else -> throw RuntimeException("No outgoing sequence flow of the exclusive gateway")
        }
    }
}
