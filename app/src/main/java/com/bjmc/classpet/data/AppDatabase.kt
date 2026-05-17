package com.bjmc.classpet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bjmc.classpet.data.dao.PetDao
import com.bjmc.classpet.data.dao.PresetRewardDao
import com.bjmc.classpet.data.dao.RewardLogDao
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity

@Database(
    entities = [PetEntity::class, RewardLogEntity::class, PresetRewardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun rewardLogDao(): RewardLogDao
    abstract fun presetRewardDao(): PresetRewardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "class_pet.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
