package com.anhq.taskmanagement.feature.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anhq.taskmanagement.core.data.repository.TaskRepository
import com.anhq.taskmanagement.core.model.Task
import com.anhq.taskmanagement.feature.edittask.navigation.EditTaskRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id = savedStateHandle.toRoute<EditTaskRoute>().id

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _isShowTimePicker = MutableStateFlow(false)
    val isShowTimePicker: StateFlow<Boolean> = _isShowTimePicker.asStateFlow()

    private val _eventId = MutableStateFlow<Long?>(null)
    val eventId: StateFlow<Long?> = _eventId.asStateFlow()


    val task = taskRepository.getTaskById(id).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Task(0, "", "", "00:00", "01/01/2010")
    )

    fun updateTask() {

    }

    fun updateEventId(newEventId: Long?) {
        _eventId.value = newEventId
    }

    fun onTitleChange(title: String) {
        _title.value = title
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onTimeChange(time: String) {
        _time.value = time
    }

    fun onDateChange(date: String) {
        _date.value = date
    }

    fun onShowTimePicker(isShowTimePicker: Boolean) {
        _isShowTimePicker.value = isShowTimePicker
    }

    fun save() {
        viewModelScope.launch {
            val task = Task(
                id = 0,
                title = title.value,
                description = description.value,
                time = time.value,
                date = date.value
            )
            taskRepository.insertTask(task)
        }
    }
}