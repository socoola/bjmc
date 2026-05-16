# 班级萌宠 Android App 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建班级萌宠 Android App — 老师手机操作发放积分驱动萌宠成长，大屏展示萌宠和班级信息。

**Architecture:** 单 Activity + Compose 底部三 Tab + NavHost 导航 + Room 本地存储 + Presentation API 投屏。MainViewModel 通过 StateFlow 管理全部状态，手机端和大屏端共享同一 ViewModel。

**Tech Stack:** Kotlin 1.9.22, Jetpack Compose (BOM 2024.02.00), Room 2.6.1, Navigation Compose 2.7.7, Material3

---

## File Structure

```
bjmc/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/values/{strings.xml, themes.xml}
│       └── java/com/bjmc/classpet/
│           ├── ClassPetApplication.kt
│           ├── MainActivity.kt
│           ├── data/
│           │   ├── entity/   (PetEntity, RewardLogEntity, PresetRewardEntity)
│           │   ├── dao/      (PetDao, RewardLogDao, PresetRewardDao)
│           │   ├── AppDatabase.kt
│           │   └── PetRepository.kt
│           ├── model/        (GrowthStage, PetType, PetColor)
│           ├── viewmodel/    (MainViewModel)
│           ├── ui/
│           │   ├── theme/          (Theme)
│           │   ├── screen/         (PetHomeScreen, RewardScreen, RewardHistoryScreen)
│           │   ├── component/      (PetAvatar, GrowthProgressBar, PetSetupWizard, PresetRewardEditor)
│           │   ├── presentation/   (ClassroomPresentation)
│           │   └── navigation/     (AppNavigation)
│           └── util/               (GrowthCalculator, CastingManager)
```

---

### Task 1: Project Scaffolding

**Files:**
- Create: `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`, `app/src/main/res/values/themes.xml`

- [ ] **Step 1: Create gradle/libs.versions.toml**

```toml
[versions]
agp = "8.2.2"
kotlin = "1.9.22"
coreKtx = "1.12.0"
lifecycleRuntime = "2.7.0"
activityCompose = "1.8.2"
composeBom = "2024.02.00"
room = "2.6.1"
navigationCompose = "2.7.7"
ksp = "1.9.22-1.0.17"

[libraries]
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntime" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons = { group = "androidx.compose.material", name = "material-icons-extended" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

- [ ] **Step 2: Create build.gradle.kts (project-level)**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
}
```

- [ ] **Step 3: Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ClassPet"
include(":app")
```

- [ ] **Step 4: Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 5: Create app/build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bjmc.classpet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bjmc.classpet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)
}
```

- [ ] **Step 6: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-feature
        android:name="android.software.companion_device_setup"
        android:required="false" />

    <application
        android:name=".ClassPetApplication"
        android:allowBackup="true"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.ClassPet">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.ClassPet">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 7: Create res/values/strings.xml and res/values/themes.xml**

```xml
<!-- strings.xml -->
<resources>
    <string name="app_name">班级萌宠</string>
</resources>
```

```xml
<!-- themes.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.ClassPet" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

- [ ] **Step 8: Verify project builds**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9: Commit**

```bash
git add build.gradle.kts settings.gradle.kts gradle.properties gradle/libs.versions.toml app/build.gradle.kts app/src/main/AndroidManifest.xml app/src/main/res/
git commit -m "chore: scaffold Android project with Compose + Room"
```

---

### Task 2: Domain Models

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/model/GrowthStage.kt`
- Create: `app/src/main/java/com/bjmc/classpet/model/PetType.kt`
- Create: `app/src/main/java/com/bjmc/classpet/model/PetColor.kt`

- [ ] **Step 1: Create GrowthStage.kt**

```kotlin
package com.bjmc.classpet.model

enum class GrowthStage(val label: String, val threshold: Int) {
    EGG("蛋", 0),
    BABY("幼崽", 50),
    TEEN("少年", 150),
    ADULT("成年", 300),
    MATURE("成熟", 500);

    companion object {
        fun fromScore(score: Int): GrowthStage =
            entries.lastOrNull { score >= it.threshold } ?: EGG
    }
}
```

- [ ] **Step 2: Create PetType.kt**

```kotlin
package com.bjmc.classpet.model

enum class PetType(val label: String, val emoji: String) {
    CAT("小猫", "🐱"),
    DOG("小狗", "🐶"),
    RABBIT("小兔", "🐰"),
    FOX("小狐狸", "🦊"),
    PANDA("熊猫", "🐼"),
    DINO("小恐龙", "🦖")
}
```

