package com.bohush.surveyapp

import androidx.navigation.NavHostController
import com.bohush.surveyapp.SurveyScreens.QUESTIONS_SCREEN
import com.bohush.surveyapp.SurveyScreens.START_SURVEY_SCREEN

private object SurveyScreens {
    const val START_SURVEY_SCREEN = "start"
    const val QUESTIONS_SCREEN = "questions"
}

object SurveyDestinations {
    const val START_SURVEY_ROUTE = START_SURVEY_SCREEN
    const val QUESTIONS_ROUTE = QUESTIONS_SCREEN
}

class SurveyNavigationActions(private val navController: NavHostController) {

    fun navigateToQuestionsScreen() {
        navController.navigate(QUESTIONS_SCREEN)
    }
}
