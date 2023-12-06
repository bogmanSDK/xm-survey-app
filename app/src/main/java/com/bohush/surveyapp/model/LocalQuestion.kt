package com.bohush.surveyapp.model

data class LocalQuestion(
    val id: Int,
    val question: String,
    var submittedAnswer: String = "",
    var isSubmitted: Boolean = false,
)
