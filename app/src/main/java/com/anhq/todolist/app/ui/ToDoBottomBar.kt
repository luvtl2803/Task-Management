package com.anhq.todolist.app.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anhq.todolist.app.navigation.TopLevelDestination
import com.anhq.todolist.core.designsystem.theme.Neutral01
import com.anhq.todolist.core.designsystem.theme.Neutral03
import com.anhq.todolist.core.designsystem.theme.Neutral08
import com.anhq.todolist.core.designsystem.theme.Neutral10
import com.anhq.todolist.core.designsystem.theme.label1
import com.anhq.todolist.core.designsystem.theme.label2

@Composable
fun NanaBottomBar(
    currentDestination: TopLevelDestination?,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    BottomAppBar(
        containerColor = Neutral10,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
        destinations.forEach { destination ->
            val selected = destination == currentDestination
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        tint = Color.Unspecified,
                        painter = painterResource(
                            id = if (selected)
                                destination.selectedIconId
                            else
                                destination.unSelectedIconId
                        ), contentDescription = null
                    )
                },
                label = {
                    if (selected)
                        Text(
                            text = stringResource(id = destination.labelId),
                            color = Neutral01,
                            style = MaterialTheme.typography.label1
                        )
                    else
                        Text(
                            text = stringResource(id = destination.labelId),
                            color = Neutral03,
                            style = MaterialTheme.typography.label2
                        )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Neutral08,
                )
            )
        }
    }
}


@Preview
@Composable
private fun NanaBottomBarPreview() {
    NanaBottomBar(
        currentDestination = TopLevelDestination.HOME,
        destinations = TopLevelDestination.entries,
        onNavigateToDestination = {}
    )
}