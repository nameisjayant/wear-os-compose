package com.example.focusworkwearapp.presentation.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false
) : Parcelable {
    @Parcelize
    data class TaskResponse(
        val task: Task? = null,
        val key: String = ""
    ) : Parcelable
}
