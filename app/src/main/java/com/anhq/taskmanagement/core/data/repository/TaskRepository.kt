package com.anhq.taskmanagement.core.data.repository

import com.anhq.taskmanagement.core.model.Task
import com.anhq.taskmanagement.core.sync.Syncable
import kotlinx.coroutines.flow.Flow

interface TaskRepository : Syncable {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(taskId: Int): Flow<Task>
    suspend fun deleteTaskById(taskId: Int)
    suspend fun insertTask(task: Task)
    suspend fun clearAllTask()
}