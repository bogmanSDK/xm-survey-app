package com.bohush.surveyapp.data.source.remote

import com.bohush.surveyapp.data.ApiService
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val apiService: ApiService): RemoteDataSource {

    override suspend fun getQuestions(): Response<List<Question>> {
        return apiService.getQuestions()
    }

    override suspend fun submitAnswer(answer: Answer): Response<Unit> {
        return apiService.submitAnswer(answer)
    }
}
