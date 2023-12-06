package com.bohush.surveyapp.data

import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/questions")
    suspend fun getQuestions(): Response<List<Question>>

    @POST("/question/submit")
    suspend fun submitAnswer(@Body answer: Answer): Response<Unit>
}
