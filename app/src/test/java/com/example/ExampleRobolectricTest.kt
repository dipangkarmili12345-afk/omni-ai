package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Omni AI", appName)
  }

  @Test
  fun `verify 5 frontier ai models exist`() {
    val models = com.example.data.model.AiModelType.entries
    assertEquals(5, models.size)
    val ids = models.map { it.id }
    org.junit.Assert.assertTrue(ids.contains("gemini"))
    org.junit.Assert.assertTrue(ids.contains("chatgpt"))
    org.junit.Assert.assertTrue(ids.contains("claude"))
    org.junit.Assert.assertTrue(ids.contains("deepseek"))
    org.junit.Assert.assertTrue(ids.contains("grok"))
  }

  @Test
  fun `verify tools exist`() {
    val tools = com.example.data.model.ToolType.entries
    assertEquals(8, tools.size)
  }

  @Test
  fun `verify user free quota calculation`() {
    val prefs = com.example.data.local.UserPreferences(
      messagesUsedToday = 5,
      bonusMessages = 10,
      isPremium = false
    )
    assertEquals(40, prefs.dailyLimit) // 30 + 10
    assertEquals(35, prefs.remainingMessages)
    org.junit.Assert.assertTrue(prefs.canSendMessage)
  }
}
