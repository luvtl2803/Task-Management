package com.anhq.taskmanagement.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anhq.taskmanagement.core.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)
    @Query("DELETE FROM task_entity WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
    @Query("SELECT * FROM task_entity WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<TaskEntity>
    @Query("SELECT * FROM task_entity")
    fun getTasks(): Flow<List<TaskEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTasks(data: List<TaskEntity>)
    @Query("DELETE FROM task_entity")
    suspend fun clear()
    @Update
    suspend fun updateTasks(task: List<TaskEntity>)
    @Update
    suspend fun updateTask(task: TaskEntity)
}