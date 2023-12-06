package com.bohush.surveyapp.screen

import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.data.SurveyRepository
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class QuestionsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var repository: SurveyRepository

    private val questionFirst = Question(id = 1, question = "What is your favorite brand?")
    private val questionSecond = Question(id = 2, question = "What is your favorite color?")
    private val questionLast = Question(id = 3, question = "What is your favorite sport?")

    private val errorText = "Encountered an issue while attempting to retrieve questions."
    private val retryButtonText = "Retry"
    private val nextButtonText = "Next"
    private val previousButtonText = "Previous"
    private val answerText = "Adidas"
    private val submitButtonText = "Submit"
    private val placeholderText = "Type here for an answer…"

    @Before
    fun setUp() {
        repository = mock(SurveyRepository::class.java)
    }

    private fun setContent() {
        composeTestRule.setContent {
            Surface {
                QuestionsScreen(
                    viewModel = SurveyViewModel(repository),
                    onBack = {}
                )
            }
        }
    }

    private suspend fun mockRepositoryWithQuestions(questions: List<Question>) {
        `when`(repository.getQuestions()).thenReturn(flow {
            emit(DataResult.Success(questions))
        })
    }

    private fun getExpectedQuestions(): List<Question> {
        return listOf(questionFirst, questionSecond, questionLast)
    }

    @Test
    fun displayFirstQuestion_whenRepositoryHasData() = runTest {
        val expectedQuestions = getExpectedQuestions()
        mockRepositoryWithQuestions(expectedQuestions)

        setContent()

        composeTestRule.onNodeWithText(questionFirst.question).assertIsDisplayed()
    }

    @Test
    fun displayErrorScreen_whenRepositoryFailedToFetchData() = runTest {
        val expectedQuestions = emptyList<Question>()
        mockRepositoryWithQuestions(expectedQuestions)

        setContent()

        composeTestRule.onNodeWithText(errorText)
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(retryButtonText)).assertExists()
    }

    @Test
    fun testNextAndPreviousButtonsVisibility() = runTest {
        val expectedQuestions = getExpectedQuestions()
        mockRepositoryWithQuestions(expectedQuestions)

        setContent()

        composeTestRule.onNode(hasText(nextButtonText)).assertExists()
        composeTestRule.onNode(hasText(previousButtonText)).assertExists()
    }

    @Test
    fun testGoToNextQuestion_whenNextButtonPressed() = runTest {
        val expectedQuestions = getExpectedQuestions()
        mockRepositoryWithQuestions(expectedQuestions)

        setContent()

        composeTestRule.onNodeWithText(nextButtonText).performClick()

        composeTestRule.onNodeWithText(questionSecond.question).assertIsDisplayed()
        composeTestRule.onNodeWithText("Questions 2/${getExpectedQuestions().size}")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(previousButtonText).assertIsEnabled()
        composeTestRule.onNodeWithText(nextButtonText).assertIsEnabled()
    }

    @Test
    fun testPreviousButtonDisabled_whenFirstQuestionDisplayed() = runTest {
        val expectedQuestions = getExpectedQuestions()
        mockRepositoryWithQuestions(expectedQuestions)

        setContent()

        composeTestRule.onNodeWithText(previousButtonText).assertIsNotEnabled()
        composeTestRule.onNodeWithText(nextButtonText).assertIsEnabled()
    }

    @Test
    fun testContentBehaviour_whenLastQuestionAchieved() = runTest {
        val expectedQuestions = getExpectedQuestions()
        mockRepositoryWithQuestions(expectedQuestions)

        setContent()

        for (i in 1..2) {
            composeTestRule.onNodeWithText(nextButtonText).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNodeWithText(questionLast.question).assertIsDisplayed()
        composeTestRule.onNodeWithText("Questions ${getExpectedQuestions().size}/${getExpectedQuestions().size}")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(previousButtonText).assertIsEnabled()
        composeTestRule.onNodeWithText(nextButtonText).assertIsNotEnabled()
    }

    @Test
    fun testSubmitAction_whenRepositoryRespondsWithSuccess() = runTest {
        val expectedQuestions = getExpectedQuestions()
        mockRepositoryWithQuestions(expectedQuestions)

        val expectedResult = DataResult.Success(Unit)
        `when`(repository.submitAnswer(Answer(id = 1, answer = answerText))).thenReturn(
            expectedResult
        )

        setContent()

        composeTestRule.onNodeWithText(placeholderText).performTextInput(answerText)
        composeTestRule.onNodeWithText(submitButtonText).performClick()

        composeTestRule.onNodeWithText("Questions submitted: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText(answerText).assertIsDisplayed()
        composeTestRule.onNodeWithText(placeholderText).assertDoesNotExist()
    }
}