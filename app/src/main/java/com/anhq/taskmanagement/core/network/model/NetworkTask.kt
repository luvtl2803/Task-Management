package com.anhq.taskmanagement.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTask(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("timeInMills") val timeInMills: Long? = 0,
)