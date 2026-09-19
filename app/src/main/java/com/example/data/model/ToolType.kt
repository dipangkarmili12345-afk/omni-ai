package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class ToolType(
    val id: String,
    val title: String,
    val hindiTitle: String,
    val description: String,
    val hindiDescription: String,
    val accentColor: Color,
    val category: String
) {
    IMAGE_GENERATOR(
        id = "image_gen",
        title = "Image Generator",
        hindiTitle = "इमेज जनरेटर",
        description = "Turn ideas & text prompts into high-detail AI visuals with style presets",
        hindiDescription = "अपने टेक्स्ट प्रॉम्प्ट से सुंदर AI तस्वीरें तैयार करें",
        accentColor = Color(0xFFEC4899),
        category = "Creative"
    ),
    VOICE_CHAT(
        id = "voice_chat",
        title = "Voice Chat",
        hindiTitle = "वॉइस चैट",
        description = "Natural hands-free voice conversations with any AI model",
        hindiDescription = "किसी भी AI मॉडल के साथ सीधी बोलकर बातचीत करें",
        accentColor = Color(0xFF06B6D4),
        category = "Interaction"
    ),
    CODE_WRITER(
        id = "code_writer",
        title = "Code Writer",
        hindiTitle = "कोड राइटर",
        description = "Generate bug-free code, algorithms & debug scripts in 8+ languages",
        hindiDescription = "किसी भी प्रोग्रामिंग भाषा में साफ़ और कुशल कोड लिखें",
        accentColor = Color(0xFF10B981),
        category = "Development"
    ),
    TRANSLATOR(
        id = "translator",
        title = "Universal Translator",
        hindiTitle = "यूनिवर्सल ट्रांसलेटर",
        description = "Instant neural translation across Hindi, English, Spanish, German, Japanese & more",
        hindiDescription = "हिंदी, अंग्रेजी और कई भाषाओं में त्वरित और सटीक अनुवाद",
        accentColor = Color(0xFF3B82F6),
        category = "Language"
    ),
    SUMMARIZER(
        id = "summarizer",
        title = "Smart Summarizer",
        hindiTitle = "स्मार्ट सारांश",
        description = "Condense long articles, reports & meeting notes into bullet takeaways",
        hindiDescription = "लंबे लेख, दस्तावेज़ और नोट्स का त्वरित मुख्य सारांश प्राप्त करें",
        accentColor = Color(0xFF8B5CF6),
        category = "Productivity"
    ),
    ESSAY_WRITER(
        id = "essay_writer",
        title = "Essay & Article Writer",
        hindiTitle = "निबंध और लेख लेखक",
        description = "Structured essays, persuasive arguments & research outlines with tone tuning",
        hindiDescription = "गहन शोध और बेहतरीन संरचना के साथ पेशेवर निबंध तैयार करें",
        accentColor = Color(0xFFF59E0B),
        category = "Writing"
    ),
    RESUME_BUILDER(
        id = "resume_builder",
        title = "Resume Builder",
        hindiTitle = "बायोडाटा / रिज़्यूमे मेकर",
        description = "Create ATS-compliant resumes with optimized summaries, skills & experience",
        hindiDescription = "नौकरी के लिए पेशेवर और आकर्षक ATS-फ्रेंडली रिज़्यूमे बनाएं",
        accentColor = Color(0xFF14B8A6),
        category = "Career"
    ),
    EMAIL_WRITER(
        id = "email_writer",
        title = "Email & Letter Writer",
        hindiTitle = "ईमेल और पत्र लेखक",
        description = "Polished professional emails, follow-ups, pitches & proposals in seconds",
        hindiDescription = "नौकरी, बिज़नेस या पर्सनल काम के लिए प्रभावी ईमेल लिखें",
        accentColor = Color(0xFF6366F1),
        category = "Business"
    )
}
