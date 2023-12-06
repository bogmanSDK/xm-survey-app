import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class StartSurveyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val startSurveyButtonText = "Start survey"
    private val welcomeText = "Welcome"

    @Test
    fun startSurveyButtonClick_shouldInvokeCallback() = runTest {
        var callbackInvoked = false

        composeTestRule.setContent {
            StartSurveyScreen {
                callbackInvoked = true
            }
        }

        val expectedStartSurveyText = startSurveyButtonText
        val startSurveyButton = composeTestRule.onNodeWithText(expectedStartSurveyText)
        startSurveyButton.performClick()

        assertTrue(callbackInvoked)

        composeTestRule.onNodeWithText(expectedStartSurveyText).assertIsDisplayed()
    }

    @Test
    fun startSurveyScreen_shouldDisplayWelcomeText() = runTest {
        composeTestRule.setContent {
            StartSurveyScreen {}
        }

        val expectedWelcomeText = welcomeText
        val welcomeText = composeTestRule.onNodeWithText(expectedWelcomeText)
        welcomeText.assertExists()
    }
}
