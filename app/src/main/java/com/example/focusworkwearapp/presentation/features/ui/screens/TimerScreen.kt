package com.example.focusworkwearapp.presentation.features.ui.screens

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import com.example.focusworkwearapp.R
import com.example.focusworkwearapp.presentation.features.ui.viewmodel.MainViewModel
import com.example.focusworkwearapp.presentation.theme.LightGrey
import com.example.focusworkwearapp.utils.LEVEL1
import com.example.focusworkwearapp.utils.LEVEL2
import com.example.focusworkwearapp.utils.LEVEL3


@Composable
fun TimerScreen(
    viewModel: MainViewModel
) {
    val data = viewModel.task.value
    val levels by remember { mutableStateOf(listOf(LEVEL1, LEVEL2, LEVEL3)) }
    var levelState by remember { mutableStateOf(LEVEL1) }
    var timer by remember { mutableStateOf("") }

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
                    text = data.task?.title ?: "-",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = data.task?.description ?: "-",
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
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextInput(
                        modifier = Modifier.align(CenterVertically)
                    ) {
                        timer = it
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(onClick = { }) {
                        Text(text = stringResource(R.string.set), color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (timer.isNotEmpty())
                    Text(text = "$timer:00 min", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {}) {
                    Text(text = stringResource(R.string.start), color = Color.White)
                }
            }
        }

    }

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
                .align(Alignment.CenterVertically)
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
