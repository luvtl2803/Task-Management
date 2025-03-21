package com.anhq.todolist.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.anhq.todolist.app.ui.ToDoAppState
import com.anhq.todolist.feature.home.navigation.HomeRoute
import com.anhq.todolist.feature.home.navigation.homeScreen
import com.anhq.todolist.feature.newtask.navigation.newTaskScreen

@Composable
fun ToDoNavHost(
    appState: ToDoAppState,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = HomeRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }

    ) {
        homeScreen(

        )
        newTaskScreen(
            onBackHomeClick = { appState.navigateToTopLevelDestination(TopLevelDestination.HOME) },
        )
    }
}