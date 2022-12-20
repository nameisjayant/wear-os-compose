package com.example.focusworkwearapp.presentation.features.ui.screens

import android.app.RemoteInput
import android.content.Intent
import android.inputmethodservice.Keyboard.Row
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.wear.compose.material.*
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import com.example.focusworkwearapp.R
import com.example.focusworkwearapp.presentation.data.repository.PreferenceStore
import com.example.focusworkwearapp.presentation.features.ui.viewmodel.MainViewModel
import com.example.focusworkwearapp.presentation.service.ServiceHelper
import com.example.focusworkwearapp.presentation.service.StopwatchService
import com.example.focusworkwearapp.presentation.service.StopwatchState
import com.example.focusworkwearapp.presentation.theme.DarkBlue
import com.example.focusworkwearapp.presentation.theme.LightGrey
import com.example.focusworkwearapp.presentation.theme.Navy
import com.example.focusworkwearapp.presentation.theme.Orange
import com.example.focusworkwearapp.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimerScreen(
    viewModel: MainViewModel,
    stopwatchService: StopwatchService
) {
    val data = viewModel.task.value
    val levels by remember { mutableStateOf(listOf(LEVEL1, LEVEL2, LEVEL3)) }
    val context = LocalContext.current
    var levelState by remember { mutableStateOf(LEVEL1) }
    var timer by remember { mutableStateOf("") }
    var setTimerState by remember { mutableStateOf(true) }
    var startTimerState by remember { mutableStateOf(true) }
    val hours by stopwatchService.hours
    val minutes by stopwatchService.minutes
    val seconds by stopwatchService.seconds
    val currentState by stopwatchService.currentState
    val minToLong: Long = if (timer.isNotEmpty()) {
        TimeUnit.MINUTES.toMillis(timer.toLong())
    } else {
        0
    }

    ScalingLazyColumn(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxSize(),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = runBlocking {
                        viewModel.getStringPref(PreferenceStore.title).first()
                    },
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = runBlocking {
                        viewModel.getStringPref(PreferenceStore.des).first()
                    },
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                levels.forEach {
                    CommonRadioButton(
                        selected = it == levelState,
                        title = it,
                        onTitleUpdate = {
                            levelState = it
                        }
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = hours,
                    transitionSpec = { addAnimation() }) {
                    Text(
                        text = hours,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                }
                AnimatedContent(
                    targetState = minutes,
                    transitionSpec = { addAnimation() }) {
                    Text(
                        text = minutes, style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (minutes == "00") White else Orange
                        )
                    )
                }
                AnimatedContent(
                    targetState = seconds,
                    transitionSpec = { addAnimation() }) {
                    Text(
                        text = seconds, style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (seconds == "00") White else Red
                        )
                    )
                }
            }
        }

        item {
            Row(
                // modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(0.8f),
                    onClick = {
//                        if (!isNotificationServiceEnabled(context)) {
//                            context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
//                        } else {
                        viewModel.setBooleanPref(PreferenceStore.isRunning, true)
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = if (currentState == StopwatchState.Started) ACTION_SERVICE_STOP
                            else ACTION_SERVICE_START
                        )
                        // }
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (currentState == StopwatchState.Started) Red else Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (currentState == StopwatchState.Started) "Stop"
                        else if ((currentState == StopwatchState.Stopped)) "Resume"
                        else "Start"
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(0.8f),
                    onClick = {
                        viewModel.setBooleanPref(PreferenceStore.isRunning, false)
                        //  context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        ServiceHelper.triggerForegroundService(
                            context = context, action = ACTION_SERVICE_CANCEL
                        )
                    },
                    enabled = seconds != "00" && currentState != StopwatchState.Started,
                    colors = ButtonDefaults.buttonColors(disabledBackgroundColor = Color.Gray)
                ) {
                    Text(text = "Cancel")
                }
            }
        }

//        item {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(5.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    TextInput(
//                        modifier = Modifier.align(CenterVertically)
//                    ) {
//                        timer = it
//                    }
//                    Spacer(modifier = Modifier.width(5.dp))
//                    Button(onClick = {
//                        if (timer.isNotEmpty())
//                            setTimerState = false
//                    }, enabled = setTimerState) {
//                        Text(text = stringResource(R.string.set), color = Color.White)
//                    }
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//                if (timer.isNotEmpty())
//                    CountDownTimerScreen(
//                        totalTime = minToLong,
//                        startTimer = startTimerState,
//                        onTimeUpdate = { timer = it }
//                    ) {
//                        startTimerState = it
//                        setTimerState = it
//                    }
//                Spacer(modifier = Modifier.height(10.dp))
//                Row {
//                    Button(
//                        onClick = {
//                            if (timer.isNotEmpty()) {
//                                startTimerState = false
//                                setTimerState = false
//                            }
//                        },
//                        enabled = startTimerState
//                    ) {
//                        Text(text = stringResource(R.string.start), color = Color.White)
//                    }
//                    Button(onClick = {
//                        timer = ""
//                        setTimerState = true
//                        startTimerState = true
//                    }, modifier = Modifier.padding(start = 10.dp)) {
//                        Text(text = stringResource(R.string.reset), color = Color.White)
//                    }
//                }
//            }
//        }

    }

}

@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}

@Composable
fun CountDownTimerScreen(
    totalTime: Long,
    startTimer: Boolean,
    onTimeUpdate: (String) -> Unit,
    onButtonEnable: (Boolean) -> Unit
) {

    var currentTime by remember {
        mutableStateOf(totalTime)
    }
    val minutes = (currentTime / 1000).toInt() / 60
    val seconds = (currentTime / 1000).toInt() % 60


    val res = java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    if (!startTimer)
        LaunchedEffect(key1 = currentTime) {

            if (currentTime > 0) {
                delay(100L)
                currentTime -= 100L
            } else {
                onButtonEnable(true)
                onTimeUpdate("")
            }
        }

    Text(
        text = res,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

}

@Composable
fun CommonRadioButton(
    selected: Boolean,
    title: String,
    onTitleUpdate: (String) -> Unit
) {

    Row {
        RadioButton(selected = selected, onClick = {
            onTitleUpdate(title)
        })
        Text(
            title, style = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier
                .align(CenterVertically)
                .padding(start = 5.dp)
        )
    }

}


@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val ipAddress: CharSequence? = results.getCharSequence("timer")
                onValueChange(ipAddress as String)
            }
        }
    Chip(
        label = {
            Text(
                "Enter Min",
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )
        },
        onClick = {
            val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent();
            val remoteInputs: List<RemoteInput> = listOf(
                RemoteInput.Builder("timer")
                    .setLabel("Enter Min")
                    .wearableExtender {
                        setEmojisAllowed(false)
                        setInputActionType(EditorInfo.IME_ACTION_DONE)
                    }.build()
            )

            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

            launcher.launch(intent)
        },
        modifier = modifier
            .width(80.dp)
            .height(30.dp),
        shape = RoundedCornerShape(5.dp)
    )
}
