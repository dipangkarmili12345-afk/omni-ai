package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class AiModelType(
    val id: String,
    val displayName: String,
    val provider: String,
    val description: String,
    val tag: String,
    val accentColor: Color,
    val iconRes: String = ""
) {
    GEMINI(
        id = "gemini",
        displayName = "Gemini 1.5",
        provider = "Google",
        description = "Multimodal reasoning, real-time knowledge & speed",
        tag = "Google AI",
        accentColor = Color(0xFF1A73E8)
    ),
    CHATGPT(
        id = "chatgpt",
        displayName = "ChatGPT (GPT-4o)",
        provider = "OpenAI",
        description = "Balanced versatile knowledge, writing & problem solving",
        tag = "OpenAI",
        accentColor = Color(0xFF10A37F)
    ),
    CLAUDE(
        id = "claude",
        displayName = "Claude 3.5 Sonnet",
        provider = "Anthropic",
        description = "Exceptional nuance, coding precision & natural prose",
        tag = "Anthropic",
        accentColor = Color(0xFFD97706)
    ),
    DEEPSEEK(
        id = "deepseek",
        displayName = "DeepSeek R1/V3",
        provider = "DeepSeek",
        description = "Deep chain-of-thought reasoning, math & logic mastery",
        tag = "DeepSeek",
        accentColor = Color(0xFF4F46E5)
    ),
    GROK(
        id = "grok",
        displayName = "Grok 2",
        provider = "xAI",
        description = "Unfiltered directness, humor, live context & witty depth",
        tag = "xAI",
        accentColor = Color(0xFF0F172A)
    );

    companion object {
        fun fromId(id: String): AiModelType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GEMINI
        }
    }
}
