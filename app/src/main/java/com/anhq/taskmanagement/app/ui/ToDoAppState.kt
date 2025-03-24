package com.anhq.taskmanagement.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.anhq.taskmanagement.app.navigation.TopLevelDestination
import com.anhq.taskmanagement.feature.home.navigation.HomeRoute
import com.anhq.taskmanagement.feature.home.navigation.navigateToHome
import com.anhq.taskmanagement.feature.newtask.navigation.navigateToNewTask

@Composable
fun rememberToDoAppState(
    navController: NavHostController = rememberNavController()
): ToDoAppState {
    return remember(
        navController
    ) {
        ToDoAppState(
            navController = navController
        )
    }
}

@Stable
class ToDoAppState(
    val navController: NavHostController
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            val currentDestination = currentDestination
            return remember(currentDestination) {
                TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                    currentDestination?.hasRoute(route = topLevelDestination.route) ?: false
                }
            }
        }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo<HomeRoute> {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.HOME -> {
                navController.navigateToHome(topLevelNavOptions)
            }

            TopLevelDestination.NEW_TASK -> {
                navController.navigateToNewTask(topLevelNavOptions)
            }
        }
    }
}