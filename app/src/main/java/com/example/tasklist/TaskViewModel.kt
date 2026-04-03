package com.example.tasklist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel : ViewModel() {
    private val tasks = mutableStateListOf<Task>()
    val tasksList: SnapshotStateList<Task> = tasks

    private lateinit var prefs: TaskPreferences

    fun initPrefs(context: android.content.Context) {
        prefs = TaskPreferences(context)
        loadTasks()
    }

    fun addTask(title: String) {
        if (title.isNotBlank()) {
            val newTask = Task(title = title)
            tasks.add(0,newTask)
            saveTasks()
        }
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            saveTasks()
        }
    }

    fun deleteTask(task: Task) {
        tasks.removeAll { it.id == task.id }
        saveTasks()
    }

    fun toggleTaskCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        updateTask(updatedTask)
    }

    private fun saveTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasksJson = tasks.joinToString("|||") {
                "${it.id}|${it.title}|${it.isCompleted}"
            }
            prefs.saveTasks(tasksJson)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasksJson = prefs.getTasks()
            withContext(Dispatchers.Main) {
                if (tasksJson.isNotEmpty()) {
                    val loadedTasks = tasksJson.split("|||").mapNotNull { taskStr ->
                        val parts = taskStr.split("|")
                        if (parts.size == 3) {
                            Task(
                                id = parts[0],
                                title = parts[1],
                                isCompleted = parts[2].toBoolean()
                            )
                        } else null
                    }
                    tasks.clear()
                    tasks.addAll(loadedTasks)
                }
            }
        }
    }
}