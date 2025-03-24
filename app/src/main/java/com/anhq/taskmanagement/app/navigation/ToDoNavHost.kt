package com.anhq.taskmanagement.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.anhq.taskmanagement.app.ui.ToDoAppState
import com.anhq.taskmanagement.feature.edittask.navigation.editTaskScreen
import com.anhq.taskmanagement.feature.home.navigation.HomeRoute
import com.anhq.taskmanagement.feature.home.navigation.homeScreen
import com.anhq.taskmanagement.feature.newtask.navigation.newTaskScreen

@Composable
fun ToDoNavHost(
    appState: ToDoAppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }

    ) {
        homeScreen(
            navController = navController
        )
        newTaskScreen(
            onBackHomeClick = { appState.navigateToTopLevelDestination(TopLevelDestination.HOME) },
        )
        editTaskScreen(
            onBackHomeClick = { appState.navigateToTopLevelDestination(TopLevelDestination.HOME) }
        )
    }
}