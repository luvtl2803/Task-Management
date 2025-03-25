package com.anhq.taskmanagement.feature.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhq.taskmanagement.core.data.repository.TaskRepository
import com.anhq.taskmanagement.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time.asStateFlow()

    private val _timeInMills = MutableStateFlow(0L)
    val timeInMills: StateFlow<Long> = _timeInMills.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _isShowTimePicker = MutableStateFlow(false)
    val isShowTimePicker: StateFlow<Boolean> = _isShowTimePicker.asStateFlow()

    fun onTitleChange(title: String) {
        _name.value = title
    }

    fun onContentChange(content: String) {
        _description.value = content
    }

    fun onTimeInMillsChange(timeInMills: Long) {
        _timeInMills.value = timeInMills
        val calendar = Calendar.getInstance().apply { this.timeInMillis = timeInMills }
        _date.value = dateFormatter.format(calendar.time)
        _time.value = timeFormatter.format(calendar.time)
    }

    fun onShowTimePicker(isShowTimePicker: Boolean) {
        _isShowTimePicker.value = isShowTimePicker
    }

    fun save() {
        viewModelScope.launch {
            val task = Task(
                id = 0,
                title = name.value,
                description = description.value,
                timeInMills = if (_timeInMills.value == 0L) null else _timeInMills.value
            )
            taskRepository.insertTask(task)
        }
    }
}