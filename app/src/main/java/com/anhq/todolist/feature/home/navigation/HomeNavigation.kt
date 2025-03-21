package com.anhq.todolist.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.anhq.todolist.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(
    route = HomeRoute, navOptions = navOptions
)

fun NavGraphBuilder.homeScreen(

) {
    composable<HomeRoute> {
        HomeRoute(

        )
    }
}