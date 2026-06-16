package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.repository.DevPulseRepository

class DevPulseApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DevPulseRepository(database.projectDao(), database.taskDao(), database.activityDao()) }
}
