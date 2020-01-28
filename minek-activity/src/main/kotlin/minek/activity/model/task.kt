package minek.activity.model

open class Task : Activity() {

}

class UserTask : Task() {
    var assignee: String? = null
    var owner: String? = null
    val candidateUsers: MutableList<String> = mutableListOf()
    val candidateGroups: MutableList<String> = mutableListOf()
}