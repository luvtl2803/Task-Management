package com.anhq.taskmanagement.feature.edittask.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anhq.taskmanagement.feature.edittask.EditTaskRoute
import kotlinx.serialization.Serializable

@Serializable
data class EditTaskRoute(
    val id: Int
)

fun NavController.navigateToEditTask(id: Int) {
    val route = EditTaskRoute(id)
    navigate(
        route = route
    )
}

fun NavGraphBuilder.editTaskScreen(
    onBackHomeClick: () -> Unit
) {
    composable<EditTaskRoute> {
        EditTaskRoute(
            onBackHomeClick = onBackHomeClick
        )
    }
}