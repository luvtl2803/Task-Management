package com.anhq.taskmanagement.core.sync

interface Syncable {
    suspend fun sync(): Boolean
}