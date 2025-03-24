package com.anhq.taskmanagement.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.anhq.taskmanagement.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(
    route = HomeRoute, navOptions = navOptions
)

fun NavGraphBuilder.homeScreen(
    navController: NavController
) {
    composable<HomeRoute> {
        HomeRoute(
            navController = navController
        )
    }
}