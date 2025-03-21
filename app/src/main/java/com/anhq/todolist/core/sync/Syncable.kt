package com.anhq.todolist.core.sync

interface Syncable {
    suspend fun sync(): Boolean
}