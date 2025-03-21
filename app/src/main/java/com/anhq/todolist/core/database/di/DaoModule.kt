package com.anhq.todolist.core.database.di

import com.anhq.todolist.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Provides
    fun providesTaskDao(database: AppDatabase) = database.taskDao()
}