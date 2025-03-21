package com.anhq.todolist.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anhq.todolist.core.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM task_entity")
    fun getTask(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTasks(data: List<TaskEntity>)

    @Query("DELETE FROM task_entity")
    suspend fun clear()

    @Update
    suspend fun updateTasks(tasks: List<TaskEntity>)
}