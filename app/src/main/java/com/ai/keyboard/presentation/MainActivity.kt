package com.ai.keyboard.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ai.keyboard.presentation.screen.settings.SettingsScreen
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import com.ai.keyboard.presentation.screen.setup.SetupScreen
import com.ai.keyboard.presentation.screen.theme.ThemeScreen
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIKeyboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onEnableKeyboard = {
                            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                        },
                        onSelectKeyboard = {
                            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                                .showInputMethodPicker()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEnableKeyboard: () -> Unit,
    onSelectKeyboard: () -> Unit,
    viewModel: KeyboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Keyboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val inputText = remember { mutableStateOf("") }
            TextField(
                value = inputText.value,
                onValueChange = { inputText.value = it },
                label = { Text("Enter text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Setup") },
                    icon = { Icon(Icons.Default.Settings, "Setup") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Themes") },
                    icon = { Icon(Icons.Default.Face, "Themes") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Settings") },
                    icon = { Icon(Icons.Default.Build, "Settings") }
                )
            }

            // Content
            when (selectedTab) {
                0 -> SetupScreen(
                    onEnableKeyboard = onEnableKeyboard,
                    onSelectKeyboard = onSelectKeyboard
                )

                1 -> ThemeScreen(
                    currentTheme = uiState.keyboardState.theme,
                    onThemeChange = { theme ->
                        viewModel.handleIntent(KeyboardIntent.ThemeChanged(theme))
                    }
                )

                2 -> SettingsScreen(
                    isHapticEnabled = uiState.keyboardState.isHapticEnabled,
                    isSoundEnabled = uiState.keyboardState.isSoundEnabled,
                    isNumberRowEnabled = uiState.keyboardState.isNumberRowEnabled,
                    onToggleHaptic = {
                        viewModel.handleIntent(KeyboardIntent.ToggleHaptic)
                    },
                    onToggleSound = {
                        viewModel.handleIntent(KeyboardIntent.ToggleSound)
                    },
                    onToggleNumberRow = {
                        viewModel.handleIntent(KeyboardIntent.ToggleNumerRow)
                    }
                )
            }
        }
    }
}