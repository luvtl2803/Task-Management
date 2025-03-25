package com.anhq.taskmanagement.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anhq.taskmanagement.core.network.model.NetworkTask

@Entity(tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val timeInMills: Long?
)

fun NetworkTask.toTaskEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    timeInMills = timeInMills,
)
