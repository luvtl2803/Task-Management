package com.anhq.taskmanagement.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTask(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("time") val time: String? = null,
    @SerialName("date") val date: String? = null
)