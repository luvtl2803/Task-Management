package com.anhq.todolist.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anhq.todolist.core.database.dao.TaskDao
import com.anhq.todolist.core.database.model.TaskEntity

@Database(
    entities = [
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}