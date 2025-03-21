package com.anhq.todolist.app.navigation

import com.anhq.todolist.R
import com.anhq.todolist.feature.home.navigation.HomeRoute
import com.anhq.todolist.feature.newtask.navigation.NewTaskRoute
import kotlin.reflect.KClass


enum class TopLevelDestination(
    val labelId: Int,
    val selectedIconId: Int,
    val unSelectedIconId: Int,
    val route: KClass<*>,
) {
    HOME(
        labelId = R.string.home,
        selectedIconId = R.drawable.ic_home_black,
        unSelectedIconId = R.drawable.ic_home_black,
        route = HomeRoute::class,
    ),
    NEW_TASK(
        labelId = R.string.new_task,
        selectedIconId = R.drawable.ic_add_task_black,
        unSelectedIconId = R.drawable.ic_add_task_black,
        route = NewTaskRoute::class
    )
}