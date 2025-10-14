package com.ai.keyboard.core.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

@SuppressLint("AccessibilityPolicy")
class KeyboardAccessibilityService : AccessibilityService() {

    private val lastMessageMap = mutableMapOf<String, String?>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = serviceInfo
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
        setServiceInfo(info)
        Log.d("KeyboardAccessibilityService", "Service connected with WebView access")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Handle relevant events for message detection
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {

            Log.d("AccessibilityEvent", "Event from: $packageName, Type: ${event.eventType}")

            android.os.Handler(Looper.getMainLooper()).postDelayed({
                val root = rootInActiveWindow ?: return@postDelayed

                try {
                    val allTexts = extractTextFromRoot(root)
                    if (allTexts.isEmpty()) {
                        Log.d("EventText", "No text found in root node")
                        return@postDelayed
                    }

                    val filteredText = filterAppMessages(packageName, allTexts)
                    val lastMessage = concatenateMessage(filteredText, packageName)
                    Log.d("EventText", "Filtered texts: $filteredText")

                    if (lastMessage != null && lastMessage != lastMessageMap[packageName]) {
                        lastMessageMap[packageName] = lastMessage
                        val appName = getAppNameFromPackage(packageName)
                        Log.i("IncomingMessage", "[$appName] Last incoming message: \"$lastMessage\"")
                    } else {
                        Log.d("EventText", "No new message or same as previous: $lastMessage")
                    }
                } catch (e: Exception) {
                    Log.e("KeyboardAccessibilityService", "Error parsing messages: ${e.message}", e)
                } finally {
                    root.recycle()
                }
            }, 600) // Increased delay for UI stability
        }
    }

    override fun onInterrupt() {
        Log.d("KeyboardAccessibilityService", "Service interrupted")
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        Log.d("KeyboardAccessibilityService", "Service unbound")
        return super.onUnbind(intent)
    }

    /**
     * Traverse view tree and extract visible texts, handling large messages.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractTextFromRoot(root: AccessibilityNodeInfo?): List<String> {
        if (root == null) return emptyList()
        val result = mutableListOf<String>()
        val stack = ArrayDeque<AccessibilityNodeInfo>()
        stack.add(root)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            try {
                if (node.isVisibleToUser) {
                    // Extract text from various node properties
                    node.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { result.add(it) }
                    node.contentDescription?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { result.add(it) }
                    node.hintText?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { result.add(it) }

                    // Check for Instagram-specific message views
                    if (node.packageName == "com.instagram.android") {
                        node.findAccessibilityNodeInfosByViewId("com.instagram.android:id/direct_message_text")
                            .forEach { messageNode ->
                                messageNode.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
                                    ?.let { result.add(it) }
                            }
                    }
                    // Check for WhatsApp-specific message views (unchanged)
                    if (node.className?.contains("WebView", true) == true && node.packageName == "com.whatsapp") {
                        node.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text")
                            .forEach { webNode ->
                                webNode.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
                                    ?.let { result.add(it) }
                            }
                    }
                }

                // Traverse all children
                val count = node.childCount
                for (i in 0 until count) {
                    node.getChild(i)?.let { stack.add(it) }
                }
            } catch (e: Throwable) {
                Log.e("ExtractText", "Error processing node: ${e.message}")
            } finally {
                if (node != root) try { node.recycle() } catch (_: Throwable) {}
            }
        }

        return result.map { it.replace(Regex("\\s+"), " ") }
            .filter { it.isNotBlank() }
            .distinct()
    }

    /**
     * Filter messages by app-specific patterns.
     */
    private fun filterAppMessages(packageName: String, texts: List<String>): List<String> {
        return when (packageName) {
            "com.whatsapp" -> texts.filterNot {
                it.contains("Type a message", true) ||
                        it.contains("Search", true) ||
                        it.contains("online", true) ||
                        it.contains("typing", true) ||
                        it.matches(Regex("\\d{1,2}:\\d{2}"))
            }
            "com.instagram.android" -> texts.filterNot {
                it.contains("Send a message", true) || // Updated to match Instagram's input field
                        it.contains("Search", true) ||
                        it.contains("Seen", true) || // Exclude "Seen" status
                        it.contains("Active", true) // Exclude "Active now" status
            }
            "com.facebook.orca" -> texts.filterNot { it.contains("Aa", true) || it.contains("Search", true) }
            "com.linkedin.android" -> texts.filterNot { it.contains("Write a message", true) }
            "com.google.android.gm" -> texts.filterNot { it.contains("Compose", true) || it.contains("Search", true) }
            else -> texts
        }
    }

    /**
     * Concatenate text fragments that likely belong to the same message.
     */
    private fun concatenateMessage(texts: List<String>, packageName: String): String? {
        if (texts.isEmpty()) return null

        // For WhatsApp, try to identify the last message by excluding UI elements
        if (packageName == "com.whatsapp") {
            val messageCandidates = texts.filter { text ->
                !text.matches(Regex("\\d{1,2}:\\d{2}")) && // Exclude timestamps
                        !text.contains("You deleted this message", true) &&
                        !text.contains("This message was deleted", true) &&
                        !text.contains("Type a message", true) &&
                        !text.contains("Search", true)
            }

            // Concatenate the last few texts that likely form a single message
            return messageCandidates.takeLast(3).joinToString(" ").takeIf { it.isNotBlank() }
        }

        // For other apps, return the last text
        return texts.lastOrNull()
    }


    /**
     * Map package names to human-readable app names.
     */
    private fun getAppNameFromPackage(pkg: String): String {
        return when (pkg) {
            "com.whatsapp" -> "WhatsApp"
            "com.instagram.android" -> "Instagram"
            "com.facebook.orca" -> "Facebook Messenger"
            "com.linkedin.android" -> "LinkedIn"
            "com.google.android.gm" -> "Gmail"
            else -> pkg
        }
    }
}