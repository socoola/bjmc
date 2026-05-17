package com.bjmc.classpet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bjmc.classpet.data.entity.PresetRewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetRewardDao {
    @Query("SELECT * FROM preset_reward ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<PresetRewardEntity>>

    @Query("SELECT COUNT(*) FROM preset_reward")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(presets: List<PresetRewardEntity>)

    @Query("DELETE FROM preset_reward")
    suspend fun deleteAll()
}
