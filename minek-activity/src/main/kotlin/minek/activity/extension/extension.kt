package minek.activity.extension

import minek.activity.behavior.Behavior
import minek.activity.behavior.BehaviorFactory
import org.camunda.bpm.model.bpmn.instance.FlowNode
import org.camunda.bpm.model.bpmn.instance.Process
import org.camunda.bpm.model.bpmn.instance.StartEvent

fun Process.initialFlowElement(): StartEvent = this.getChildElementsByType(StartEvent::class.java).first()
fun FlowNode.behavior(): Behavior = BehaviorFactory.build(this)
