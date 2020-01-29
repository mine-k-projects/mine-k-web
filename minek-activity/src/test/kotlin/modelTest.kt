import minek.activity.ProcessManager
import minek.activity.behavior.BehaviorFactory
import minek.activity.behavior.UserTaskBehavior
import minek.activity.expression.JexlExpressionManager
import minek.activity.extension.behavior
import minek.activity.instance.StatelessInstance
import org.camunda.bpm.model.bpmn.Bpmn
import org.camunda.bpm.model.bpmn.instance.UserTask
import org.junit.jupiter.api.Test

class ModelTest {

    val processManager = ProcessManager(JexlExpressionManager())

    @Test
    fun basicTest() {
        val bpmnModelInstance = Bpmn.createProcess()
            .name("테스트 프로세스")
            .executable()
            .startEvent()
            .userTask()
            .id("userTask1")
            .endEvent()
            .done()

        val instance = StatelessInstance(bpmnModelInstance, processManager)
        instance.execute()

        println("after...")

        val userTask = bpmnModelInstance.getModelElementById<UserTask>("userTask1")

        val behavior = BehaviorFactory.build(userTask) as UserTaskBehavior
        behavior.completeTask(instance)
    }

    @Test
    fun parallelGatewayTest() {
        val bpmnModelInstance = Bpmn.createProcess()
            .startEvent()
            .userTask().id("userTask1")
            .parallelGateway("fork")
            .serviceTask()
            .parallelGateway("join")
            .moveToNode("fork")
            .userTask().id("userTask2")
            .connectTo("join")
            .moveToNode("fork")
            .userTask().id("userTask3")
            .connectTo("join")
            .endEvent()
            .done()

        val instance = StatelessInstance(bpmnModelInstance, processManager)
        instance.execute()
    }

    @Test
    fun exclusiveGatewayTest() {
        val bpmnModelInstance = Bpmn.createProcess()
            .startEvent()
            .userTask().id("userTask1")
            .exclusiveGateway()
            .name("Everything fine?")
            .condition("yes", "foo == 1")
            .serviceTask()
            .userTask().id("userTask2")
            .endEvent()
            .moveToLastGateway()
            .condition("no", "foo == 2")
            .userTask().id("userTask3")
            .connectTo("userTask1")
            .done()

        val instance = StatelessInstance(bpmnModelInstance, processManager)
        instance.addVariable("foo", 1)
        instance.execute()

        println("after...")

        (bpmnModelInstance.getModelElementById<UserTask>("userTask1").behavior() as UserTaskBehavior).completeTask(
            instance
        )

        println("after...")

        (bpmnModelInstance.getModelElementById<UserTask>("userTask2").behavior() as UserTaskBehavior).completeTask(
            instance
        )
    }
}
