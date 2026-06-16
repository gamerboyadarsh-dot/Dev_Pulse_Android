package com.example.data.repository

import com.example.data.dao.ActivityDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.TaskDao
import com.example.data.model.ActivityLog
import com.example.data.model.Project
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

class DevPulseRepository(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao,
    private val activityDao: ActivityDao
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val recentActivity: Flow<List<ActivityLog>> = activityDao.getRecentActivity()
    val completedTasksCount: Flow<Int> = taskDao.getCompletedTasksCount()

    suspend fun insertProject(project: Project) {
        val id = projectDao.insertProject(project)
        logActivity("Project Created", "Created project: ${project.name}", id)
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
        logActivity("Project Updated", "Updated project: ${project.name}", project.id)
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
        logActivity("Project Deleted", "Deleted project: ${project.name}", project.id)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
        logActivity("Task Added", "Added task: ${task.title}", task.projectId)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        if (task.isCompleted) {
            logActivity("Task Completed", "Completed task: ${task.title}", task.projectId)
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    private suspend fun logActivity(type: String, description: String, projectId: Long? = null) {
        activityDao.insertActivity(
            ActivityLog(
                type = type,
                description = description,
                projectId = projectId
            )
        )
    }
}
