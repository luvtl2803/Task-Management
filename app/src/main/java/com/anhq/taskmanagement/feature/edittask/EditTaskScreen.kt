package com.anhq.taskmanagement.feature.edittask

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhq.taskmanagement.R
import com.anhq.taskmanagement.core.designsystem.theme.body2
import com.anhq.taskmanagement.core.designsystem.theme.body3_medium
import com.anhq.taskmanagement.core.help.PermissionHelper
import java.util.Calendar
import java.util.TimeZone

@Composable
internal fun EditTaskRoute(
    onBackHomeClick: () -> Unit,
    viewModel: EditTaskViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val title by viewModel.title.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()
    val timeInMills by viewModel.timeInMills.collectAsStateWithLifecycle()
    val isShowTimePicker by viewModel.isShowTimePicker.collectAsStateWithLifecycle()
    val eventId by viewModel.eventId.collectAsStateWithLifecycle()

    EditTaskScreen(
        onBackHomeClick = onBackHomeClick,
        onUpdateTaskClick = {
            if (title.isNotBlank() && description.isNotBlank()) {
                viewModel.updateTask()
                if (!PermissionHelper.hasCalendarPermissions(context)) {
                    PermissionHelper.requestCalendarPermissions(context as Activity)
                } else {
                    updateCalendarEvent(
                        context = context,
                        title = title,
                        description = description,
                        timeInMills = timeInMills,
                        eventId = eventId,
                        onEventIdUpdated = { newEventId -> viewModel.updateEventId(newEventId) }
                    )
                }
            } else {
                Toast.makeText(context, "Title or Description cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        },
        title = title,
        onTitleChange = { viewModel.onTitleChange(it) },
        description = description,
        onContentChange = { viewModel.onDescriptionChange(it) },
        time = time,
        date = date,
        timeInMills = timeInMills,
        onTimeInMillsChange = { viewModel.onTimeInMillsChange(it) },
        onClearAlarmClick = { viewModel.clearAlarm() },
        isShowPickerDialog = isShowTimePicker,
        onShowTimePicker = { viewModel.onShowTimePicker(it) }
    )
}

private fun updateCalendarEvent(
    context: Context,
    title: String,
    description: String,
    timeInMills: Long,
    eventId: Long?,
    onEventIdUpdated: (Long?) -> Unit
) {
    val contentResolver = context.contentResolver

    eventId?.let { id ->
        contentResolver.delete(
            CalendarContract.Events.CONTENT_URI,
            "${CalendarContract.Events._ID}=?",
            arrayOf(id.toString())
        )
        onEventIdUpdated(null)
    }

    if (timeInMills <= 0) {
        Toast.makeText(context, "Calendar event removed", Toast.LENGTH_SHORT).show()
        return
    }

    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeInMills
    }
    val startInMillis = calendar.timeInMillis
    val endInMillis = startInMillis + 1 * 60 * 60 * 1000

    val values = ContentValues().apply {
        put(CalendarContract.Events.DTSTART, startInMillis)
        put(CalendarContract.Events.DTEND, endInMillis)
        put(CalendarContract.Events.TITLE, title)
        put(CalendarContract.Events.DESCRIPTION, description)
        put(CalendarContract.Events.CALENDAR_ID, 1L)
        put(CalendarContract.Events.HAS_ALARM, true)
        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }

    val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    if (uri != null) {
        val newEventId = uri.lastPathSegment?.toLongOrNull()
        onEventIdUpdated(newEventId)
        val reminderValues = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, newEventId)
            put(CalendarContract.Reminders.MINUTES, 10)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
        Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Failed to update calendar event", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun EditTaskScreen(
    onBackHomeClick: () -> Unit,
    onUpdateTaskClick: () -> Unit,
    onClearAlarmClick: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onContentChange: (String) -> Unit,
    time: String,
    date: String,
    timeInMills: Long,
    onTimeInMillsChange: (Long) -> Unit,
    isShowPickerDialog: Boolean,
    onShowTimePicker: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackHomeClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back_black),
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Edit Task",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onUpdateTaskClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = "Update"
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = title,
                    label = { Text("Title: ") },
                    onValueChange = onTitleChange
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = description,
                    label = { Text("Description: ") },
                    onValueChange = onContentChange
                )

                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = { onShowTimePicker(true) }
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_add_alarm_black),
                        contentDescription = "Add Time"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Alarm", style = MaterialTheme.typography.titleSmall)
                }

                if (date.isNotBlank() && time.isNotBlank()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.width(200.dp),
                                value = date,
                                label = { Text("Alarm Date: ") },
                                maxLines = 1,
                                onValueChange = {},
                                enabled = false
                            )
                            OutlinedTextField(
                                modifier = Modifier.width(150.dp),
                                value = time,
                                label = { Text("Alarm Time: ") },
                                maxLines = 1,
                                onValueChange = {},
                                enabled = false
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                modifier = Modifier,
                                onClick = {
                                    onClearAlarmClick()
                                    onShowTimePicker(false)
                                }
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.ic_delete_alarm),
                                    contentDescription = "Clear Alarm"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Clear Alarm", style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }

            if (isShowPickerDialog) {
                DateTimePickerDialog(
                    initialTimeInMillis = timeInMills,
                    onSelectedTimeInMillis = onTimeInMillsChange,
                    onDismiss = { onShowTimePicker(false) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialTimeInMillis: Long,
    onSelectedTimeInMillis: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val currentTime = Calendar.getInstance().apply {
        timeInMillis = if (initialTimeInMillis > 0) initialTimeInMillis else calendar.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (initialTimeInMillis > 0) initialTimeInMillis else calendar.timeInMillis,
        selectableDates = PresentSelectableDates,
        initialDisplayMode = DisplayMode.Input
    )
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            }
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(20.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Date and Time",
                color = Color.White,
                style = MaterialTheme.typography.body2
            )

            DatePicker(
                modifier = Modifier,
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.DarkGray,
                    headlineContentColor = Color.White,
                    titleContentColor = Color.White,
                    dateTextFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.LightGray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray
                    )
                ),
                title = {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "Date:",
                        style = MaterialTheme.typography.body3_medium
                    )
                }
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp),
                text = "Time:",
                color = Color.White,
                style = MaterialTheme.typography.body3_medium
            )
            TimeInput(
                state = timePickerState
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = onDismiss) {
                    Text("Dismiss", style = MaterialTheme.typography.titleSmall)
                }
                Button(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: return@Button
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = selectedDateMillis
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onSelectedTimeInMillis(calendar.timeInMillis)
                    onDismiss()
                }) {
                    Text("Confirm", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object PresentSelectableDates : SelectableDates {
    private val currentCalendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= currentCalendar.timeInMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= currentCalendar.get(Calendar.YEAR)
    }
}

@Preview
@Composable
fun PreviewEditTaskScreen() {
    var title by remember { mutableStateOf("My Task") }
    var content by remember { mutableStateOf("Task content here") }
    var time by remember { mutableStateOf("1") }
    var date by remember { mutableStateOf("1") }
    var isShowTimePicker by remember { mutableStateOf(false) }

    EditTaskScreen(
        onBackHomeClick = {  },
        onUpdateTaskClick = { },
        onClearAlarmClick = { time = ""; date = "" },
        title = title,
        onTitleChange = { title = it },
        description = content,
        onContentChange = { content = it },
        time = time,
        date = date,
        timeInMills = 0L,
        onTimeInMillsChange = {},
        isShowPickerDialog = isShowTimePicker,
        onShowTimePicker = { isShowTimePicker = it }
    )
}