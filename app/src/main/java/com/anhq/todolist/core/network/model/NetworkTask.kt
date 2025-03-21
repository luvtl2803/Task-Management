package com.anhq.todolist.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTask(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("time") val time: String? = null,
    @SerialName("date") val date: String? = null
)