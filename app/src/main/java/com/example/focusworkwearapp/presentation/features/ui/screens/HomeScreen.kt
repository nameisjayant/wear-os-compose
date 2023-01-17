package com.example.focusworkwearapp.presentation.features.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Task
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.focusworkwearapp.R
import com.example.focusworkwearapp.presentation.features.navigation.Navigators

@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center
    ) {
        HomeScreenContents(
            title = stringResource(R.string.task_screen),
            icon = Icons.Default.Home
        ) {
            navHostController.navigate(Navigators.Task.route)
        }
        HomeScreenContents(
            title = stringResource(R.string.report_screen),
            icon = Icons.Default.Task
        ) {
            navHostController.navigate(Navigators.Report.route)
        }
        HomeScreenContents(
            title = stringResource(R.string.info_screen),
            icon = Icons.Default.Info
        ) {
            navHostController.navigate(Navigators.Info.route)
        }
    }

}

@Composable
fun HomeScreenContents(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = { onClick() }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Icon(icon, contentDescription = "", tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.align(CenterVertically),
                textAlign = TextAlign.Center
            )
        }
    }
}