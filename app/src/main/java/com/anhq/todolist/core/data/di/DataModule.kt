package com.anhq.todolist.core.data.di

import com.anhq.todolist.core.data.repository.TaskRepository
import com.anhq.todolist.core.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun provideTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}