package com.ai.keyboard.presentation.service

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import com.ai.keyboard.presentation.screen.keyboard.KeyboardScreen
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import org.koin.android.ext.android.inject

class KeyboardIME : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val viewModel: KeyboardViewModel by inject()
    private var composeView: ComposeView? = null

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val audioManager: AudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    // Lifecycle
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry

    // ViewModel Store
    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = store

    // SavedState
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        // Set owners on the IME window decor view if available
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this@KeyboardIME)
            decorView.setViewTreeViewModelStoreOwner(this@KeyboardIME)
            decorView.setViewTreeSavedStateRegistryOwner(this@KeyboardIME)
        }
    }

    override fun onCreateInputView(): View {
        // Ensure lifecycle is at least STARTED before creating ComposeView
        if (lifecycleRegistry.currentState < Lifecycle.State.STARTED) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        composeView = ComposeView(this).apply {
            // Set the lifecycle owners before setting content
            setViewTreeLifecycleOwner(this@KeyboardIME)
            setViewTreeViewModelStoreOwner(this@KeyboardIME)
            setViewTreeSavedStateRegistryOwner(this@KeyboardIME)

            setContent {
                val uiState by viewModel.uiState.collectAsState()

                KeyboardScreen(
                    onTextChange = ::commitText,
                    onCursorChange = ::moveCursor,
                    onKeyPress = {
                        performFeedback(
                            hapticEnabled = uiState.keyboardState.isHapticEnabled,
                            soundEnabled = uiState.keyboardState.isSoundEnabled
                        )
                    },
                    viewModel = viewModel
                )
            }
        }

        return composeView!!
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd
        )
        if (newSelStart != -1) {
            viewModel.handleIntent(KeyboardIntent.CursorPositionChanged(newSelStart))
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        store.clear()
    }

    private fun commitText(text: String, newCursorPosition: Int) {
        val ic: InputConnection = currentInputConnection ?: return

        ic.beginBatchEdit()
        ic.deleteSurroundingText(Int.MAX_VALUE, Int.MAX_VALUE) // Clear the entire field
        ic.commitText(text, 1)
        ic.setSelection(newCursorPosition, newCursorPosition)
        ic.endBatchEdit()
    }

    private fun moveCursor(position: Int) {
        val ic: InputConnection = currentInputConnection ?: return
        ic.setSelection(position, position)
    }

    private fun performFeedback(hapticEnabled: Boolean, soundEnabled: Boolean) {
        if (hapticEnabled) {
            performHapticFeedback()
        }

        if (soundEnabled) {
            performSoundFeedback()
        }
    }

    private fun performHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        10,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(10)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun performSoundFeedback() {
        try {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}