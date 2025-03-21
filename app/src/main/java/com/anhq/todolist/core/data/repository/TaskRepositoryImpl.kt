package com.anhq.todolist.core.data.repository

import android.util.Log
import com.anhq.todolist.core.database.dao.TaskDao
import com.anhq.todolist.core.database.model.toTaskEntity
import com.anhq.todolist.core.model.Task
import com.anhq.todolist.core.model.mapToTask
import com.anhq.todolist.core.model.toTaskEntity
import com.anhq.todolist.core.network.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao, private val networkDatasource: NetworkDataSource
) : TaskRepository {

    override fun getTask(): Flow<List<Task>> {
        return taskDao.getTask().map { it.mapToTask() }
    }

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.insertTask(task.toTaskEntity())
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
            val localTasks = taskDao.getTask().first()
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