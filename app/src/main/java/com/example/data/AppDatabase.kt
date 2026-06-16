package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ActivityDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.TaskDao
import com.example.data.model.ActivityLog
import com.example.data.model.Project
import com.example.data.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Project::class, Task::class, ActivityLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "devpulse_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val projectDao = database.projectDao()
            val taskDao = database.taskDao()
            val activityDao = database.activityDao()

            val p1Id = projectDao.insertProject(Project(name = "DevPulse Android", description = "Productivity Hub", language = "Kotlin"))
            val p2Id = projectDao.insertProject(Project(name = "Awesome Rust Tools", description = "CLI tools for devs", language = "Rust"))

            taskDao.insertTask(Task(title = "Design DB Schema", projectId = p1Id, isCompleted = true, completedAt = System.currentTimeMillis() - 86400000))
            taskDao.insertTask(Task(title = "Implement Room", projectId = p1Id, isCompleted = true, completedAt = System.currentTimeMillis() - 3600000))
            taskDao.insertTask(Task(title = "Integrate GitHub API", projectId = p1Id))
            taskDao.insertTask(Task(title = "Build Analytics Chart", projectId = p1Id))

            taskDao.insertTask(Task(title = "Setup Cargo", projectId = p2Id, isCompleted = true))
            taskDao.insertTask(Task(title = "Write memory safe struct", projectId = p2Id))

            activityDao.insertActivity(ActivityLog(type = "Project Created", description = "Created project: DevPulse Android", projectId = p1Id, timestamp = System.currentTimeMillis() - 172800000))
            activityDao.insertActivity(ActivityLog(type = "Task Completed", description = "Completed task: Implement Room", projectId = p1Id, timestamp = System.currentTimeMillis() - 3600000))
        }
    }
}

