package com.codetron.studentplanner.data.model

data class Task(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var image: String? = null,
    var date: String? = null,
    var priority: Int? = null,
) {

    companion object {

        fun toMapTask(task: Task): Map<String, Any?> {
            val mapTask = HashMap<String, Any?>()
            mapTask["id"] = task.id
            mapTask["title"] = task.title
            mapTask["description"] = task.description
            mapTask["image"] = task.image
            mapTask["date"] = task.date
            mapTask["priority"] = task.priority
            return mapTask
        }
    }


}