package com.anhq.taskmanagement.core.data.repository

import android.util.Log
import com.anhq.taskmanagement.core.database.dao.TaskDao
import com.anhq.taskmanagement.core.database.model.toTaskEntity
import com.anhq.taskmanagement.core.model.Task
import com.anhq.taskmanagement.core.model.mapToTask
import com.anhq.taskmanagement.core.model.mapToTasks
import com.anhq.taskmanagement.core.model.toTaskEntity
import com.anhq.taskmanagement.core.network.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao, private val networkDatasource: NetworkDataSource
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasks().map { it.mapToTasks() }
    }

    override fun getTaskById(taskId: Int): Flow<Task> {
        return taskDao.getTaskById(taskId).map { it.mapToTask() }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.updateTask(task.toTaskEntity())
        }
    }

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.insertTask(task.toTaskEntity())
        }
    }

    override suspend fun deleteTaskById(taskId: Int) {
        withContext(Dispatchers.IO) {
            taskDao.deleteTaskById(taskId)
        }
    }

    override suspend fun clearAllTask() {
        withContext(Dispatchers.IO) {
            taskDao.clear()
        }
    }

    override suspend fun sync(): Boolean {
        return try {
            val remoteTasks = networkDatasource.getTasks().map { it.toTaskEntity() }
            val localTasks = taskDao.getTasks().first()
            val tasksToInsertOrUpdate = remoteTasks.filter { remoteTask ->
                localTasks.none { localTask -> localTask.id == remoteTask.id }
            }
            taskDao.addTasks(tasksToInsertOrUpdate)
            taskDao.updateTasks(remoteTasks)

            true
        } catch (e: Exception) {
            Log.d(javaClass.name, e.stackTraceToString())
            false
        }
    }



}