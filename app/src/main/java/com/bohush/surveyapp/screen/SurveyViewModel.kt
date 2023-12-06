package com.bohush.surveyapp.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bohush.surveyapp.R
import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.data.SurveyRepository
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.LocalQuestion
import com.bohush.surveyapp.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SurveyViewState(
    val items: List<LocalQuestion> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val submissionData: SubmissionData = SubmissionData()
)

data class SubmissionData(
    val isWaitingAnswerSubmission: Boolean = false,
    val isSubmissionSuccessful: Boolean = false,
    val totalSubmitted: Int = 0,
    val userMessage: Int? = null,
)

@HiltViewModel
class SurveyViewModel @Inject constructor(private val repository: SurveyRepository) :
    ViewModel() {

    private val _localQuestions: MutableStateFlow<List<LocalQuestion>> =
        MutableStateFlow(emptyList())

    private val _isLoading = MutableStateFlow(false)
    private val _isError = MutableStateFlow(false)
    private val _currentQuestionIndex = MutableStateFlow(0)
    private val _submissionData: MutableStateFlow<SubmissionData> = MutableStateFlow(
        SubmissionData()
    )

    val uiState: StateFlow<SurveyViewState> = combine(
        _localQuestions,
        _isLoading,
        _isError,
        _currentQuestionIndex,
        _submissionData
    ) { localQuestions, isLoading, isError, currentQuestionIndex, submissionData ->
        SurveyViewState(
            items = localQuestions,
            isLoading = isLoading,
            isError = isError,
            currentQuestionIndex = currentQuestionIndex,
            submissionData = submissionData,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SurveyViewState(isLoading = true)
    )


    var currentQuestionIndex: Int
        get() = _currentQuestionIndex.value
        set(value) {
            _currentQuestionIndex.value = value
        }

    init {
        getQuestions()
    }

    fun getQuestions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getQuestions().collect { questionsResult ->
                    if (questionsResult is DataResult.Success) {
                        val questions = questionsResult.data
                        if (questions.isNullOrEmpty()) {
                            _isLoading.value = false
                            _isError.value = true
                        } else {
                            _isLoading.value = false
                            _isError.value = false
                            _localQuestions.value = questions.map { mapToLocalQuestion(it) }
                        }
                    } else {
                        _isLoading.value = false
                        _isError.value = true
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _isError.value = true
            }
        }
    }

    fun onNextQuestion(): Int {
        currentQuestionIndex += 1
        return currentQuestionIndex
    }

    fun onPreviousQuestion(): Int {
        currentQuestionIndex -= 1
        return currentQuestionIndex
    }

    private fun mapToLocalQuestion(question: Question): LocalQuestion {
        return LocalQuestion(
            id = question.id,
            question = question.question,
        )
    }

    fun snackbarMessageShown() {
        _submissionData.value = _submissionData.value.copy(userMessage = null)
    }

    private fun showSnackbarMessage(message: Int) {
        _submissionData.value = _submissionData.value.copy(userMessage = message)
    }

    private fun getTotalSubmittedQuestions(): Int {
        return _localQuestions.value.count { it.isSubmitted }
    }

    fun submitAnswer(enteredAnswer: String) {
        if (enteredAnswer.isNullOrEmpty()) {
            showSnackbarMessage(R.string.empty_answer_message)
            return
        }
        viewModelScope.launch {
            _submissionData.value = _submissionData.value.copy(isWaitingAnswerSubmission = true)
            try {
                val answer = Answer(
                    id = _localQuestions.value[currentQuestionIndex].id,
                    answer = enteredAnswer,
                )
                val result = repository.submitAnswer(answer)
                if (result is DataResult.Success) {
                    showSnackbarMessage(R.string.answer_submitted)
                    _localQuestions.value[currentQuestionIndex].apply {
                        this.isSubmitted = true
                        this.submittedAnswer = enteredAnswer
                    }
                    _submissionData.value = _submissionData.value.copy(
                        isWaitingAnswerSubmission = false,
                        isSubmissionSuccessful = true,
                        totalSubmitted = getTotalSubmittedQuestions()
                    )
                } else {
                    showSnackbarMessage(R.string.answer_submission_failed)
                    _submissionData.value = _submissionData.value.copy(
                        isWaitingAnswerSubmission = false,
                        isSubmissionSuccessful = false,
                    )
                }
            } catch (e: Exception) {
                showSnackbarMessage(R.string.answer_submission_failed)
                _submissionData.value = _submissionData.value.copy(
                    isWaitingAnswerSubmission = false,
                    isSubmissionSuccessful = false
                )
            }
        }
    }
}