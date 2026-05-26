package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

// ==========================================
// GEMINI REST SCHEMA
// ==========================================

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

// ==========================================
// RETROFIT API SERVICE DEFINITION
// ==========================================

interface GeminiApiGateway {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun consultGeminiModel(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// ==========================================
// SINGLETON CLIENT GATEWAY
// ==========================================

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val restAdapter: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: GeminiApiGateway by lazy {
        restAdapter.create(GeminiApiGateway::class.java)
    }

    /**
     * Attempts to query Gemini for an answer.
     * Fallback responses provide a delightful simulation of a Nepalese NGO support agent
     * when the GEMINI_API_KEY is not defined.
     */
    suspend fun fetchResponse(userPrompt: String, history: List<GeminiContent> = emptyList()): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // Detect unset placeholder or empty keys
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            return generateOfflineAnswer(userPrompt)
        }

        val systemInstruction = GeminiContent(
            parts = listOf(
                GeminiPart(
                    text = "You are 'Sagar', an artificial intelligence representative of Future Minds Nepal, " +
                           "a leading Nepalese youth NGO focused on financial transparency (verified by audit reports), digital literacy, " +
                           "women skill development, scholarship applications, and flood safety early warnings in the Terai regions. " +
                           "Keep your communications brief, polite, youth-conscious, and supportive. Use occasional Nepali words like " +
                           "'Namaste', 'Dhanyabad', or 'Sathi' (friend) to align with standard branding. Always emphasize hope and social progress."
                )
            )
        )

        // Compile combined messaging thread
        val contents = history + GeminiContent(parts = listOf(GeminiPart(text = userPrompt)), role = "user")
        val requestBody = GeminiRequest(contents = contents, systemInstruction = systemInstruction)

        return try {
            val response = api.consultGeminiModel(apiKey, requestBody)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I apologize, sathi, but my system didn't produce a direct response content. How else can Future Minds help you today?"
        } catch (e: Exception) {
            "Connection issue: ${e.localizedMessage}. Entering local simulation:\n\n${generateOfflineAnswer(userPrompt)}"
        }
    }

    /**
     * Offline backup rule dictionary tailored to Future Minds Nepal NGO services.
     */
    private fun generateOfflineAnswer(prompt: String): String {
        val msg = prompt.lowercase()
        return when {
            msg.contains("namaste") || msg.contains("hello") || msg.contains("hi") -> {
                "Namaste sathi! 🙏 Welcome to Future Minds Nepal Support. I am Sagar, your local NGO guide. " +
                "Ask me about our Rural Education campaigns, active volunteering hours, how to apply for scholarships, " +
                "or how to check our eSewa donation tracking page!"
            }
            msg.contains("volunteer") || msg.contains("join") || msg.contains("work") -> {
                "Dhanyabad for wanting to serve Nepal's youth! You can head over to our 'Volunteers' tab within the bottom navigation bar. " +
                "Fill in your skills (e.g., teaching, coding) and weekend availability. Once approved by the administrator, " +
                "your official NGO Digital ID card will download, and you can record service hours!"
            }
            msg.contains("donate") || msg.contains("esewa") || msg.contains("khalti") || msg.contains("money") || msg.contains("payment") -> {
                "Future Minds handles donation tracking with absolute transparency! Browse to our 'Donate' screen to find " +
                "official Nepali QR Codes (eSewa, Khalti, Fonepay) or direct bank routing variables. Your donations will automatically " +
                "show up in our public real-time Ledger and progress bar!"
            }
            msg.contains("scholarship") || msg.contains("support") || msg.contains("student") -> {
                "We provide full scholarships for students from marginalized backgrounds. Navigate to the 'Scholarships' section, " +
                "give your target school and family income brackets, upload standard files, and monitor status live. Admins approve/deny " +
                "these queries dynamically!"
            }
            msg.contains("transparency") || msg.contains("audit") || msg.contains("income") || msg.contains("financial") || msg.contains("budget") -> {
                "Transparency is our ultimate principle! In the 'Financial Reports' tab, we display a detailed balance sheet showing " +
                "our direct Income and Expense logs, plus custom interactive visual graphs of project spending. Absolute trust guarantees absolute impact!"
            }
            msg.contains("project") || msg.contains("educat") || msg.contains("literacy") -> {
                "Future Minds runs massive youth camps! These include 'Rural Education Empowerment' in Solukhumbu & Humla, " +
                "'Digital Literacy' coding camps for young girls, and Terai Pre-Monsoon Flood warning systems. Head to the 'Projects' tab to read impact diaries!"
            }
            else -> {
                "Namaste sathi! That is an interesting query regarding Nepal's progress. " +
                "To access full active Gemini AI answers, please configure your `GEMINI_API_KEY` securely in the *AI Studio Secrets Panel*! " +
                "Nonetheless, our support team is always standing by to empower you. Dhanyabad!"
            }
        }
    }
}
