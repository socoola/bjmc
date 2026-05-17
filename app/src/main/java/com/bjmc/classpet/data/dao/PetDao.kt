package com.bjmc.classpet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bjmc.classpet.data.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pet WHERE id = 1")
    fun observe(): Flow<PetEntity?>

    @Query("SELECT * FROM pet WHERE id = 1")
    suspend fun get(): PetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pet: PetEntity)

    @Query("UPDATE pet SET totalScore = :score, stage = :stage WHERE id = 1")
    suspend fun updateScoreAndStage(score: Int, stage: String)
}
