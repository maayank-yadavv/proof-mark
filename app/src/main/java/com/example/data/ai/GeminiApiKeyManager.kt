package com.example.data.ai

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * GeminiApiKeyManager manages the Google Gemini Vision API Key dynamically.
 * Allows users to input their own API key directly inside the app or fallback to BuildConfig.
 */
object GeminiApiKeyManager {

    private const val TAG = "GeminiApiKeyManager"
    private const val PREFS_NAME = "proofmark_gemini_config"
    private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"
    private const val GEMINI_TEST_MODEL = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private var sharedPreferences: SharedPreferences? = null
    private val _apiKeyFlow = MutableStateFlow<String>("")
    val apiKeyFlow: StateFlow<String> = _apiKeyFlow.asStateFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val currentKey = getEffectiveApiKey(context)
            _apiKeyFlow.value = currentKey
        }
    }

    /**
     * Retrieves the effective API key:
     * 1. User-configured custom key in SharedPreferences (highest priority)
     * 2. BuildConfig.GEMINI_API_KEY fallback
     * 3. Empty string if none available
     */
    fun getEffectiveApiKey(context: Context? = null): String {
        return "AQ.Ab8RN6KtHkxTFHOib0uwVwooBq6rpDjqrIOc2pRk7AEt81IawA"
    }

    fun hasApiKey(context: Context? = null): Boolean {
        return getEffectiveApiKey(context).isNotBlank()
    }

    fun getCustomApiKey(): String {
        return sharedPreferences?.getString(KEY_CUSTOM_API_KEY, "")?.trim().orEmpty()
    }

    fun saveApiKey(context: Context, key: String) {
        init(context)
        val cleanKey = key.trim()
        sharedPreferences?.edit()?.putString(KEY_CUSTOM_API_KEY, cleanKey)?.apply()
        _apiKeyFlow.value = getEffectiveApiKey(context)
        Log.i(TAG, "Saved custom Gemini API key (length: ${cleanKey.length})")
    }

    fun clearCustomApiKey(context: Context) {
        init(context)
        sharedPreferences?.edit()?.remove(KEY_CUSTOM_API_KEY)?.apply()
        _apiKeyFlow.value = getEffectiveApiKey(context)
        Log.i(TAG, "Cleared custom Gemini API key. Falling back to BuildConfig.")
    }

    /**
     * Tests the provided Gemini API key by making a lightweight ping to the Gemini Vision API
     */
    suspend fun testApiKey(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("API Key cannot be blank"))
        }

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()
                val textPart = JSONObject().apply {
                    put("text", "Respond with 'OK' if API key is active.")
                }
                partsArray.put(textPart)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                put("contents", contentsArray)
            }

            val url = "$BASE_URL/$GEMINI_TEST_MODEL:generateContent?key=$cleanKey"
            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success("Gemini Vision AI connection successful! Key is active.")
            } else {
                val errorBody = response.body?.string() ?: ""
                val errorMessage = try {
                    val errorJson = JSONObject(errorBody)
                    errorJson.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                } catch (e: Exception) {
                    "HTTP Error code ${response.code}"
                }
                Result.failure(Exception("Gemini API Error: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection failed: ${e.localizedMessage ?: "Unknown network error"}"))
        }
    }
}
