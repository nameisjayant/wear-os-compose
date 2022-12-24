package com.example.focusworkwearapp.utils

import android.content.Context
import android.widget.Toast


const val TASK = "task"
const val LEVEL1 = "Level 1"
const val LEVEL2 = "Level 2"
const val LEVEL3 = "Level 3"
const val QUESTIONS = "questions"

const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"
const val ACTION_SERVICE_CANCEL = "ACTION_SERVICE_CANCEL"

const val STOPWATCH_STATE = "STOPWATCH_STATE"

const val NOTIFICATION_CHANNEL_ID = "STOPWATCH_NOTIFICATION_ID"
const val NOTIFICATION_CHANNEL_NAME = "STOPWATCH_NOTIFICATION"
const val NOTIFICATION_ID = 10

const val CLICK_REQUEST_CODE = 100
const val CANCEL_REQUEST_CODE = 101
const val STOP_REQUEST_CODE = 102
const val RESUME_REQUEST_CODE = 103

fun Context.showToast(
    msg: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, msg, duration).show()
