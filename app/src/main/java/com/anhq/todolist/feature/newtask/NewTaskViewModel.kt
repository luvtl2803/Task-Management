package com.anhq.todolist.feature.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhq.todolist.core.data.repository.TaskRepository
import com.anhq.todolist.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Math.random
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name
    private val _content = MutableStateFlow("")
    var description: StateFlow<String> = _content
    private val _time = MutableStateFlow("")
    var time: StateFlow<String> = _time
    private val _date = MutableStateFlow("")
    var date: StateFlow<String> = _date
    private val _isShowTimePicker = MutableStateFlow(false)
    var isShowTimePicker: StateFlow<Boolean> = _isShowTimePicker

    fun onTitleChange(title: String) {
        _name.value = title
    }
    fun onContentChange(content: String) {
        _content.value = content
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
                id = random().toString(), name = name.value, description = description.value, time = time.value, date = date.value
            )
            taskRepository.insertTask(task)
        }
    }
}