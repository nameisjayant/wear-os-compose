package com.example.focusworkwearapp.presentation.features.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.example.focusworkwearapp.R
import com.example.focusworkwearapp.presentation.data.models.Task
import com.example.focusworkwearapp.presentation.data.repository.PreferenceStore
import com.example.focusworkwearapp.presentation.features.navigation.Navigators
import com.example.focusworkwearapp.presentation.features.ui.viewmodel.MainViewModel
import com.example.focusworkwearapp.presentation.theme.Orange
import kotlinx.coroutines.flow.first


@Composable
fun TaskScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController
) {
    val res = viewModel.taskResponse.value
    var isTaskRunning by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (viewModel.getBooleanPref(PreferenceStore.isRunning).first()) {
            isTaskRunning = true
        }
    }
    if (isTaskRunning) {
        TimerRunningText {
            navHostController.navigate(Navigators.Timer.route)
            isTaskRunning = false
        }

    }

    Scaffold {
        if (res.data.isNotEmpty()) {
            ScalingLazyColumn {
                items(res.data,
                    key = { it.key }) { data ->
                    TaskEachRow(data = data) {
                        viewModel.setStringPref(
                            PreferenceStore.title,
                            data.task?.title ?: "-"
                        )
                        viewModel.setStringPref(
                            PreferenceStore.des,
                            data.task?.description ?: "-"
                        )
                        viewModel.setTaskData(data)
                        navHostController.navigate(
                            Navigators.Timer.route
                        )
                    }
                }
            }

        }

        if (res.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        if (res.msg.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.something_went_wrong))
            }
        }
    }

}

@Composable
fun TaskEachRow(
    data: Task.TaskResponse,
    onClick: () -> Unit = {}
) {

    Card(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column {
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
    }

}

@Composable
fun TimerRunningText(
    onClick: () -> Unit = {}
) {

    Dialog(onDismissRequest = { }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Orange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Task is already schedule", style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { onClick() }) {
                Text(text = "Go", color = Color.White, fontSize = 10.sp)
            }
        }
    }

}
