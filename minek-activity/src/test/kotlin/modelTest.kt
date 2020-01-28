import minek.activity.StatelessInstance
import minek.activity.behavior.UserTaskBehavior
import minek.activity.model.*
import org.junit.jupiter.api.Test

class ModelTest {

    @Test
    fun basicTest() {
        val startEvent = StartEvent()
        val userTask = UserTask()
        val endEvent = EndEvent()

        SequenceFlow.of(startEvent, userTask)
        SequenceFlow.of(userTask, endEvent)

        val process = Process().apply {
            name = "테스트 프로세스"
        }
        process.addFlowElement(startEvent)
        process.addFlowElement(userTask)
        process.addFlowElement(endEvent)

        val instance = StatelessInstance(process)
        instance.execute()

        println("after...")

        val behavior = userTask.behavior as UserTaskBehavior
        behavior.completeTask(instance)
    }

    @Test
    fun parallelGatewayTest() {
        val startEvent = StartEvent()
        val userTask1 = UserTask()
        val userTask2 = UserTask()
        val gateway1 = ParallelGateway()
        val gateway2 = ParallelGateway()
        val endEvent = EndEvent()

        SequenceFlow.of(startEvent, gateway1)
        SequenceFlow.of(gateway1, userTask1)
        SequenceFlow.of(gateway1, userTask2)
        SequenceFlow.of(userTask1, gateway2)
        SequenceFlow.of(userTask2, gateway2)
        SequenceFlow.of(gateway2, endEvent)

        val process = Process().apply {
            name = "테스트 프로세스"
        }
        process.addFlowElement(startEvent)
        process.addFlowElement(userTask1)
        process.addFlowElement(userTask2)
        process.addFlowElement(gateway1)
        process.addFlowElement(gateway2)
        process.addFlowElement(endEvent)

        val instance = StatelessInstance(process)
        instance.execute()

        println("after...")

        val behavior1 = userTask1.behavior as UserTaskBehavior
        behavior1.completeTask(instance)

        println("after...")

        val behavior2 = userTask2.behavior as UserTaskBehavior
        behavior2.completeTask(instance)
    }

}