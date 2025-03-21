package com.anhq.todolist.core.model

import com.anhq.todolist.core.database.model.TaskEntity

data class Task(
    val id: String,
    val name: String,
    val description: String,
    val time: String?,
    val date: String?
)

fun TaskEntity.toTask() = Task(
    id = id,
    name = name,
    description = description,
    time = time,
    date = date
)

fun Task.toTaskEntity() = TaskEntity(
    id = id,
    name = name,
    description = description,
    time = time,
    date = date
)

fun List<TaskEntity>.mapToTask() = map { it.toTask() }
