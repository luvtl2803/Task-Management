package com.anhq.taskmanagement.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.anhq.taskmanagement.core.designsystem.theme.body1
import com.anhq.taskmanagement.core.model.Task
import com.anhq.taskmanagement.core.ui.dialog.DeleteConfirmationDialog
import com.anhq.taskmanagement.feature.edittask.navigation.navigateToEditTask

@Composable
fun HomeRoute(
    navController: NavController
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    HomeScreen(
        tasks = tasks,
        onEditTask = { task -> navController.navigateToEditTask(task.id) },
        onDeleteTask = { taskId -> viewModel.deleteTaskById(taskId) }
    )
}

@Composable
fun HomeScreen(
    tasks: List<Task>,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
        color = Color.LightGray
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(tasks) { index, item ->
                    TaskCard(
                        task = item,
                        index = index,
                        onEditTask = onEditTask,
                        onDeleteTask = onDeleteTask
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    index: Int,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${index + 1}. ${task.title}",
                    style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                )
            }

            Text(
                text = task.description,
                style = MaterialTheme.typography.body1.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onEditTask(task) },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(36.dp),
                ) {
                    Text(text = "Edit", fontSize = 14.sp)
                }
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.height(36.dp),
                ) {
                    Text(text = "Delete", fontSize = 14.sp)
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            taskTitle = task.title,
            onConfirm = {
                onDeleteTask(task.id)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Preview
@Composable
private fun HomePreview() {
    val tasks: List<Task> = listOf(
        Task(1, "Buy groceries", "Buy milk, bread, and eggs", 0L),
        Task(2, "Complete project", "Finish the Android app project", 0L)
    )

    HomeScreen(
        tasks = tasks,
        onEditTask = {},
        onDeleteTask = {}
    )
}