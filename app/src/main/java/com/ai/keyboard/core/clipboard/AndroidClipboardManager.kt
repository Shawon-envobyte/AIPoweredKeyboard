package com.ai.keyboard.core.clipboard

import android.content.ClipboardManager
import android.content.Context
import java.text.Normalizer

class AndroidClipboardManager(private val context: Context) {
    
    private val clipboardManager: ClipboardManager by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    
    fun getClipboardText(): String {
        val clipData = clipboardManager.primaryClip ?: return ""
        return if (clipData.itemCount > 0) {
            val item = clipData.getItemAt(0)
            // Convert to plain text (removes any spans or styles)
            val raw = item.coerceToText(context).toString()
            // Normalize styled Unicode (e.g., mathematical bold/italic, fullwidth) to regular ASCII where possible
            val normalized = Normalizer.normalize(raw, Normalizer.Form.NFKD)
                .replace(Regex("\\p{M}+"), "")
            
            normalized
                .replace(Regex("<[^>]*>"), "") // Remove all HTML tags
                .replace(Regex("(?m)^\\s{0,3}#{1,6}\\s+"), "") // Remove Markdown heading markers at line starts
                .replace("```", "") // Remove fenced code block markers but keep content
                .replace(Regex("`([^`]+)`"), "$1") // Inline code to plain text
                .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1") // Remove markdown bold **text**
                .replace(Regex("__([^_]+)__"), "$1") // Remove markdown bold __text__
                .replace(Regex("\\*([^*]+)\\*"), "$1") // Remove markdown italic *text*
                .replace(Regex("_([^_]+)_"), "$1") // Remove markdown italic _text_
                .replace(Regex("~~([^~]+)~~"), "$1") // Remove strikethrough ~~text~~
                .replace(Regex("!\\[[^\\]]*\\]\\([^)]*\\)"), "") // Remove markdown images
                .replace(Regex("\\[([^\\]]+)\\]\\([^)]*\\)"), "$1") // Remove markdown links [text](url)
                .replace("&nbsp;", " ") // Decode common HTML entities
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace(Regex("\\r\\n|\\r|\\n"), " ") // Replace line breaks with spaces
                .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
                .trim()
        } else {
            ""
        }
    }
}