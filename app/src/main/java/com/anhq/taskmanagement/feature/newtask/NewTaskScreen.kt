package com.anhq.taskmanagement.feature.newtask

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
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
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhq.taskmanagement.R
import com.anhq.taskmanagement.core.designsystem.theme.body2
import com.anhq.taskmanagement.core.designsystem.theme.body3_medium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewTaskRoute(
    onBackHomeClick: () -> Unit,
    viewModel: NewTaskViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val title by viewModel.name.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()
    val isShowTimePicker by viewModel.isShowTimePicker.collectAsStateWithLifecycle()
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<Long?>(null) }

    NewTaskScreen(
        onBackHomeClick = onBackHomeClick,
        onSaveTaskClick = {
            if (title.isNotBlank() && description.isNotBlank()) {
                viewModel.save()

                val permissions = arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CALENDAR
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_CALENDAR
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(context as Activity, permissions, 1001)
                } else {
                    val contentResolver = context.contentResolver
                    val calID: Long = 1

                    val calendar = Calendar.getInstance().apply {
                        if (selectedDate != null) {
                            timeInMillis = selectedDate!!
                        }
                        if (selectedTime != null) {
                            val timeCal =
                                Calendar.getInstance().apply { timeInMillis = selectedTime!! }
                            set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                    }

                    val startInMillis = calendar.timeInMillis
                    val endInMillis = startInMillis + 1 * 60 * 60 * 1000

                    val values = ContentValues().apply {
                        put(CalendarContract.Events.DTSTART, startInMillis)
                        put(CalendarContract.Events.DTEND, endInMillis)
                        put(CalendarContract.Events.TITLE, title)
                        put(CalendarContract.Events.DESCRIPTION, description)
                        put(CalendarContract.Events.CALENDAR_ID, calID)
                        put(CalendarContract.Events.HAS_ALARM, true)
                        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                    }
                    val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

                    if (uri != null) {
                        val eventID = uri.lastPathSegment?.toLongOrNull()
                        val reminderValues = ContentValues().apply {
                            put(CalendarContract.Reminders.EVENT_ID, eventID)
                            put(CalendarContract.Reminders.MINUTES, 10)
                            put(
                                CalendarContract.Reminders.METHOD,
                                CalendarContract.Reminders.METHOD_ALERT
                            )
                        }
                        contentResolver.insert(
                            CalendarContract.Reminders.CONTENT_URI,
                            reminderValues
                        )
                        Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Title or Description cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        },
        title = title,
        onTitleChange = { viewModel.onTitleChange(it) },
        content = description,
        onContentChange = { viewModel.onContentChange(it) },
        time = time,
        date = date,
        isShowTimePicker = isShowTimePicker,
        onShowTimePicker = { viewModel.onShowTimePicker(it) },
        onSelectedTime = { timePickerState ->
            if (timePickerState != null) {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedTime = cal.timeInMillis
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                viewModel.onTimeChange(formatter.format(cal.time))
            }
        },
        onSelectedDate = { datePickerState ->
            if (datePickerState?.selectedDateMillis != null) {
                selectedDate = datePickerState.selectedDateMillis
                val date = Date(selectedDate!!)
                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                viewModel.onDateChange(formatter.format(date))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    onBackHomeClick: () -> Unit,
    onSaveTaskClick: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    time: String,
    date: String,
    isShowTimePicker: Boolean,
    onShowTimePicker: (Boolean) -> Unit,
    onSelectedTime: (TimePickerState?) -> Unit,
    onSelectedDate: (DatePickerState?) -> Unit
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
                    text = "Add New Task",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onSaveTaskClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = "Save"
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
                    value = content,
                    label = { Text("Description: ") },
                    onValueChange = onContentChange
                )
                Spacer(modifier = Modifier.height(20.dp))
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
                        .align(Alignment.End)
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier.padding(10.dp),
                        onClick = { onShowTimePicker(true) }
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_delete_alarm),
                            contentDescription = "Clear Alarm"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Alarm", style = MaterialTheme.typography.titleSmall)
                    }
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
                }
            }
            if (isShowTimePicker) {
                DateTimePickerDialog(
                    onSelectedTime = onSelectedTime,
                    onSelectedDate = onSelectedDate,
                    onDismiss = { onShowTimePicker(false) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    onSelectedTime: (TimePickerState?) -> Unit,
    onSelectedDate: (DatePickerState?) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTime = Calendar.getInstance()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTime.timeInMillis,
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
                style = MaterialTheme.typography.body2
            )

            DatePicker(
                modifier = Modifier,
                state = datePickerState,
                colors = DatePickerDefaults.colors(containerColor = Color.DarkGray),
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
                style = MaterialTheme.typography.body3_medium
            )
            TimeInput(
                state = timePickerState
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = onDismiss) {
                    Text("Dismiss", style = MaterialTheme.typography.titleSmall)
                }
                Button(onClick = {
                    onSelectedDate(datePickerState)
                    onSelectedTime(timePickerState)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewNewTaskScreen() {
    var title by remember { mutableStateOf("My Task") }
    var content by remember { mutableStateOf("Task content here") }
    val time by remember { mutableStateOf("10:30") }
    val date by remember { mutableStateOf("21/03/2025") }
    var isShowTimePicker by remember { mutableStateOf(true) }

    NewTaskScreen(
        onBackHomeClick = { /* Handle back action */ },
        onSaveTaskClick = { /* Handle save task action */ },
        title = title,
        onTitleChange = { title = it },
        content = content,
        onContentChange = { content = it },
        time = time,
        date = date,
        isShowTimePicker = isShowTimePicker,
        onShowTimePicker = { isShowTimePicker = it },
        onSelectedTime = { /* Handle selected time */ },
        onSelectedDate = { /* Handle selected date */ }
    )
}