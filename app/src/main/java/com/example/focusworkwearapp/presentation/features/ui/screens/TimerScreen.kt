package com.example.focusworkwearapp.presentation.features.ui.screens

import android.app.RemoteInput
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.inputmethodservice.Keyboard.Row
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
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
    val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
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
                        //   context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
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

@RequiresApi(Build.VERSION_CODES.M)
private fun isNotificationServiceEnabled(
    context: Context
): Boolean {

    val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    val pkgName = context.packageName
    val flat = Settings.Secure.getString(
        context.contentResolver, ENABLED_NOTIFICATION_LISTENERS
    )
    if (!TextUtils.isEmpty(flat)) {
        val names = flat.split(":").toTypedArray()
        for (i in names.indices) {
            val cn = ComponentName.unflattenFromString(names[i])
            if (cn != null) {
                if (TextUtils.equals(pkgName, cn.packageName)) {
                    return true
                }
            }
        }
    }
    return false
}