- [ ] **Step 3: Create PetColor.kt**

```kotlin
package com.bjmc.classpet.model

data class PetColor(val name: String, val hex: Long)

val presetColors = listOf(
    PetColor("粉红", 0xFFFF9C9C),
    PetColor("暖黄", 0xFFFFD27F),
    PetColor("草绿", 0xFFA0E7A0),
    PetColor("天蓝", 0xFF9CC9FF),
    PetColor("淡紫", 0xFFD4A0FF),
    PetColor("柔粉", 0xFFFFB5D0)
)
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/model/
git commit -m "feat: add domain models — GrowthStage, PetType, PetColor"
```

---

### Task 3: Room Entities

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/data/entity/PetEntity.kt`
- Create: `app/src/main/java/com/bjmc/classpet/data/entity/RewardLogEntity.kt`
- Create: `app/src/main/java/com/bjmc/classpet/data/entity/PresetRewardEntity.kt`

- [ ] **Step 1: Create PetEntity.kt**

```kotlin
package com.bjmc.classpet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType

@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val type: String = PetType.CAT.name,
    val color: Long = 0xFFFF9C9C,
    val totalScore: Int = 0,
    val stage: String = GrowthStage.EGG.name
)
```

- [ ] **Step 2: Create RewardLogEntity.kt**

```kotlin
package com.bjmc.classpet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_log")
data class RewardLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val score: Int,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

- [ ] **Step 3: Create PresetRewardEntity.kt**

```kotlin
package com.bjmc.classpet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preset_reward")
data class PresetRewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val icon: String,
    val score: Int,
    val color: Long,
    val sortOrder: Int
)
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/data/entity/
git commit -m "feat: add Room entities — Pet, RewardLog, PresetReward"
```

---

### Task 4: Room DAOs

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/data/dao/PetDao.kt`
- Create: `app/src/main/java/com/bjmc/classpet/data/dao/RewardLogDao.kt`
- Create: `app/src/main/java/com/bjmc/classpet/data/dao/PresetRewardDao.kt`

- [ ] **Step 1: Create PetDao.kt**

```kotlin
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
```

- [ ] **Step 2: Create RewardLogDao.kt**

```kotlin
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
```

- [ ] **Step 3: Create PresetRewardDao.kt**

```kotlin
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
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/data/dao/
git commit -m "feat: add Room DAOs — PetDao, RewardLogDao, PresetRewardDao"
```

---

### Task 5: AppDatabase + PetRepository

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/data/AppDatabase.kt`
- Create: `app/src/main/java/com/bjmc/classpet/data/PetRepository.kt`

- [ ] **Step 1: Create AppDatabase.kt**

```kotlin
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
```

- [ ] **Step 2: Create PetRepository.kt**

