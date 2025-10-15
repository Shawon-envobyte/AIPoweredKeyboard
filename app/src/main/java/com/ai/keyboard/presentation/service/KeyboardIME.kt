package com.ai.keyboard.presentation.service

import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
import com.ai.keyboard.presentation.screen.keyboard.KeyboardScreen
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import org.koin.android.ext.android.inject

object KeyboardBridge {
    var ime: KeyboardIME? = null
}
class KeyboardIME : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val viewModel: KeyboardViewModel by inject()
    private var composeView: ComposeView? = null

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val audioManager: AudioManager by lazy {
        getSystemService(AUDIO_SERVICE) as AudioManager
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
        KeyboardBridge.ime = this
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
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@KeyboardIME)
            setViewTreeViewModelStoreOwner(this@KeyboardIME)
            setViewTreeSavedStateRegistryOwner(this@KeyboardIME)

            setContent {
                val uiState by viewModel.uiState.collectAsState()

                KeyboardScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { clip = false },
                    onTextChange = ::commitText,
                    onCursorChange = ::moveCursor,
                    onTextSelectAndDelete = ::selectAndDelete,
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

        // Don't initialize text - let InputConnection manage it
        viewModel.resetText()
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
        val allText = getAllTextFromInputField()
        if (allText != null) {
            viewModel.updateInputFieldText(allText)
        }
        // Update cursor position in ViewModel
        if (newSelStart != -1 && newSelStart == newSelEnd) {
            // Get current text from InputConnection
            val ic = currentInputConnection
            if (ic != null) {
                try {
                    val textBefore = ic.getTextBeforeCursor(10000, 0)?.toString() ?: ""
                    val textAfter = ic.getTextAfterCursor(10000, 0)?.toString() ?: ""
                    val fullText = textBefore + textAfter
                    viewModel.syncWithInputConnection(fullText, newSelStart)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onDestroy() {
        KeyboardBridge.ime = null
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        store.clear()
        super.onDestroy()
    }

    private fun commitText(text: String, newCursorPosition: Int) {
        val ic: InputConnection = currentInputConnection ?: return

        try {
            if (text == "BACKSPACE") {
                // Handle backspace
                ic.deleteSurroundingText(1, 0)
            } else {
                // Commit the character directly
                ic.commitText(text, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveCursor(position: Int) {
        val ic: InputConnection = currentInputConnection ?: return
        try {
            ic.setSelection(position, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectAndDelete(amount: Int) {
        val ic: InputConnection = currentInputConnection ?: return
        try {
            if (amount < 0) {
                ic.deleteSurroundingText(-amount, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                        1,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(1)
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

    private fun getAllTextFromInputField(): String? {
        val ic = currentInputConnection ?: return null
        val extracted = ic.getExtractedText(android.view.inputmethod.ExtractedTextRequest(), 0)
        return extracted?.text?.toString()
    }
    fun replaceInputFieldText(newText: String) {
        val ic = currentInputConnection ?: return
        try {
            // Get all existing text
            val currentText = getAllTextFromInputField() ?: ""
            if (currentText.isNotEmpty()) {
                // Select all text
                ic.setSelection(0, currentText.length)
                // Replace with new text
                ic.commitText(newText, 1)
            } else {
                // If field is empty, just insert the new text
                ic.commitText(newText, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}