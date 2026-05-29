package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.VideoEntity
import com.example.ui.screens.VideoGridCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val mockVideo = VideoEntity(
        id = "Ke7g77mRhyo",
        title = "Apple Vision Pro Review: Tomorrow's Tech Today",
        channelName = "Marques Brownlee",
        description = "Apple Vision Pro has been on my face for a week.",
        publishedAt = "2024-02-06",
        duration = "29:56",
        viewCount = "18.5M views",
        category = "Tech"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        VideoGridCard(video = mockVideo, onClick = {})
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
