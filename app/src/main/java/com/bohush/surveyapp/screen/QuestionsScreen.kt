package com.bohush.surveyapp.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bohush.surveyapp.R
import kotlinx.coroutines.launch

@Composable
fun QuestionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SurveyViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        uiState.submissionData.userMessage?.let { userMessage ->
            val snackbarText = stringResource(userMessage)
            LaunchedEffect(scaffoldState, viewModel, userMessage, snackbarText) {
                scaffoldState.snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }

        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            CenterAlignedTopAppBarExample(
                uiState = uiState,
                onBack = onBack,
                onNextQuestion = { viewModel.onNextQuestion() },
                onPreviousQuestion = { viewModel.onPreviousQuestion() },
                onPageChanged = { page ->
                    viewModel.currentQuestionIndex = page
                },
                onSubmit = { answer -> viewModel.submitAnswer(answer) },
                onRetry = { viewModel.getQuestions() },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionsContent(
    innerPadding: PaddingValues,
    uiState: SurveyViewState,
    onPreviousQuestion: () -> Int,
    onNextQuestion: () -> Int,
    onPageChanged: (page: Int) -> Unit,
    onSubmit: (answer: String) -> Unit,

    ) {

    val pagerState = rememberPagerState(pageCount = { uiState.items.size })

    var textValue by remember { mutableStateOf("") }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            textValue = ""
            onPageChanged(page)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    id = R.string.questions_submitted, uiState.submissionData.totalSubmitted
                ), modifier = Modifier.padding(horizontal = 16.dp), fontSize = 16.sp
            )
        }
        HorizontalPager(
            state = pagerState, contentPadding = innerPadding, modifier = Modifier.weight(0.8f)
        ) { page ->

            val localQuestionModel = uiState.items[page]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = localQuestionModel.question,
                    modifier = Modifier.padding(top = 64.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(64.dp))
                if (localQuestionModel.isSubmitted) {
                    Text(
                        text = localQuestionModel.submittedAnswer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp, top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                } else {
                    TextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp, top = 8.dp),
                        label = { Text(stringResource(id = R.string.input_answer_hint_text)) },
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(64.dp))

                SubmitButton(
                    isSubmitted = localQuestionModel.isSubmitted,
                    isWaitingAnswerSubmission = uiState.submissionData.isWaitingAnswerSubmission,
                    onClick = {
                        val answer = textValue
                        onSubmit(answer)
                    })
            }
        }

        BottomButtonSection(
            onNextQuestion = onNextQuestion,
            onPreviousQuestion = onPreviousQuestion,
            pagerState = pagerState,
            currentQuestionIndex = uiState.currentQuestionIndex,
            questionsItemSize = uiState.items.size,
        )
    }
}

@Composable
fun LoadingIndicator(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
fun SubmitButton(
    isSubmitted: Boolean,
    isWaitingAnswerSubmission: Boolean,
    onClick: () -> Unit,
) {
    val buttonText = if (isSubmitted) R.string.submitted_button else R.string.submit_button
    Button(
        onClick = onClick,
        enabled = !isSubmitted && !isWaitingAnswerSubmission,
    ) {
        Text(
            stringResource(id = buttonText),
            modifier = Modifier.padding(horizontal = 42.dp, vertical = 4.dp),
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomButtonSection(
    onPreviousQuestion: () -> Int,
    onNextQuestion: () -> Int,
    pagerState: PagerState,
    currentQuestionIndex: Int,
    questionsItemSize: Int,
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), onClick = {
                val page = onPreviousQuestion()
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page)
                }
            }, enabled = currentQuestionIndex > 0
        ) {
            Text(stringResource(id = R.string.previous_button))
        }

        Spacer(modifier = Modifier.width(32.dp))

        Button(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), onClick = {
                val page = onNextQuestion()
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page)
                }
            }, enabled = currentQuestionIndex < questionsItemSize - 1
        ) {
            Text(
                stringResource(id = R.string.next_button),
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBarExample(
    onBack: () -> Unit,
    uiState: SurveyViewState,
    onPreviousQuestion: () -> Int,
    onNextQuestion: () -> Int,
    onRetry: () -> Unit,
    onPageChanged: (page: Int) -> Unit,
    onSubmit: (answer: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.questions_count,
                            if (uiState.items.isNotEmpty()) uiState.currentQuestionIndex + 1
                            else uiState.currentQuestionIndex,
                            uiState.items.size
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        if (!uiState.isError) {
            QuestionsContent(
                innerPadding,
                uiState = uiState,
                onNextQuestion = onNextQuestion,
                onPreviousQuestion = onPreviousQuestion,
                onPageChanged = onPageChanged,
                onSubmit = onSubmit,
            )
        } else {
            RetryContent(onRetry = onRetry)
        }
    }
}

@Composable
fun RetryContent(
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                id = R.string.error_receiving_questions,
            ), textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRetry,
        ) {
            Text(
                stringResource(id = R.string.retry_button), fontSize = 16.sp
            )
        }
    }
}

