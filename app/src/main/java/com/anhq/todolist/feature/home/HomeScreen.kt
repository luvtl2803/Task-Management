package com.anhq.todolist.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhq.todolist.core.designsystem.theme.body1
import com.anhq.todolist.core.model.Task

@Composable
fun HomeRoute(

) {
    val viewModel: HomeViewModel = hiltViewModel()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    HomeScreen(
        onNewTaskClick = { viewModel.clearAllTask() }, tasks = tasks
    )
}

@Composable
fun HomeScreen(
    onNewTaskClick: () -> Unit, tasks: List<Task>
) {
    Surface(
        modifier = Modifier.fillMaxSize().padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()), color = Color.LightGray
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(tasks) { item ->
                    TaskCard(task = item)
                }
            }

            Button(
                onClick = onNewTaskClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Create New Task", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = task.description, style = MaterialTheme.typography.body1.copy(fontSize = 16.sp)
            )
        }
    }
}

@Preview
@Composable
private fun HomePreview() {
    val tasks: List<Task> = listOf(
        Task("1", "Buy groceries", "Buy milk, bread, and eggs", "11", "12"),
        Task("2", "Complete project", "Finish the Android app project", "11", "12"),
        Task("3", "Clean the house", "Vacuum and mop the floor", "11", "12"),
        Task("4", "Read a book", "Read Kotlin programming book", "11", "12"),
        Task("5", "Go for a walk", "Walk in the park for 30 minutes", "11", "12")
    )

    HomeScreen(onNewTaskClick = {}, tasks = tasks)
}
