package com.bohush.surveyapp.data

import com.bohush.surveyapp.core.BaseApiResponse
import com.bohush.surveyapp.core.DataResult
import com.bohush.surveyapp.data.source.remote.RemoteDataSourceImpl
import com.bohush.surveyapp.model.Answer
import com.bohush.surveyapp.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSourceImpl) :
    BaseApiResponse(), SurveyRepository {

    // Function to get the list of questions from the server
    override suspend fun getQuestions(): Flow<DataResult<List<Question>?>> {
        return flow<DataResult<List<Question>?>> {
            emit(safeApiCall { remoteDataSource.getQuestions() })
        }.flowOn(Dispatchers.IO)
    }

    // Function to submit an answer to a question
    override suspend fun submitAnswer(answer: Answer): DataResult<Unit> {
        return remoteDataSource.submitAnswer(answer)
    }
}
