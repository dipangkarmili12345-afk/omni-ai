package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.util.AppStrings
import com.example.ui.viewmodel.OmniViewModel

data class SubscriptionPlan(
    val id: String,
    val productId: String,
    val titleKey: String,
    val priceDisplay: String,
    val durationText: String,
    val savingsBadge: String? = null,
    val isPopular: Boolean = false
)

@Composable
fun PremiumScreen(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userPrefs by viewModel.userPreferences.collectAsState()
    val lang = userPrefs.language

    val plans = listOf(
        SubscriptionPlan(
            id = "weekly",
            productId = "omni_sub_weekly",
            titleKey = "weekly",
            priceDisplay = "₹149 / $1.99",
            durationText = "Billed weekly, cancel anytime"
        ),
        SubscriptionPlan(
            id = "monthly",
            productId = "omni_sub_monthly",
            titleKey = "monthly",
            priceDisplay = "₹499 / $5.99",
            durationText = "Billed monthly, cancel anytime",
            savingsBadge = "MOST POPULAR",
            isPopular = true
        ),
        SubscriptionPlan(
            id = "yearly",
            productId = "omni_sub_yearly",
            titleKey = "yearly",
            priceDisplay = "₹3,999 / $49.99",
            durationText = "Billed yearly, save 35%",
            savingsBadge = "BEST VALUE"
        )
    )

    var selectedPlanId by remember { mutableStateOf("monthly") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("premium_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF312E81), Color(0xFF1E1B4B), Color(0xFF0F172A))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEC4899))),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = AppStrings.get("upgrade_headline", lang),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = AppStrings.get("upgrade_sub", lang),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )

                    if (userPrefs.isPremium) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "ACTIVE: ${userPrefs.premiumPlan.uppercase()} PLAN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }

        // Feature Comparison Checklist
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Everything in Premium",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    BenefitRow(
                        icon = Icons.Default.FlashOn,
                        title = "Unlimited Daily AI Messages",
                        subtitle = "No 30-message daily cap. Chat as much as you need."
                    )
                    BenefitRow(
                        icon = Icons.Default.Block,
                        title = "100% Ad-Free Experience",
                        subtitle = "Removes all banners, interstitials, and video ads."
                    )
                    BenefitRow(
                        icon = Icons.Default.Star,
                        title = "All 5 Frontier AI Models Unlocked",
                        subtitle = "Gemini 2.5 Flash, ChatGPT-4o, Claude 3.5 Sonnet, DeepSeek R1 & Grok 2."
                    )
                    BenefitRow(
                        icon = Icons.Default.Image,
                        title = "High-Resolution AI Image Studio",
                        subtitle = "Photorealistic, Cyberpunk, and 3D render generation."
                    )
                    BenefitRow(
                        icon = Icons.Default.Mic,
                        title = "Real-Time Voice Chat & Synthesis",
                        subtitle = "Fluid speech-to-speech interaction across all models."
                    )
                }
            }
        }

        // Subscription Plans (Weekly, Monthly, Yearly)
        item {
            Text(
                text = "Choose Your Subscription Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(plans.size) { index ->
            val plan = plans[index]
            val isSelected = selectedPlanId == plan.id

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        2.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { selectedPlanId = plan.id }
                    .padding(16.dp)
                    .testTag("plan_card_${plan.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppStrings.get(plan.titleKey, lang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (plan.savingsBadge != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (plan.isPopular) Color(0xFF6366F1) else Color(0xFF10B981))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = plan.savingsBadge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = plan.durationText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = plan.priceDisplay,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Subscribe / Manage Button
        item {
            val selectedPlan = plans.find { it.id == selectedPlanId } ?: plans[1]

            Button(
                onClick = {
                    val planName = selectedPlan.id.replaceFirstChar { it.uppercase() }
                    viewModel.upgradeSubscription(planName)
                    Toast.makeText(context, "Google Play Billing: Subscribed to $planName Plan!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("subscribe_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (userPrefs.isPremium) "Switch Plan with Google Play" else AppStrings.get("subscribe_now", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }

        if (userPrefs.isPremium) {
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.cancelSubscription()
                        Toast.makeText(context, "Subscription downgraded to Free", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("cancel_subscription_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel Subscription (Revert to Free)", color = Color.Gray)
                }
            }
        }

        item {
            OutlinedButton(
                onClick = {
                    viewModel.restorePurchases()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("restore_purchases_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Restore Google Play Purchases", fontSize = 13.sp)
            }
        }

        item {
            Text(
                text = "Subscriptions automatically renew via Google Play Billing unless cancelled at least 24 hours before end of period.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun BenefitRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