```kotlin
package com.bjmc.classpet.data

import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.GrowthStage
import kotlinx.coroutines.flow.Flow

class PetRepository(private val db: AppDatabase) {

    private val petDao = db.petDao()
    private val rewardLogDao = db.rewardLogDao()
    private val presetRewardDao = db.presetRewardDao()

    val pet: Flow<PetEntity?> = petDao.observe()
    val rewardLogs: Flow<List<RewardLogEntity>> = rewardLogDao.observeAll()
    val presetRewards: Flow<List<PresetRewardEntity>> = presetRewardDao.observeAll()

    suspend fun getPet(): PetEntity? = petDao.get()

    suspend fun savePet(pet: PetEntity) {
        petDao.upsert(pet)
    }

    suspend fun addReward(label: String, score: Int, note: String? = null) {
        val log = RewardLogEntity(label = label, score = score, note = note)
        rewardLogDao.insert(log)
        val pet = getPet() ?: return
        val newScore = pet.totalScore + score
        val newStage = GrowthStage.fromScore(newScore)
        petDao.updateScoreAndStage(newScore, newStage.name)
    }

    suspend fun undoLastReward() {
        val latest = rewardLogDao.getLatest() ?: return
        rewardLogDao.deleteById(latest.id)
        val pet = getPet() ?: return
        val newScore = (pet.totalScore - latest.score).coerceAtLeast(0)
        val currentStage = GrowthStage.fromScore(newScore)
        val storedStage = GrowthStage.valueOf(pet.stage)
        val stageToKeep = if (storedStage.ordinal > currentStage.ordinal) {
            storedStage.name
        } else {
            currentStage.name
        }
        petDao.updateScoreAndStage(newScore, stageToKeep)
    }

    suspend fun initDefaultPresetsIfEmpty() {
        if (presetRewardDao.count() == 0) {
            presetRewardDao.upsertAll(defaultPresets)
        }
    }

    suspend fun savePresets(presets: List<PresetRewardEntity>) {
        presetRewardDao.deleteAll()
        presetRewardDao.upsertAll(presets)
    }

    companion object {
        val defaultPresets = listOf(
            PresetRewardEntity(label = "举手回答", icon = "🙋", score = 2, color = 0xFF4ADE80, sortOrder = 0),
            PresetRewardEntity(label = "作业优秀", icon = "📝", score = 5, color = 0xFF60A5FA, sortOrder = 1),
            PresetRewardEntity(label = "帮助同学", icon = "🤝", score = 3, color = 0xFFFBBF24, sortOrder = 2),
            PresetRewardEntity(label = "卫生整洁", icon = "🧹", score = 2, color = 0xFFF472B6, sortOrder = 3),
            PresetRewardEntity(label = "阅读认真", icon = "📖", score = 3, color = 0xFFA78BFA, sortOrder = 4),
            PresetRewardEntity(label = "课堂专注", icon = "🎯", score = 4, color = 0xFF34D399, sortOrder = 5)
        )
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/data/AppDatabase.kt app/src/main/java/com/bjmc/classpet/data/PetRepository.kt
git commit -m "feat: add AppDatabase singleton and PetRepository"
```

---

### Task 6: GrowthCalculator + CastingManager Utilities

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/util/GrowthCalculator.kt`
- Create: `app/src/main/java/com/bjmc/classpet/util/CastingManager.kt`

- [ ] **Step 1: Create GrowthCalculator.kt**

```kotlin
package com.bjmc.classpet.util

import com.bjmc.classpet.model.GrowthStage

object GrowthCalculator {

    fun getStage(score: Int): GrowthStage = GrowthStage.fromScore(score)

    fun getNextStage(score: Int): GrowthStage? {
        val current = getStage(score)
        return GrowthStage.entries.getOrNull(current.ordinal + 1)
    }

    fun getStageProgress(score: Int): Float {
        val current = getStage(score)
        val next = getNextStage(score) ?: return 1f
        val range = next.threshold - current.threshold
        val progress = score - current.threshold
        return (progress.toFloat() / range).coerceIn(0f, 1f)
    }

    fun getScoreToNextStage(score: Int): Int {
        val next = getNextStage(score) ?: return 0
        return next.threshold - score
    }
}
```

- [ ] **Step 2: Create CastingManager.kt**

```kotlin
package com.bjmc.classpet.util

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.bjmc.classpet.ui.presentation.ClassroomPresentation

class CastingManager(context: Context) {
    private val displayManager =
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private var presentation: ClassroomPresentation? = null

    val displays: Array<Display>
        get() = displayManager.displays

    fun getSecondaryDisplay(): Display? {
        return displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }
    }

    fun isCasting(): Boolean = presentation?.isShowing == true

    fun startCasting(context: Context) {
        val display = getSecondaryDisplay() ?: return
        presentation?.dismiss()
        presentation = ClassroomPresentation(context, display)
        presentation?.show()
    }

    fun stopCasting() {
        presentation?.dismiss()
        presentation = null
    }

    fun dismiss() {
        presentation?.dismiss()
        presentation = null
    }
}

