package com.anhq.taskmanagement.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhq.taskmanagement.core.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private  val taskRepository: TaskRepository
) : ViewModel() {

    val tasks = taskRepository.getTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun deleteTaskById(taskId: Int) {
        viewModelScope.launch {
            taskRepository.deleteTaskById(taskId)
        }
    }

    fun clearAllTask() {
        viewModelScope.launch {
            taskRepository.clearAllTask()
        }
    }
}