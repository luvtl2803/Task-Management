package com.anhq.todolist.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anhq.todolist.core.network.model.NetworkTask

@Entity(tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val time: String?,
    val date: String?
)

fun NetworkTask.toTaskEntity() = TaskEntity(
    id = id,
    name = name,
    description = description,
    time = time,
    date = date
)
