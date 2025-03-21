package com.anhq.todolist.feature.newtask

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhq.todolist.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewTaskRoute(
    onBackHomeClick: () -> Unit,
    viewModel: NewTaskViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val name by viewModel.name.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()
    val isShowTimePicker by viewModel.isShowTimePicker.collectAsStateWithLifecycle()
    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
    var selectedDate: DatePickerState? by remember { mutableStateOf(null) }

    NewTaskScreen(
        onBackHomeClick = onBackHomeClick,
        onSaveTaskClick = {
            if (name.isNotBlank() && description.isNotBlank()) {
                viewModel.save()

                val permissions =
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
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
                        timeInMillis = selectedDate?.selectedDateMillis!!
                        set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                        set(Calendar.MINUTE, selectedTime!!.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val startInMillis = calendar.timeInMillis
                    val endInMillis = startInMillis + 1 * 60 * 60 * 1000


                    val values = ContentValues().apply {
                        put(CalendarContract.Events.DTSTART, startInMillis)
                        put(CalendarContract.Events.DTEND, endInMillis)
                        put(CalendarContract.Events.TITLE, name)
                        put(CalendarContract.Events.DESCRIPTION, description)
                        put(CalendarContract.Events.CALENDAR_ID, calID)
                        put(CalendarContract.Events.HAS_ALARM, true)
                        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                    }
                    val uri: Uri? =
                        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

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

                        Toast.makeText(
                            context,
                            "Event added successfully $calID",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Title or Description cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        },
        title = name,
        onTitleChange = {
            viewModel.onTitleChange(it)
        },
        content = description, onContentChange = {
            viewModel.onContentChange(it)
        },
        time = time,
        date = date,
        isShowTimePicker = isShowTimePicker,
        onShowTimePicker = {
            viewModel.onShowTimePicker(it)
        },
        onSelectedTime = {
            selectedTime = it
            if (selectedTime != null) {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                    set(Calendar.MINUTE, selectedTime!!.minute)
                }

                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                viewModel.onTimeChange(formatter.format(cal.time))
            } else {
                Toast.makeText(context, "Select time failed !", Toast.LENGTH_SHORT).show()
            }
        },
        onSelectedDate = {
            selectedDate = it
            if (selectedDate != null) {
                val selectedDateMillis = it?.selectedDateMillis

                val calendar = Calendar.getInstance().apply {
                    if (selectedDateMillis != null) {
                        timeInMillis = selectedDateMillis
                    }
                }
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                viewModel.onDateChange("$day/$month/$year")
            } else {
                Toast.makeText(context, "Select date failed !", Toast.LENGTH_SHORT).show()
            }
        })
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
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
        color = Color.LightGray
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackHomeClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Add New Task",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                )
                IconButton(onClick = onSaveTaskClick) {
                    Icon(
                        imageVector = Icons.Filled.Done, contentDescription = "Save"
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = title,
                    label = { Text(text = "Task: ") },
                    onValueChange = { newText ->
                        onTitleChange(newText)
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = content,
                    label = { Text(text = "Description: ") },
                    onValueChange = { newText ->
                        onContentChange(newText)
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = date,
                    label = { Text(text = "Alarm Date: ") },
                    onValueChange = { },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = time,
                    label = { Text(text = "Alarm Time: ") },
                    onValueChange = { },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(modifier = Modifier.padding(10.dp),
                        onClick = { onShowTimePicker(true) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_delete_alarm),
                            contentDescription = "Clear Alarm"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Clear Alarm", style = MaterialTheme.typography.titleSmall)
                    }
                    Button(modifier = Modifier.padding(10.dp),
                        onClick = { onShowTimePicker(true) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_add_alarm_black),
                            contentDescription = "Add Time"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Add Alarm", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
            if (isShowTimePicker) {
                DateTimePickerDialog(
                    onSelectedTime = {
                        onSelectedTime(it)
                        onShowTimePicker(false)
                    },
                    onSelectedDate = {
                        onSelectedDate(it)
                    },
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
    var isDateSelected by remember { mutableStateOf(true) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTime.timeInMillis,
        selectableDates = PresentSelectableDates
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
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isDateSelected) {
                DatePicker(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(color = Color.White),
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.White
                    ),
                    title = {
                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = "Select Date",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
                Button(onClick = {
                    isDateSelected = false
                    onSelectedDate(datePickerState)
                }) {
                    Text("Select Date", style = MaterialTheme.typography.titleSmall)
                }
            } else {
                Text(
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.Start),
                    text = "Select time",
                    style = MaterialTheme.typography.titleMedium
                )
                TimeInput(
                    modifier = Modifier
                        .padding(vertical = 20.dp), state = timePickerState
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = onDismiss) {
                        Text("Dismiss", style = MaterialTheme.typography.titleSmall)
                    }

                    Button(onClick = {
                        onSelectedTime(timePickerState)
                        onDismiss()
                    }) {
                        Text("Confirm", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
    var isShowTimePicker by remember { mutableStateOf(false) }

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

