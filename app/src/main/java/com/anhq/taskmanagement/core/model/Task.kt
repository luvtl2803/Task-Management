package com.anhq.taskmanagement.core.model

import com.anhq.taskmanagement.core.database.model.TaskEntity

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val timeInMills: Long?
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    description = description,
    timeInMills = timeInMills
)

fun Task.toTaskEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    timeInMills = timeInMills
)

fun List<TaskEntity>.mapToTasks() = map { it.toTask() }
fun TaskEntity.mapToTask() = toTask()

