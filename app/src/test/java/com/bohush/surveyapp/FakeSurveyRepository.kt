/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private val _savedTasks = MutableStateFlow<DataResult<List<Question>?>>(
        DataResult.Success(listOf(questionFirst, questionSecond, questionLast))
    )
    private val savedTasks: StateFlow<DataResult<List<Question>?>> = _savedTasks.asStateFlow()

    override suspend fun getQuestions(): Flow<DataResult<List<Question>?>> {
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
