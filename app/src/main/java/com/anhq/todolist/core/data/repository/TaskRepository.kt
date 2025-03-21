package com.anhq.todolist.core.data.repository

import com.anhq.todolist.core.model.Task
import com.anhq.todolist.core.sync.Syncable
import kotlinx.coroutines.flow.Flow

interface TaskRepository : Syncable {
    fun getTask(): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun clearAllTask()
}