@Composable
fun rememberCastingManager(): CastingManager {
    val context = LocalContext.current
    return remember { CastingManager(context) }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/util/
git commit -m "feat: add GrowthCalculator and CastingManager utilities"
```

---

### Task 7: MainViewModel

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/viewmodel/MainViewModel.kt`

- [ ] **Step 1: Create MainViewModel.kt**

```kotlin
package com.bjmc.classpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bjmc.classpet.data.PetRepository
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.util.GrowthCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PetRepository) : ViewModel() {

    val pet: StateFlow<PetEntity?> = repository.pet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rewardLogs: StateFlow<List<RewardLogEntity>> = repository.rewardLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presetRewards: StateFlow<List<PresetRewardEntity>> = repository.presetRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _rewardEvent = MutableSharedFlow<Int>(extraBufferCapacity = 10)
    val rewardEvent = _rewardEvent.asSharedFlow()

    private val _evolutionEvent = MutableSharedFlow<GrowthStage>(extraBufferCapacity = 10)
    val evolutionEvent = _evolutionEvent.asSharedFlow()

    val hasPet: Boolean get() = pet.value?.name?.isNotEmpty() == true
    val currentStage: GrowthStage get() = GrowthCalculator.getStage(pet.value?.totalScore ?: 0)
    val stageProgress: Float get() = GrowthCalculator.getStageProgress(pet.value?.totalScore ?: 0)
    val scoreToNext: Int get() = GrowthCalculator.getScoreToNextStage(pet.value?.totalScore ?: 0)
    val nextStage: GrowthStage? get() = GrowthCalculator.getNextStage(pet.value?.totalScore ?: 0)

    init {
        viewModelScope.launch {
            repository.initDefaultPresetsIfEmpty()
        }
    }

    fun savePet(name: String, type: PetType, color: Long) {
        viewModelScope.launch {
            repository.savePet(PetEntity(name = name, type = type.name, color = color))
        }
    }

    fun updatePetName(name: String) {
        viewModelScope.launch {
            val current = pet.value ?: return@launch
            repository.savePet(current.copy(name = name))
        }
    }

    fun updatePetAppearance(type: PetType, color: Long) {
        viewModelScope.launch {
            val current = pet.value ?: return@launch
            repository.savePet(current.copy(type = type.name, color = color))
        }
    }

    fun addReward(label: String, score: Int, note: String? = null) {
        viewModelScope.launch {
            val oldStage = currentStage
            repository.addReward(label, score, note)
            _rewardEvent.emit(score)
            val newScore = pet.value?.totalScore ?: 0
            val newStage = GrowthCalculator.getStage(newScore)
            if (newStage.ordinal > oldStage.ordinal) {
                _evolutionEvent.emit(newStage)
            }
        }
    }

    fun undoLastReward() {
        viewModelScope.launch {
            repository.undoLastReward()
        }
    }

    fun savePresets(presets: List<PresetRewardEntity>) {
        viewModelScope.launch {
            repository.savePresets(presets)
        }
    }

    class Factory(private val repository: PetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/viewmodel/MainViewModel.kt
git commit -m "feat: add MainViewModel with StateFlow state management"
```

---

### Task 8: Theme + PetAvatar + GrowthProgressBar

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/bjmc/classpet/ui/component/PetAvatar.kt`
- Create: `app/src/main/java/com/bjmc/classpet/ui/component/GrowthProgressBar.kt`

- [ ] **Step 1: Create Theme.kt**

```kotlin
package com.bjmc.classpet.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFFF59E0B),
    secondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFF10B981),
    surface = Color(0xFFFAFAFA),
    background = Color(0xFFF5F5F5),
    error = Color(0xFFEF4444)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp)
)

@Composable
fun ClassPetTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = LightColors.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
```

- [ ] **Step 2: Create PetAvatar.kt**

```kotlin
package com.bjmc.classpet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType

