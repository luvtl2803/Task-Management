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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id = savedStateHandle.toRoute<EditTaskRoute>().id

    val task = taskRepository.getTaskById(id).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Task(0, "", "", 0L)
    )

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _timeInMills = MutableStateFlow(0L)
    val timeInMills: StateFlow<Long> = _timeInMills.asStateFlow()

    private val _isShowTimePicker = MutableStateFlow(false)
    val isShowTimePicker: StateFlow<Boolean> = _isShowTimePicker.asStateFlow()

    private val _eventId = MutableStateFlow<Long?>(null)
    val eventId: StateFlow<Long?> = _eventId.asStateFlow()

    init {
        viewModelScope.launch {
            task.collect { currentTask ->
                _title.value = currentTask.title
                _description.value = currentTask.description
                _timeInMills.value = currentTask.timeInMills ?: 0L
                if (currentTask.timeInMills != null && currentTask.timeInMills > 0) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = currentTask.timeInMills
                    }
                    _date.value = dateFormatter.format(calendar.time)
                    _time.value = timeFormatter.format(calendar.time)
                } else {
                    _date.value = ""
                    _time.value = ""
                }
            }
        }
    }

    fun updateEventId(newEventId: Long?) {
        _eventId.value = newEventId
    }

    fun onTimeInMillsChange(timeInMills: Long) {
        _timeInMills.value = timeInMills
        val calendar = Calendar.getInstance().apply { this.timeInMillis = timeInMills }
        _date.value = dateFormatter.format(calendar.time)
        _time.value = timeFormatter.format(calendar.time)
    }

    fun onTitleChange(title: String) {
        _title.value = title
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onShowTimePicker(isShowTimePicker: Boolean) {
        _isShowTimePicker.value = isShowTimePicker
    }

    fun clearAlarm() {
        _timeInMills.value = 0L
        _date.value = ""
        _time.value = ""
    }

    fun updateTask() {
        viewModelScope.launch {
            val updatedTask = Task(
                id = id,
                title = title.value,
                description = description.value,
                timeInMills = timeInMills.value
            )
            taskRepository.updateTask(updatedTask)
        }
    }
}