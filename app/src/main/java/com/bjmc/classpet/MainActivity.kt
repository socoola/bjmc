package com.bjmc.classpet

import android.content.Context
import android.os.Bundle
import android.hardware.display.DisplayManager
import android.view.Display
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
                                onUndoLastReward = { id -> if (id != null) viewModel.deleteRewardLog(id) else viewModel.undoLastReward() },
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
