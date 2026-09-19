package com.example.data.util

object AppStrings {
    fun get(key: String, lang: String): String {
        val isHi = lang.equals("hi", ignoreCase = true)
        return when (key) {
            "app_name" -> "Omni AI"
            "tagline" -> if (isHi) "सभी AI, एक ही ऐप" else "All AI. One App."
            "home" -> if (isHi) "होम" else "Home"
            "chat" -> if (isHi) "चैट" else "Chat"
            "compare" -> if (isHi) "तुलना" else "Compare"
            "tools" -> if (isHi) "टूल्स" else "Tools"
            "premium" -> if (isHi) "प्रीमियम" else "Premium"
            "profile" -> if (isHi) "प्रोफ़ाइल" else "Profile"
            "daily_messages" -> if (isHi) "दैनिक संदेश कोटा" else "Daily Message Quota"
            "messages_left" -> if (isHi) "संदेश शेष" else "messages left"
            "get_more_messages" -> if (isHi) "+10 संदेश पाएं (विज्ञापन देखें)" else "Get +10 Messages (Watch Ad)"
            "unlimited_premium" -> if (isHi) "असीमित (प्रीमियम सक्रिय)" else "Unlimited (Premium Active)"
            "ask_placeholder" -> if (isHi) "कोई भी सवाल पूछें..." else "Ask anything to Omni AI..."
            "send" -> if (isHi) "भेजें" else "Send"
            "new_chat" -> if (isHi) "नई चैट" else "New Chat"
            "clear_history" -> if (isHi) "इतिहास साफ़ करें" else "Clear History"
            "compare_desc" -> if (isHi) "एक ही सवाल सभी 5 AI मॉडल्स से एक साथ पूछें" else "Send one prompt to all 5 AI models simultaneously"
            "compare_button" -> if (isHi) "सभी AI से तुलना करें" else "Compare All 5 AIs"
            "voice_chat" -> if (isHi) "वॉइस चैट" else "Voice Chat"
            "listening" -> if (isHi) "सुन रहा हूँ... बोलिए" else "Listening... Speak now"
            "tap_to_speak" -> if (isHi) "बोलने के लिए टैप करें" else "Tap to speak"
            "tap_to_stop" -> if (isHi) "रुकने के लिए टैप करें" else "Tap to finish"
            "upgrade_headline" -> if (isHi) "प्रीमियम अनलॉक करें" else "Unlock Omni AI Premium"
            "upgrade_sub" -> if (isHi) "असीमित सवाल, कोई विज्ञापन नहीं, 2x तेज़ गति" else "Unlimited messages, zero ads, priority speed & all 5 models"
            "weekly" -> if (isHi) "साप्ताहिक" else "Weekly"
            "monthly" -> if (isHi) "मासिक" else "Monthly"
            "yearly" -> if (isHi) "वार्षिक" else "Yearly"
            "subscribe_now" -> if (isHi) "अभी सब्सक्राइब करें" else "Subscribe with Google Play"
            "demo_mode_active" -> if (isHi) "स्मार्ट ऑफलाइन इंजन सक्रिय" else "Smart Engine (Offline / Demo Active)"
            "theme" -> if (isHi) "थीम" else "Theme"
            "language" -> if (isHi) "भाषा" else "Language"
            "settings" -> if (isHi) "सेटिंग्स" else "Settings"
            "select_model" -> if (isHi) "मॉडल चुनें" else "Select AI Model"
            else -> key
        }
    }
}
