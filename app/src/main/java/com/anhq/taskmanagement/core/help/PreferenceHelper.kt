package com.anhq.taskmanagement.core.help

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "TaskPreferences"
    private const val KEY_EVENT_ID_PREFIX = "event_id_"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveEventId(context: Context, taskId: String, eventId: Long?) {
        val prefs = getPreferences(context)
        with(prefs.edit()) {
            if (eventId != null) {
                putLong(KEY_EVENT_ID_PREFIX + taskId, eventId)
            } else {
                remove(KEY_EVENT_ID_PREFIX + taskId)
            }
            apply()
        }
    }

    fun getEventId(context: Context, taskId: String): Long? {
        val prefs = getPreferences(context)
        val key = KEY_EVENT_ID_PREFIX + taskId
        return if (prefs.contains(key)) {
            prefs.getLong(key, -1).takeIf { it != -1L }
        } else {
            null
        }
    }

    fun removeEventId(context: Context, taskId: String) {
        val prefs = getPreferences(context)
        with(prefs.edit()) {
            remove(KEY_EVENT_ID_PREFIX + taskId)
            apply()
        }
    }
}