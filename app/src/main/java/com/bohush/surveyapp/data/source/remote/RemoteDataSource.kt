package com.bohush.surveyapp.data.source.remote

import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getQuestions(): Response<List<Question>>
    suspend fun submitAnswer(answer: Answer): DataResult<Unit>
}