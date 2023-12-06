package com.bohush.surveyapp.data.source.remote

import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.data.ApiService
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val apiService: ApiService): RemoteDataSource {

    override suspend fun getQuestions(): Response<List<Question>> {
        return apiService.getQuestions()
    }

    override suspend fun submitAnswer(answer: Answer): DataResult<Unit> {
        return try {
            val response = apiService.submitAnswer(answer)
            if (response.isSuccessful) {
                DataResult.Success(Unit)
            } else {
                DataResult.Error("Failed to submit question: ${response.code()}")
            }
        } catch (e: Exception) {
            DataResult.Error("Error submitting question: ${e.message}")
        }
    }
}
