package com.anhq.taskmanagement.core.model

import com.anhq.taskmanagement.core.database.model.TaskEntity

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val time: String?,
    val date: String?
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    description = description,
    time = time,
    date = date
)

fun Task.toTaskEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    time = time,
    date = date
)

fun List<TaskEntity>.mapToTasks() = map { it.toTask() }
fun TaskEntity.mapToTask() = toTask()

