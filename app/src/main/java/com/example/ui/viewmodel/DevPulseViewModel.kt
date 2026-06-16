package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Project
import com.example.data.model.Task
import com.example.data.repository.DevPulseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DevPulseViewModel(private val repository: DevPulseRepository) : ViewModel() {

    val projects = repository.allProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentActivity = repository.recentActivity.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedTasksCount = repository.completedTasksCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )


    fun addProject(name: String, description: String, language: String) {
        viewModelScope.launch {
            repository.insertProject(Project(name = name, description = description, language = language))
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun addTask(title: String, projectId: Long? = null, priority: Int = 2) {
        viewModelScope.launch {
            repository.insertTask(Task(title = title, projectId = projectId, priority = priority))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted, completedAt = if (!task.isCompleted) System.currentTimeMillis() else null))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}

class DevPulseViewModelFactory(private val repository: DevPulseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevPulseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevPulseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
