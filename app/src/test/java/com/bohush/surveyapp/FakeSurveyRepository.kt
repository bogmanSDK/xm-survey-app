package com.bohush.surveyapp

import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.data.SurveyRepository
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSurveyRepository : SurveyRepository {

    private val questionFirst = Question(id = 1, question = "What is your favorite brand?")
    private val questionSecond = Question(id = 2, question = "What is your favorite color?")
    private val questionLast = Question(id = 3, question = "What is your favorite sport?")

    private var shouldThrowError = false

    private val _savedTasks = MutableStateFlow<DataResult<List<Question>>>(
        DataResult.Success(listOf(questionFirst, questionSecond, questionLast))
    )
    private val savedTasks: StateFlow<DataResult<List<Question>>> = _savedTasks.asStateFlow()

    override suspend fun getQuestions(): Flow<DataResult<List<Question>>> {
        if (shouldThrowError) {
            throw Exception("Load exception")
        } else {
            return savedTasks
        }
    }

    override suspend fun submitAnswer(answer: Answer): DataResult<Unit> {
        return if (shouldThrowError) {
            DataResult.Error("Submission failed")
        } else {
            DataResult.Success(Unit)
        }
    }

    fun setShouldThrowError(value: Boolean) {
        shouldThrowError = value
    }
}
