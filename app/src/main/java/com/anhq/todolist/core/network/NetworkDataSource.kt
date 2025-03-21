package com.anhq.todolist.core.network

import com.anhq.todolist.core.network.model.NetworkTask
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

const val TASK_ENDPOINT = "task.json"

class NetworkDataSource @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getTasks(): List<NetworkTask> {
        return ktorClient.get(TASK_ENDPOINT).body()
    }
}