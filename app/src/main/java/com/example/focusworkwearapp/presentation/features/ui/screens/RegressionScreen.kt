package com.example.focusworkwearapp.presentation.features.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.focusworkwearapp.BuildConfig
import com.example.focusworkwearapp.R
import com.example.focusworkwearapp.presentation.common.Result
import com.example.focusworkwearapp.presentation.features.navigation.Navigators
import com.example.focusworkwearapp.presentation.features.ui.viewmodel.MainScreenEvents
import com.example.focusworkwearapp.presentation.features.ui.viewmodel.MainViewModel
import com.example.focusworkwearapp.presentation.theme.Orange
import com.example.focusworkwearapp.utils.Questions
import com.example.focusworkwearapp.utils.questionList
import com.example.focusworkwearapp.utils.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegressionScreen(
    navHostController: NavHostController, viewModel: MainViewModel = hiltViewModel()
) {
    var currentIndex by remember { mutableStateOf(0) }
    val scroll = rememberScrollState()
    val questionList = questionList
    val addQuestionList: MutableList<Questions> by rememberSaveable { mutableStateOf(mutableListOf()) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.RECORD_AUDIO
    )

    LaunchedEffect(key1 = true) {
        viewModel.addQuestionEventFlow.collectLatest {
            isLoading = when (it) {
                is Result.Success -> {
                    context.showToast(it.data)
                    navHostController.navigateUp()
                    false
                }
                is Result.Failure -> {
                    context.showToast(
                        it.msg.message ?: context.getString(R.string.something_went_wrong)
                    )
                    false
                }
                Result.Loading -> {
                    true
                }
            }
        }
    }

    SideEffect {
        permissionState.launchPermissionRequest()
    }

    val speechRecognizerLauncher =
        rememberLauncherForActivityResult(contract = SpeechRecognizerContract(), onResult = {
            viewModel.changeTextValue(it.toString())
        })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(vertical = 10.dp, horizontal = 5.dp)
    ) {
        if (currentIndex == 6) Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.thank_you_response), style = TextStyle(
                    color = Orange, fontSize = 12.sp, fontWeight = FontWeight.Bold
                ), textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Button(
                onClick = {
                    viewModel.onEvent(
                        MainScreenEvents.AddQuestionEvent(
                            addQuestionList
                        )
                    )

                }, modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(text = stringResource(id = R.string.ok), color = Color.White, fontSize = 12.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (currentIndex < 6) {
                Text(
                    text = "${questionList[currentIndex].id}).  ${questionList[currentIndex].question}",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (viewModel.state.text != null && viewModel.state.text != "null") {
                    Text(
                        text = viewModel.state.text?.replace(oldChar = '[', newChar = ' ')
                            ?.replace(']', ' ') ?: "",
                        fontSize = 10.sp,
                        color = Orange,
                    )
                }
            }
        }

        if (currentIndex < 6) Button(
            onClick = {
                if (viewModel.state.text != null && viewModel.state.text != "null") {
                    addQuestionList.add(
                        Questions(
                            questionList[currentIndex].id,
                            questionList[currentIndex].question,
                            viewModel.state.text?.replace(oldChar = '[', newChar = ' ')
                                ?.replace(']', ' ') ?: context.getString(R.string.no_answer)
                        )
                    )
                    currentIndex++
                    viewModel.state.text = null
                } else {
                    context.showToast(context.getString(R.string.answer_this_question))
                }
            },
            modifier = Modifier
                .size(50.dp)
                .padding(bottom = 30.dp, end = 10.dp)
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = "Next ->", style = TextStyle(
                    color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold

                )
            )
        }

        if (currentIndex < 6)
            Icon(Icons.Default.Mic,
            contentDescription = "",
            tint = Orange,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable {
                    if (permissionState.status.isGranted) speechRecognizerLauncher.launch(Unit)
                    else permissionState.launchPermissionRequest()
                })

    }

    BackHandler {
        if (currentIndex == 6) viewModel.onEvent(
            MainScreenEvents.AddQuestionEvent(
                addQuestionList
            )
        )
        else navHostController.navigateUp()
    }

}

class SpeechRecognizerContract : ActivityResultContract<Unit, ArrayList<String>?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speak_something)
        )

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<String>? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
    }
}

