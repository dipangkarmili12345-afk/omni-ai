package com.example.data.model

data class ModelResponseItem(
    val modelType: AiModelType,
    val response: String,
    val latencyMs: Long,
    val isLoading: Boolean = false,
    val error: String? = null,
    val tokensEstimated: Int = 0
)

data class CompareSession(
    val prompt: String,
    val timestamp: Long = System.currentTimeMillis(),
    val results: Map<String, ModelResponseItem> = emptyMap()
)
