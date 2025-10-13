package com.ai.keyboard.data.source.local

import android.util.LruCache
import com.ai.keyboard.domain.model.Suggestion

class CacheManager {
    private val suggestionCache = LruCache<String, List<Suggestion>>(100)

    fun getSuggestions(key: String): List<Suggestion>? {
        return suggestionCache.get(key)
    }

    fun saveSuggestions(key: String, suggestions: List<Suggestion>) {
        suggestionCache.put(key, suggestions)
    }

    fun clear() {
        suggestionCache.evictAll()
    }
}