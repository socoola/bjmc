package com.bjmc.classpet

import android.app.Application
import com.bjmc.classpet.data.AppDatabase
import com.bjmc.classpet.data.PetRepository

class ClassPetApplication : Application() {
    lateinit var repository: PetRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = PetRepository(db)
    }
}
