package com.example.tasklist

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TaskPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

    fun saveTasks(tasksJson: String) {
        prefs.edit {
            putString("tasks_list", tasksJson)
        }
    }

    fun getTasks(): String {
        return prefs.getString("tasks_list", "") ?: ""
    }
}
