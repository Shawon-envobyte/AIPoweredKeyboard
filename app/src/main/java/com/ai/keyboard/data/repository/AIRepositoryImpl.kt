package com.ai.keyboard.data.repository

import android.util.Log
import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.data.mapper.aiWritingAssistancePrompt
import com.ai.keyboard.data.mapper.aiWritingAssistanceSystemPrompt
import com.ai.keyboard.data.mapper.contentRephrasePrompt
import com.ai.keyboard.data.mapper.contentRephraseSystemPrompt
import com.ai.keyboard.data.mapper.fixGrammarPrompt
import com.ai.keyboard.data.mapper.fixGrammarSystemPrompt
import com.ai.keyboard.data.mapper.quickReplyPrompt
import com.ai.keyboard.data.mapper.quickReplySystemPrompt
import com.ai.keyboard.data.mapper.toAPIRequest
import com.ai.keyboard.data.mapper.translatePrompt
import com.ai.keyboard.data.mapper.translateSystemPrompt
import com.ai.keyboard.data.mapper.wordTonePrompt
import com.ai.keyboard.data.mapper.wordToneSystemPrompt
import com.ai.keyboard.data.source.local.CacheManager
import com.ai.keyboard.data.source.local.LocalAIEngine
import com.ai.keyboard.data.source.remote.api.APIDataSource
import com.ai.keyboard.domain.model.QuickReply
import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.domain.repository.AIRepository

class AIRepositoryImpl(
    private val localAI: LocalAIEngine,
    private val cache: CacheManager,
    private val remoteSource: APIDataSource
) : AIRepository {

    override suspend fun getSuggestions(
        text: String,
        context: String,
        language: String
    ): Result<List<Suggestion>> {
        return try {
            // Check cache first
            cache.getSuggestions(text)?.let {
                return Result.success(it)
            }

            // Get from local AI
            val suggestions = localAI.getSuggestions(text, context, language)

            // Cache results
            cache.saveSuggestions(text, suggestions)

            Result.success(suggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun correctGrammar(text: String): Result<String> {
        return try {
            val corrected = localAI.correctGrammar(text)
            Result.success(corrected)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun predictNextWord(
        text: String,
        count: Int
    ): Result<List<String>> {
        return try {
            val predictions = localAI.predictNextWord(text, count)
            Result.success(predictions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSmartReplies(message: String): Result<List<String>> {
        return try {
            val replies = localAI.getSmartReplies(message)
            Result.success(replies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rephraseContent(
        content: String,
        language: String,
        tone: String
    ): ResultWrapper<String> {
        return try {
            val response = remoteSource.getResponseFromAPI(
                request = contentRephrasePrompt(
                    content = content,
                    language = language,
                    tone = tone
                ).toAPIRequest(contentRephraseSystemPrompt())
            )
            ResultWrapper.Success(response.candidates.first().content.parts.first().text)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun fixGrammar(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String> {
        return try {
            val response = remoteSource.getResponseFromAPI(
                request = fixGrammarPrompt(
                    content = content,
                    language = language,
                    action = action
                ).toAPIRequest(fixGrammarSystemPrompt())
            )
            ResultWrapper.Success(response.candidates.first().content.parts.first().text)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun quickReply(
        content: String,
        language: String
    ): ResultWrapper<QuickReply> {
        Log.d("QuickReplyModule", "Hit")
        return try {
            val response = remoteSource.getResponseFromAPI(
                request = quickReplyPrompt(content, language)
                    .toAPIRequest(quickReplySystemPrompt())
            )

            Log.d("QuickReplyModule", "$response")

            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            // Parse the text into lists
            val positive = text.extractBulletList("Positive")
            val neutral = text.extractBulletList("Neutral")
            val negative = text.extractBulletList("Negative")

            ResultWrapper.Success(
                QuickReply(
                    positive = positive,
                    neutral = neutral,
                    negative = negative
                )
            )
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Unknown error")
        }
    }

    private fun String.extractBulletList(section: String): List<String> {
        val regex = Regex("$section:\\s*((?:- .+\\n?)+)", RegexOption.IGNORE_CASE)
        val match = regex.find(this)?.groups?.get(1)?.value ?: return emptyList()
        return match.lines()
            .map { it.removePrefix("-").trim() }
            .filter { it.isNotEmpty() }
    }


    override suspend fun wordTone(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String> {
        return try {
            val response = remoteSource.getResponseFromAPI(
                request = wordTonePrompt(
                    content = content,
                    language = language,
                    action = action
                ).toAPIRequest(wordToneSystemPrompt())
            )
            ResultWrapper.Success(response.candidates.first().content.parts.first().text)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Unknown error")
        }
    }


    override suspend fun aiWritingAssistance(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String> {
        return try {
            val response = remoteSource.getResponseFromAPI(
                request = aiWritingAssistancePrompt(
                    content = content,
                    language = language,
                    action = action
                ).toAPIRequest(aiWritingAssistanceSystemPrompt())
            )
            ResultWrapper.Success(response.candidates.first().content.parts.first().text)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun translate(
        content: String,
        language: String
    ): ResultWrapper<String> {
        return try {
            val response = remoteSource.getResponseFromAPI(
                request = translatePrompt(
                    content = content,
                    language = language
                ).toAPIRequest(translateSystemPrompt())
            )
            ResultWrapper.Success(response.candidates.first().content.parts.first().text)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "Unknown error")
        }
    }
}


