package minek.activity.model

abstract class Gateway : FlowNode() {

}

/**
 * XOR
 */
class ExclusiveGateway : Gateway() {

}

/**
 * AND
 */
class ParallelGateway : Gateway() {

}

/**
 * OR
 */
class InclusiveGateway : Gateway() {

}