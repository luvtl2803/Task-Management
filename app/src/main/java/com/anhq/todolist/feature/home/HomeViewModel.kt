package com.anhq.todolist.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhq.todolist.core.data.repository.TaskRepository
import com.anhq.todolist.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Math.random
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private  val taskRepository: TaskRepository
) : ViewModel() {

    val tasks = taskRepository.getTask().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun clearAllTask() {
        viewModelScope.launch {
            taskRepository.clearAllTask()
        }
    }
}