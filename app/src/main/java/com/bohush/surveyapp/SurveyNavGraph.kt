/*
 * Copyright 2022 The Android Open Source Project
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

import StartSurveyScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bohush.surveyapp.screen.QuestionsScreen

@Composable
fun SurveyNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SurveyDestinations.START_SURVEY_ROUTE,
    navActions: SurveyNavigationActions = remember(navController) {
        SurveyNavigationActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            SurveyDestinations.START_SURVEY_ROUTE,
        ) {
            StartSurveyScreen(onStartSurvey = { navActions.navigateToQuestionsScreen() })
        }

        composable(
            SurveyDestinations.QUESTIONS_ROUTE,
        ) {
            QuestionsScreen(onBack = { navController.popBackStack() })
        }
    }
}
