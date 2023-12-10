package com.bohush.surveyapp.screen

import com.bohush.surveyapp.FakeSurveyRepository
import com.bohush.surveyapp.MainCoroutineRule
import com.bohush.surveyapp.model.LocalQuestion
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Unit tests for the implementation of [SurveyViewModel]
 */
@ExperimentalCoroutinesApi
class SurveyViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var repository: FakeSurveyRepository

    private val enteredAnswer = "Nike"
    private val successMessageId = 2131689501
    private val errorMessageId = 2131689500

    private val questionFirst = LocalQuestion(id = 1, question = "What is your favorite brand?")
    private val questionSecond = LocalQuestion(id = 2, question = "What is your favorite color?")
    private val questionLast = LocalQuestion(id = 3, question = "What is your favorite sport?")

    private fun getSurveyViewModel(): SurveyViewModel {
        return SurveyViewModel(repository)
    }

    @Before
    fun setupViewModel() {
        repository = FakeSurveyRepository()
    }

    private fun getExpectedQuestions(): List<LocalQuestion> {
        return listOf(questionFirst, questionSecond, questionLast)
    }

    @Test
    fun surveyViewModel_repositoryError() = runTest {
        repository.setShouldThrowError(true)
        val uiState = getSurveyViewModel().uiState.first()
        assertThat(uiState.items).isEmpty()
        assertThat(uiState.isError).isTrue()
    }

    @Test
    fun getQuestionsFromRepositoryAndLoadIntoView() = runTest {

        val expectedQuestions = getExpectedQuestions()
        repository.setShouldThrowError(false)

        val uiState = getSurveyViewModel().uiState.first()
        assertThat(uiState.items).isEqualTo(expectedQuestions)
        assertThat(uiState.isLoading).isEqualTo(false)
        assertThat(uiState.isError).isEqualTo(false)
    }

    @Test
    fun loadQuestions_loading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        var isLoading: Boolean? = true
        val job = launch {
            getSurveyViewModel().uiState.collect {
                isLoading = it.isLoading
            }
        }

        assertThat(isLoading).isTrue()

        advanceUntilIdle()

        assertThat(isLoading).isFalse()
        job.cancel()
    }

    @Test
    fun showNextQuestion_increaseQuestionIndex() = runTest {
        val viewModel = getSurveyViewModel()
        assertThat(viewModel.uiState.first().currentQuestionIndex).isEqualTo(0)

        viewModel.onNextQuestion()

        val uiState = viewModel.uiState.first()
        assertThat(uiState.currentQuestionIndex).isEqualTo(1)
    }

    @Test
    fun showPreviousQuestion_decreaseQuestionIndex() = runTest {
        val viewModel = getSurveyViewModel()
        viewModel.onNextQuestion()
        assertThat(viewModel.uiState.first().currentQuestionIndex).isEqualTo(1)

        viewModel.onPreviousQuestion()

        val uiState = viewModel.uiState.first()
        assertThat(uiState.currentQuestionIndex).isEqualTo(0)
    }

    @Test
    fun surveyViewModel_countSubmittedAnswers() = runTest {

        val viewModel = getSurveyViewModel()
        assertThat(viewModel.uiState.first().submissionData.totalSubmitted).isEqualTo(0)

        viewModel.submitAnswer(enteredAnswer)

        val uiState = viewModel.uiState.first()
        assertThat(uiState.submissionData.isSubmissionSuccessful).isTrue()
        assertThat(uiState.submissionData.isWaitingAnswerSubmission).isFalse()
        assertThat(uiState.submissionData.totalSubmitted).isEqualTo(1)
    }

    @Test
    fun submitAnswer_showSuccessMessage() = runTest {
        val viewModel = getSurveyViewModel()

        viewModel.submitAnswer(enteredAnswer)

        val uiState = viewModel.uiState.first()
        assertThat(uiState.submissionData.userMessage).isEqualTo(successMessageId)
    }

    @Test
    fun submitAnswer_showErrorMessage() = runTest {
        repository.setShouldThrowError(true)

        val viewModel = getSurveyViewModel()
        viewModel.submitAnswer(enteredAnswer)

        val uiState = viewModel.uiState.first()
        assertThat(uiState.submissionData.userMessage).isEqualTo(errorMessageId)
    }

    @Test
    fun submitAnswer_shouldSaveAnswerInMemory() = runTest {
        repository.setShouldThrowError(false)

        val viewModel = getSurveyViewModel()
        viewModel.submitAnswer(enteredAnswer)

        val uiState = viewModel.uiState.first()
        assertThat(uiState.items[0].submittedAnswer).isEqualTo(enteredAnswer)
    }
}