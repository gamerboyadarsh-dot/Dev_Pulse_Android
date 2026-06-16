package com.example.data.dao

import androidx.room.*
import com.example.data.model.ActivityLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 20")
    fun getRecentActivity(): Flow<List<ActivityLog>>

    @Insert
    suspend fun insertActivity(activityLog: ActivityLog)
    
    @Query("SELECT COUNT(*) FROM activity_logs")
    fun getActivityCount(): Flow<Int>
}
