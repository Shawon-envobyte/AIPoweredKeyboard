package com.ai.keyboard.core.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import java.lang.Thread.sleep

class KeyboardAccessibilityService(

) : AccessibilityService() {

    private val lastMessageMap = mutableMapOf<String, String?>()
    var lastMessage: String = ""
    var readMessage = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = serviceInfo
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
        setServiceInfo(info)
        Log.d("KeyboardAccessibilityService", "Service connected with WebView access")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (ServiceDataPass.isServiceRunning) {
            if (event == null) return
            val packageName = event.packageName?.toString() ?: return

            // Handle relevant events for message detection
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED
            ) {

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
                        lastMessage = concatenateMessage(filteredText, packageName).toString()
                        Log.d("EventText", "Filtered texts: $filteredText")

                        if (lastMessage != null && lastMessage != lastMessageMap[packageName]) {
                            lastMessageMap[packageName] = lastMessage
                            val appName = getAppNameFromPackage(packageName)
                            ServiceDataPass.lastMessage = lastMessage
                            Log.i(
                                "IncomingMessage",
                                "[$appName] Last incoming message: \"$lastMessage\""
                            )
                        } else {
                            Log.d("EventText", "No new message or same as previous: $lastMessage")
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "KeyboardAccessibilityService",
                            "Error parsing messages: ${e.message}",
                            e
                        )
                    } finally {
                        root.recycle()
                    }
                }, 600) // Increased delay for UI stability
            }
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
                if (node.isVisibleToUser && node.className != "android.widget.EditText") { // Skip editable text fields
                    // Extract text, excluding known UI elements
                    val nodeText = node.text?.toString()?.trim()?.takeIf {
                        it.isNotEmpty() &&
                                !it.equals("Message…", true) &&
                                !it.equals("Send a message", true) &&
                                !it.equals("Camera", true) &&
                                !it.equals("Instagram", true) &&
                                !it.equals("View Profile", true) &&
                                !it.equals("Profile picture", true)
                    }
                    nodeText?.let { result.add(it) }

                    val contentDesc = node.contentDescription?.toString()?.trim()?.takeIf {
                        it.isNotEmpty() &&
                                !it.equals("Message…", true) &&
                                !it.equals("Send a message", true) &&
                                !it.equals("Camera", true) &&
                                !it.equals("Instagram", true) &&
                                !it.equals("View Profile", true) &&
                                !it.equals("Profile picture", true)
                    }
                    contentDesc?.let { result.add(it) }

                    // Skip hintText for Instagram to avoid input field placeholders
                    if (node.packageName != "com.instagram.android") {
                        val hintText = node.hintText?.toString()?.trim()?.takeIf { it.isNotEmpty() }
                        hintText?.let { result.add(it) }
                    }

                    // Check for Instagram-specific message views
                    if (node.packageName == "com.instagram.android") {
                        // Try multiple possible view IDs for Instagram messages
                        listOf(
                            "com.instagram.android:id/direct_message_text",
                            "com.instagram.android:id/direct_thread_message_text",
                            "com.instagram.android:id/message_content_text",
                            "com.instagram.android:id/row_message_textview"
                        ).forEach { viewId ->
                            node.findAccessibilityNodeInfosByViewId(viewId)
                                .forEach { messageNode ->
                                    if (messageNode.className != "android.widget.EditText") { // Skip input fields
                                        messageNode.text?.toString()?.trim()?.takeIf {
                                            it.isNotEmpty() &&
                                                    !it.equals("Message…", true) &&
                                                    !it.equals("Send a message", true) &&
                                                    !it.equals("Camera", true) &&
                                                    !it.equals("Instagram", true) &&
                                                    !it.equals("View Profile", true) &&
                                                    !it.equals("Profile picture", true)
                                        }?.let { result.add(it) }
                                    }
                                }
                        }
                    }
                    // Check for WhatsApp-specific message views
                    if (node.className?.contains(
                            "WebView",
                            true
                        ) == true && node.packageName == "com.whatsapp"
                    ) {
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
                if (node != root) try {
                    node.recycle()
                } catch (_: Throwable) {
                }
            }
        }

        return result.map { it.replace(Regex("\\s+"), " ") }
            .filter { it.isNotBlank() }
            .distinct()
    }

    /**
     * Concatenate text fragments that likely belong to the same message.
     */
    private fun concatenateMessage(texts: List<String>, packageName: String): String? {
        if (texts.isEmpty()) return null

        // For WhatsApp, try to identify the last message by excluding UI elements
        if (packageName == "com.whatsapp") {
            val messageCandidates = texts.filter { text ->
                !text.matches(Regex("\\d{1,2}:\\d{2}(?:\\s*(?:AM|PM))?")) && // Exclude 12-hour and 24-hour timestamps
                        !text.contains("You deleted this message", true) &&
                        !text.contains("This message was deleted", true) &&
                        !text.contains("Type a message", true) &&
                        !text.contains("Search", true)
            }
            // Concatenate the last few texts that likely form a single message
            return messageCandidates.takeLast(3).joinToString(" ").takeIf { it.isNotBlank() }
        }

        // For Instagram, select the last valid message
        if (packageName == "com.instagram.android") {
            val messageCandidates = texts.filter { text ->
                !text.contains("Message…", true) &&
                        !text.contains("Send a message", true) &&
                        !text.contains("Camera", true) &&
                        !text.contains("Instagram", true) &&
                        !text.contains("View Profile", true) &&
                        !text.contains("Profile picture", true) &&
                        !text.contains("Search", true) &&
                        !text.contains("Seen", true) &&
                        !text.contains("Active", true) &&
                        !text.contains("Stickers", true) &&
                        !text.contains("Gallery", true) &&
                        !text.contains("Voice message", true) &&
                        !text.contains("Direct", true) &&
                        !text.contains("Chats", true) &&
                        !text.contains("Tap and hold to react", true) &&
                        !text.contains("Sticker", true) &&
                        !text.contains("Send", true)
            }
            // Return the last valid message
            return messageCandidates.lastOrNull()?.takeIf { it.isNotBlank() }
        }

        // For other apps, return the last text
        return texts.lastOrNull()
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
                        it.matches(Regex("\\d{1,2}:\\d{2}(?:\\s*(?:AM|PM))?")) // Filter out 12-hour and 24-hour timestamps
            }

            "com.instagram.android" -> texts.filterNot {
                it.contains("Message…", true) ||
                        it.contains("Send a message", true) ||
                        it.contains("Camera", true) ||
                        it.contains("Instagram", true) ||
                        it.contains("View Profile", true) ||
                        it.contains("Profile picture", true) ||
                        it.contains("Search", true) ||
                        it.contains("Seen", true) ||
                        it.contains("Active", true) &&
                        it.contains("Stickers", true) ||
                        it.contains("Gallery", true) ||
                        it.contains("Voice message", true) ||
                        it.contains("Direct", true) ||
                        it.contains("Chats", true) ||
                        it.contains("Messages", true) ||
                        it.contains("Add a comment", true)
            }

            "com.facebook.orca" -> texts.filterNot {
                it.contains(
                    "Aa",
                    true
                ) || it.contains("Search", true)
            }

            "com.linkedin.android" -> texts.filterNot { it.contains("Write a message", true) }
            "com.google.android.gm" -> texts.filterNot {
                it.contains(
                    "Compose",
                    true
                ) || it.contains("Search", true)
            }

            else -> texts
        }
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