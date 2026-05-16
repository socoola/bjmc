package com.bjmc.classpet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bjmc.classpet.data.entity.RewardLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardLogDao {
    @Query("SELECT * FROM reward_log ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<RewardLogEntity>>

    @Query("SELECT * FROM reward_log ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): RewardLogEntity?

    @Insert
    suspend fun insert(log: RewardLogEntity)

    @Query("DELETE FROM reward_log WHERE id = :id")
    suspend fun deleteById(id: Long)
}