@Composable
fun PetAvatar(
    type: PetType,
    stage: GrowthStage,
    color: Long,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val stageScale = when (stage) {
        GrowthStage.EGG -> 0.6f
        GrowthStage.BABY -> 0.75f
        GrowthStage.TEEN -> 0.85f
        GrowthStage.ADULT -> 0.95f
        GrowthStage.MATURE -> 1.1f
    }

    Box(
        modifier = modifier
            .size(size * stageScale)
            .clip(CircleShape)
            .background(Color(color).copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.emoji,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

- [ ] **Step 3: Create GrowthProgressBar.kt**

```kotlin
package com.bjmc.classpet.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.model.GrowthStage

@Composable
fun GrowthProgressBar(
    currentStage: GrowthStage,
    nextStage: GrowthStage?,
    progress: Float,
    scoreToNext: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "当前阶段：${currentStage.label}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (nextStage != null) {
            Text(
                text = "距${nextStage.label}还需 $scoreToNext 分",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "已达最高阶段！",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/ui/theme/ app/src/main/java/com/bjmc/classpet/ui/component/PetAvatar.kt app/src/main/java/com/bjmc/classpet/ui/component/GrowthProgressBar.kt
git commit -m "feat: add Theme, PetAvatar, and GrowthProgressBar"
```

---

### Task 9: PetSetupWizard Component

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/ui/component/PetSetupWizard.kt`

- [ ] **Step 1: Create PetSetupWizard.kt**

```kotlin
package com.bjmc.classpet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.model.presetColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PetSetupWizard(
    onComplete: (name: String, type: PetType, color: Long) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var selectedType by remember { mutableStateOf(PetType.CAT) }
    var selectedColor by remember { mutableStateOf(presetColors[0].hex) }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "创建班级萌宠", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (step) {
                0 -> "选择萌宠类型"
                1 -> "选择主色调"
                2 -> "给萌宠起个名字"
                else -> ""
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            0 -> {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PetType.entries.forEach { type ->
                        val selected = type == selectedType
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedType = type }
                                .padding(12.dp)
                        ) {
                            Text(text = type.emoji, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                            Text(text = type.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            1 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    presetColors.forEach { pc ->
                        val selected = pc.hex == selectedColor
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(pc.hex))
                                .then(
                                    if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColor = pc.hex }
                        )
                    }
                }
            }
            2 -> {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 6) name = it },
                    label = { Text("萌宠名字（最多6个字）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 0) {
                Button(onClick = { step-- }) { Text("上一步") }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            Button(
                onClick = { if (step < 2) step++ else onComplete(name, selectedType, selectedColor) },
                enabled = step != 2 || name.isNotBlank()
            ) {
                Text(if (step < 2) "下一步" else "完成")
            }
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/ui/component/PetSetupWizard.kt
git commit -m "feat: add PetSetupWizard with 3-step setup flow"
```

---

### Task 10: PresetRewardEditor Component

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/ui/component/PresetRewardEditor.kt`

- [ ] **Step 1: Create PresetRewardEditor.kt**

```kotlin
package com.bjmc.classpet.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PresetRewardEntity

@Composable
fun PresetRewardEditor(
    presets: List<PresetRewardEntity>,
    onSave: (List<PresetRewardEntity>) -> Unit,
    onDismiss: () -> Unit
) {
    val editablePresets = remember { mutableStateListOf<PresetRewardEntity>().also { it.addAll(presets) } }
    var showAddForm by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("") }
    var newScore by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "编辑快捷奖励", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(editablePresets) { index, preset ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${preset.icon} ${preset.label}  +${preset.score}")
                        IconButton(onClick = { editablePresets.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                }
            }
        }

        if (editablePresets.size < 12 && showAddForm) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newIcon,
                onValueChange = { if (it.length <= 2) newIcon = it },
                label = { Text("图标 (emoji)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newLabel,
                onValueChange = { newLabel = it },
                label = { Text("名称") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newScore,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) newScore = it },
                label = { Text("分数") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Button(onClick = {
                    val score = newScore.toIntOrNull()
                    if (newLabel.isNotBlank() && newIcon.isNotBlank() && score != null && score > 0) {
                        editablePresets.add(
                            PresetRewardEntity(
                                label = newLabel, icon = newIcon, score = score,
                                color = 0xFF6366F1, sortOrder = editablePresets.size
                            )
                        )
                        newLabel = ""; newIcon = ""; newScore = ""
                        showAddForm = false
                    }
                }) { Text("添加") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddForm = false; newLabel = ""; newIcon = ""; newScore = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("取消") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (editablePresets.size < 12 && !showAddForm) {
                Button(onClick = { showAddForm = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("添加预设")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            Row {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("取消") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onSave(editablePresets.mapIndexed { index, p -> p.copy(sortOrder = index) })
                }) { Text("保存") }
            }
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/ui/component/PresetRewardEditor.kt
git commit -m "feat: add PresetRewardEditor component"
```

---

### Task 11: UI Screens

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/ui/screen/PetHomeScreen.kt`
- Create: `app/src/main/java/com/bjmc/classpet/ui/screen/RewardScreen.kt`
- Create: `app/src/main/java/com/bjmc/classpet/ui/screen/RewardHistoryScreen.kt`

**Note:** These screens are content composables, NOT standalone Scaffolds. Navigation provides the Scaffold + bottom bar.

- [ ] **Step 1: Create PetHomeScreen.kt**

```kotlin
package com.bjmc.classpet.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.model.presetColors
import com.bjmc.classpet.ui.component.GrowthProgressBar
import com.bjmc.classpet.ui.component.PetAvatar
import com.bjmc.classpet.util.GrowthCalculator

@Composable
fun PetHomeScreen(
    pet: PetEntity?,
    onUpdateName: (String) -> Unit,
    onUpdateAppearance: (PetType, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNameEdit by remember { mutableStateOf(false) }
    var showAppearanceEdit by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(pet?.name ?: "") }

    val stage = pet?.let { GrowthCalculator.getStage(it.totalScore) } ?: GrowthStage.EGG
    val type = pet?.let { PetType.valueOf(it.type) } ?: PetType.CAT
    val progress = pet?.let { GrowthCalculator.getStageProgress(it.totalScore) } ?: 0f
    val nextStage = GrowthCalculator.getNextStage(pet?.totalScore ?: 0)
    val scoreToNext = pet?.let { GrowthCalculator.getScoreToNextStage(it.totalScore) } ?: 50

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PetAvatar(type = type, stage = stage, color = pet?.color ?: 0xFFFF9C9C, size = 160.dp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = pet?.name ?: "未设置", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "${type.emoji} ${type.label} · ${stage.label}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { editName = pet?.name ?: ""; showNameEdit = true }) {
                Text("改名")
            }
            Button(onClick = { showAppearanceEdit = true }) {
                Text("换外观")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${pet?.totalScore ?: 0} 分",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                GrowthProgressBar(
                    currentStage = stage,
                    nextStage = nextStage,
                    progress = progress,
                    scoreToNext = scoreToNext
                )
            }
        }
    }

    // Name edit dialog
    if (showNameEdit) {
        AlertDialog(
            onDismissRequest = { showNameEdit = false },
            title = { Text("改名字") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { if (it.length <= 6) editName = it },
                    label = { Text("萌宠名字（最多6个字）") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (editName.isNotBlank()) { onUpdateName(editName); showNameEdit = false }
                }) { Text("确定") }
            },
            dismissButton = {
                Button(onClick = { showNameEdit = false }) { Text("取消") }
            }
        )
    }

    // Appearance edit dialog
    if (showAppearanceEdit) {
        var selType by remember { mutableStateOf(type) }
        var selColor by remember { mutableStateOf(pet?.color ?: 0xFFFF9C9C) }

        AlertDialog(
            onDismissRequest = { showAppearanceEdit = false },
            title = { Text("换外观") },
            text = {
                Column {
                    Text("选择类型", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PetType.entries.forEach { t ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        2.dp,
                                        if (t == selType) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        MaterialTheme.shapes.small
                                    )
                                    .clickable { selType = t }
                                    .padding(8.dp)
                            ) {
                                Text(text = t.emoji, style = MaterialTheme.typography.headlineMedium)
                                Text(text = t.label, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("选择颜色", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        presetColors.forEach { pc ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(pc.hex))
                                    .then(
                                        if (pc.hex == selColor) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { selColor = pc.hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onUpdateAppearance(selType, selColor); showAppearanceEdit = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                Button(onClick = { showAppearanceEdit = false }) { Text("取消") }
            }
        )
    }
}
```

- [ ] **Step 2: Create RewardScreen.kt**

```kotlin
package com.bjmc.classpet.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PresetRewardEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RewardScreen(
    presets: List<PresetRewardEntity>,
    onAddReward: (String, Int, String?) -> Unit,
    onEditPresets: () -> Unit,
    modifier: Modifier = Modifier
) {
    var customScore by remember { mutableStateOf("") }
    var customNote by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("快捷奖励", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                Button(
                    onClick = { onAddReward(preset.label, preset.score, null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(preset.color))
                ) {
                    Text("${preset.icon} ${preset.label} +${preset.score}")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onEditPresets) { Text("编辑预设") }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("自定义奖励", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = customScore,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() } && it.length <= 3) customScore = it
                        },
                        label = { Text("分数") },
                        singleLine = true,
                        modifier = Modifier.width(100.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = customNote,
                        onValueChange = { customNote = it },
                        label = { Text("备注（可选）") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val score = customScore.toIntOrNull()
                            if (score != null && score > 0) {
                                onAddReward("自定义", score, customNote.ifBlank { null })
                                customScore = ""
                                customNote = ""
                            }
                        },
                        enabled = customScore.isNotBlank()
                    ) { Text("发放") }
                }
            }
        }
    }
}
```

- [ ] **Step 3: Create RewardHistoryScreen.kt**

```kotlin
package com.bjmc.classpet.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.RewardLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardHistoryScreen(
    logs: List<RewardLogEntity>,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    if (logs.isEmpty()) {
        Text(
            text = "暂无奖励记录",
            modifier = modifier.fillMaxSize().padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(logs, key = { it.id }) { log ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) { onUndo(); true }
                        else false
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {},
                    enableDismissFromStartToEnd = false
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = log.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Text(text = "+${log.score}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/ui/screen/
git commit -m "feat: add PetHomeScreen, RewardScreen, RewardHistoryScreen"
```

---

### Task 12: AppNavigation (Bottom Tabs)

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/ui/navigation/AppNavigation.kt`

- [ ] **Step 1: Create AppNavigation.kt**

```kotlin
package com.bjmc.classpet.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.ui.screen.PetHomeScreen
import com.bjmc.classpet.ui.screen.RewardHistoryScreen
import com.bjmc.classpet.ui.screen.RewardScreen

data class TabItem(val label: String, val icon: ImageVector)

val tabs = listOf(
    TabItem("萌宠", Icons.Default.Pets),
    TabItem("奖励", Icons.Default.Stars),
    TabItem("记录", Icons.Default.History)
)

@Composable
fun AppNavigation(
    pet: PetEntity?,
    presetRewards: List<PresetRewardEntity>,
    rewardLogs: List<RewardLogEntity>,
    onUpdateName: (String) -> Unit,
    onUpdateAppearance: (PetType, Long) -> Unit,
    onAddReward: (String, Int, String?) -> Unit,
    onUndoLastReward: () -> Unit,
    onEditPresets: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> PetHomeScreen(
                pet = pet,
                onUpdateName = onUpdateName,
                onUpdateAppearance = onUpdateAppearance,
                modifier = Modifier.padding(padding)
            )
            1 -> RewardScreen(
                presets = presetRewards,
                onAddReward = onAddReward,
                onEditPresets = onEditPresets,
                modifier = Modifier.padding(padding)
            )
            2 -> RewardHistoryScreen(
                logs = rewardLogs,
                onUndo = onUndoLastReward,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/ui/navigation/AppNavigation.kt
git commit -m "feat: add AppNavigation with bottom tab bar"
```

---

### Task 13: ClassroomPresentation + MainActivity

**Files:**
- Create: `app/src/main/java/com/bjmc/classpet/ui/presentation/ClassroomPresentation.kt`
- Create: `app/src/main/java/com/bjmc/classpet/ClassPetApplication.kt`
- Create: `app/src/main/java/com/bjmc/classpet/MainActivity.kt`

- [ ] **Step 1: Create ClassroomPresentation.kt**

```kotlin
package com.bjmc.classpet.ui.presentation

import android.app.Presentation
import android.content.Context
import android.view.Display
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.ui.component.GrowthProgressBar
import com.bjmc.classpet.ui.component.PetAvatar
import com.bjmc.classpet.ui.theme.ClassPetTheme
import com.bjmc.classpet.util.GrowthCalculator

class ClassroomPresentation(
    private val outerContext: Context,
    display: Display
) : Presentation(outerContext, display) {

    var pet: PetEntity? = null
    var recentLogs: List<RewardLogEntity> = emptyList()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(context).apply {
                setContent {
                    ClassPetTheme {
                        ClassroomContent(pet = pet, recentLogs = recentLogs)
                    }
                }
            }
        )
    }

    fun update(pet: PetEntity?, logs: List<RewardLogEntity>) {
        this.pet = pet
        this.recentLogs = logs.take(5)
        if (isShowing) {
            // Re-trigger content — recreate the view
            setContentView(
                ComposeView(context).apply {
                    setContent {
                        ClassPetTheme {
                            ClassroomContent(pet = pet, recentLogs = recentLogs)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun ClassroomContent(
    pet: PetEntity?,
    recentLogs: List<RewardLogEntity>
) {
    val stage = pet?.let { GrowthCalculator.getStage(it.totalScore) }
        ?: com.bjmc.classpet.model.GrowthStage.EGG
    val type = pet?.let { PetType.valueOf(it.type) } ?: PetType.CAT
    val progress = pet?.let { GrowthCalculator.getStageProgress(it.totalScore) } ?: 0f
    val nextStage = GrowthCalculator.getNextStage(pet?.totalScore ?: 0)
    val scoreToNext = pet?.let { GrowthCalculator.getScoreToNextStage(it.totalScore) } ?: 50

    Row(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        // Left 60% — Pet display
        Column(
            modifier = Modifier.weight(0.6f).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PetAvatar(type = type, stage = stage, color = pet?.color ?: 0xFFFF9C9C, size = 240.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pet?.name ?: "未设置",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "${type.emoji} ${type.label} · ${stage.label}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Right 40% — Info panel
        Column(modifier = Modifier.weight(0.4f).fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "${pet?.totalScore ?: 0}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("班级总积分", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("成长进度", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    GrowthProgressBar(
                        currentStage = stage,
                        nextStage = nextStage,
                        progress = progress,
                        scoreToNext = scoreToNext
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("最近奖励", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(recentLogs) { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = log.label, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "+${log.score}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 2: Create ClassPetApplication.kt**

```kotlin
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
```

- [ ] **Step 3: Create MainActivity.kt**

```kotlin
package com.bjmc.classpet

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.DisplayManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bjmc.classpet.ui.component.PetSetupWizard
import com.bjmc.classpet.ui.component.PresetRewardEditor
import com.bjmc.classpet.ui.navigation.AppNavigation
import com.bjmc.classpet.ui.presentation.ClassroomPresentation
import com.bjmc.classpet.ui.theme.ClassPetTheme
import com.bjmc.classpet.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as ClassPetApplication
        MainViewModel.Factory(app.repository)
    }

    private var presentation: ClassroomPresentation? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        setContent {
            ClassPetTheme {
                val pet by viewModel.pet.collectAsState()
                val presetRewards by viewModel.presetRewards.collectAsState()
                val rewardLogs by viewModel.rewardLogs.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showPresetEditor by remember { mutableStateOf(false) }

                    if (!viewModel.hasPet) {
                        PetSetupWizard { name, type, color ->
                            viewModel.savePet(name, type, color)
                        }
                    } else {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("班级萌宠") },
                                    actions = {
                                        if (dm.displays.size > 1) {
                                            IconButton(onClick = {
                                                if (presentation?.isShowing == true) {
                                                    presentation?.dismiss()
                                                    presentation = null
                                                } else {
                                                    dm.displays.firstOrNull {
                                                        it.displayId != Display.DEFAULT_DISPLAY
                                                    }?.let { display ->
                                                        presentation = ClassroomPresentation(
                                                            this@MainActivity, display
                                                        )
                                                        presentation?.show()
                                                        presentation?.update(pet, rewardLogs)
                                                    }
                                                }
                                            }) {
                                                Icon(
                                                    Icons.Default.Cast,
                                                    contentDescription = "投屏",
                                                    tint = if (presentation?.isShowing == true)
                                                        MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        ) { padding ->
                            AppNavigation(
                                pet = pet,
                                presetRewards = presetRewards,
                                rewardLogs = rewardLogs,
                                onUpdateName = { viewModel.updatePetName(it) },
                                onUpdateAppearance = { t, c -> viewModel.updatePetAppearance(t, c) },
                                onAddReward = { label, score, note ->
                                    viewModel.addReward(label, score, note)
                                },
                                onUndoLastReward = { viewModel.undoLastReward() },
                                onEditPresets = { showPresetEditor = true },
                                modifier = Modifier.padding(padding)
                            )
                        }

                        if (showPresetEditor) {
                            PresetRewardEditor(
                                presets = presetRewards,
                                onSave = { viewModel.savePresets(it); showPresetEditor = false },
                                onDismiss = { showPresetEditor = false }
                            )
                        }
                    }

                    LaunchedEffect(pet, rewardLogs) {
                        presentation?.update(pet, rewardLogs)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presentation?.dismiss()
    }
}
```

- [ ] **Step 4: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/bjmc/classpet/ui/presentation/ClassroomPresentation.kt app/src/main/java/com/bjmc/classpet/ClassPetApplication.kt app/src/main/java/com/bjmc/classpet/MainActivity.kt
git commit -m "feat: add ClassroomPresentation, Application, and MainActivity"
```

---

## Plan Review Checklist

After implementing all tasks, verify:

1. App launches → shows PetSetupWizard on first run
2. Complete wizard → PetHomeScreen shows with name, type, avatar
3. Switch to Tab 2 → default preset buttons visible, tap one → score increases
4. Switch to Tab 3 → reward log entry appears, swipe left to undo
5. Tab 1 → progress bar updates after rewards
6. Connect secondary display → cast icon appears in top bar → tap → pet shows on external display
7. Score reaches threshold → evolution animation triggers
8. Stage never decreases on undo
