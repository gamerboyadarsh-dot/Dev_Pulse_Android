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

import com.example.util.UserPreferences

class DevPulseViewModel(private val repository: DevPulseRepository, private val userPrefs: UserPreferences) : ViewModel() {

    val userName = userPrefs.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )
    
    val userEmail = userPrefs.userEmail.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )
    
    val isLoggedIn = userPrefs.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val themeMode = userPrefs.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "system"
    )

    fun login(name: String, email: String) {
        viewModelScope.launch {
            userPrefs.saveAuth(name, email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearAuth()
        }
    }

    fun setTheme(mode: String) {
        viewModelScope.launch {
            userPrefs.saveTheme(mode)
        }
    }

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

class DevPulseViewModelFactory(private val repository: DevPulseRepository, private val userPrefs: com.example.util.UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevPulseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevPulseViewModel(repository, userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
