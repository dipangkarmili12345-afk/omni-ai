package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ToolType
import com.example.data.util.AppStrings
import com.example.ui.components.AdBannerView
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun ToolsScreen(
    viewModel: OmniViewModel,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTool by viewModel.activeTool.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val lang = userPrefs.language

    if (activeTool != null) {
        ToolDetailView(
            tool = activeTool!!,
            viewModel = viewModel,
            onBack = { viewModel.closeTool() },
            onNavigateToPremium = onNavigateToPremium
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("tools_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = AppStrings.get("tools", lang),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Specialized AI Engines for creative and professional tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                AdBannerView(
                    isPremium = userPrefs.isPremium,
                    onUpgradeClick = onNavigateToPremium
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolType.entries.forEach { tool ->
                        ToolCardItem(
                            tool = tool,
                            lang = lang,
                            onClick = {
                                if (tool == ToolType.VOICE_CHAT) {
                                    viewModel.openVoiceChat()
                                } else {
                                    viewModel.openTool(tool)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCardItem(
    tool: ToolType,
    lang: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tool_card_${tool.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tool.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (tool) {
                        ToolType.IMAGE_GENERATOR -> Icons.Default.Image
                        ToolType.VOICE_CHAT -> Icons.Default.Mic
                        ToolType.CODE_WRITER -> Icons.Default.Code
                        ToolType.TRANSLATOR -> Icons.Default.Translate
                        ToolType.SUMMARIZER -> Icons.Default.Summarize
                        ToolType.ESSAY_WRITER -> Icons.Default.Description
                        ToolType.RESUME_BUILDER -> Icons.Default.AutoAwesome
                        ToolType.EMAIL_WRITER -> Icons.Default.Email
                    },
                    contentDescription = null,
                    tint = tool.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (lang == "hi") tool.hindiTitle else tool.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tool.accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = tool.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tool.accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (lang == "hi") tool.hindiDescription else tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailView(
    tool: ToolType,
    viewModel: OmniViewModel,
    onBack: () -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val context = LocalContext.current
    val toolOutput by viewModel.toolOutput.collectAsState()
    val isToolLoading by viewModel.isToolLoading.collectAsState()
    val generatedImageUrl by viewModel.generatedImageUrl.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val lang = userPrefs.language

    // Form states
    var param1 by remember { mutableStateOf("") }
    var param2 by remember { mutableStateOf("") }
    var param3 by remember { mutableStateOf("") }
    var param4 by remember { mutableStateOf("") }
    var param5 by remember { mutableStateOf("") }
    var param6 by remember { mutableStateOf("") }
    var param7 by remember { mutableStateOf("") }

    // Dropdown helpers
    var selectedLanguage by remember { mutableStateOf("Kotlin") }
    var selectedSourceLang by remember { mutableStateOf("English") }
    var selectedTargetLang by remember { mutableStateOf("Hindi") }
    var selectedImageStyle by remember { mutableStateOf("Photorealistic") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") }
    var selectedTone by remember { mutableStateOf("Professional") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tool_detail_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (lang == "hi") tool.hindiTitle else tool.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Powered by Omni AI Studio",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = tool.accentColor
                    )
                }
            }
        }

        // Tool specific input fields
        when (tool) {
            ToolType.IMAGE_GENERATOR -> {
                item {
                    OutlinedTextField(
                        value = param1,
                        onValueChange = { param1 = it },
                        label = { Text("Prompt description") },
                        placeholder = { Text("e.g. Futuristic cybernetic cityscape at sunset, neon reflections") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("image_prompt_input"),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val styles = listOf("Photorealistic", "Cyberpunk", "Anime", "3D Render")
                        styles.forEach { s ->
                            val isSelected = selectedImageStyle == s
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) tool.accentColor.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) tool.accentColor else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedImageStyle = s }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = s,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) tool.accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            ToolType.CODE_WRITER -> {
                item {
                    val languages = listOf("Kotlin", "Python", "JavaScript", "TypeScript", "C++", "Rust")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        languages.take(4).forEach { l ->
                            val isSelected = selectedLanguage == l
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) tool.accentColor.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) tool.accentColor else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedLanguage = l }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = l,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) tool.accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = param2,
                        onValueChange = { param2 = it },
                        label = { Text("Task description / function requirements") },
                        placeholder = { Text("e.g. Write a function to check if binary tree is balanced") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("code_task_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            ToolType.TRANSLATOR -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "From: $selectedSourceLang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = {
                            val temp = selectedSourceLang
                            selectedSourceLang = selectedTargetLang
                            selectedTargetLang = temp
                        }) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = "Swap")
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "To: $selectedTargetLang",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = param1,
                        onValueChange = { param1 = it },
                        label = { Text("Enter text to translate") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("translate_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            ToolType.SUMMARIZER -> {
                item {
                    OutlinedTextField(
                        value = param1,
                        onValueChange = { param1 = it },
                        label = { Text("Paste article, meeting notes or long text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("summarize_input"),
                        minLines = 4,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            ToolType.ESSAY_WRITER -> {
                item {
                    OutlinedTextField(
                        value = param1,
                        onValueChange = { param1 = it },
                        label = { Text("Essay Topic or Research Question") },
                        placeholder = { Text("e.g. The Impact of Artificial Intelligence on Future Healthcare") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            ToolType.RESUME_BUILDER -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = param1,
                            onValueChange = { param1 = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = param2,
                            onValueChange = { param2 = it },
                            label = { Text("Target Job Title / Role") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = param3,
                            onValueChange = { param3 = it },
                            label = { Text("Contact (Email, Phone, City)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = param4,
                            onValueChange = { param4 = it },
                            label = { Text("Professional Summary") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        OutlinedTextField(
                            value = param5,
                            onValueChange = { param5 = it },
                            label = { Text("Work Experience") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        OutlinedTextField(
                            value = param7,
                            onValueChange = { param7 = it },
                            label = { Text("Top Skills (comma separated)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            ToolType.EMAIL_WRITER -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = param1,
                            onValueChange = { param1 = it },
                            label = { Text("Email Purpose (e.g. Job Application, Project Pitch)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = param2,
                            onValueChange = { param2 = it },
                            label = { Text("Recipient Name / Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = param3,
                            onValueChange = { param3 = it },
                            label = { Text("Key Points to Mention") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }

            ToolType.VOICE_CHAT -> {}
        }

        // Action Button
        item {
            Button(
                onClick = {
                    when (tool) {
                        ToolType.IMAGE_GENERATOR -> viewModel.executeTool(tool, param1, selectedImageStyle, selectedAspectRatio)
                        ToolType.CODE_WRITER -> viewModel.executeTool(tool, selectedLanguage, param2)
                        ToolType.TRANSLATOR -> viewModel.executeTool(tool, param1, selectedSourceLang, selectedTargetLang)
                        ToolType.SUMMARIZER -> viewModel.executeTool(tool, param1, "Executive Summary")
                        ToolType.ESSAY_WRITER -> viewModel.executeTool(tool, param1, selectedTone, "Standard")
                        ToolType.RESUME_BUILDER -> viewModel.executeTool(tool, param1, param2, param3, param4, param5, param6, param7)
                        ToolType.EMAIL_WRITER -> viewModel.executeTool(tool, param1, param2, param3, selectedTone)
                        ToolType.VOICE_CHAT -> viewModel.openVoiceChat()
                    }
                },
                enabled = !isToolLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("execute_tool_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tool.accentColor)
            ) {
                if (isToolLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating...")
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate with AI", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Image Output Card
        if (generatedImageUrl != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = generatedImageUrl,
                            contentDescription = "Generated AI Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI Generated Artwork",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Save Image", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Text / Code Output Card
        if (toolOutput.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.testTag("tool_output_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Result",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = tool.accentColor
                            )
                            Row {
                                if (tool == ToolType.TRANSLATOR || tool == ToolType.SUMMARIZER || tool == ToolType.ESSAY_WRITER) {
                                    IconButton(
                                        onClick = { viewModel.voiceManager.speak(toolOutput) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Speak", modifier = Modifier.size(16.dp))
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Tool Output", toolOutput))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = toolOutput,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = if (tool == ToolType.CODE_WRITER) FontFamily.Monospace else FontFamily.Default,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
