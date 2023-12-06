package com.bohush.surveyapp.data

import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import kotlinx.coroutines.flow.Flow

interface SurveyRepository {
    suspend fun getQuestions(): Flow<DataResult<List<Question>?>>
    suspend fun submitAnswer(answer: Answer): DataResult<Unit>
}