package com.example.focusworkwearapp.utils

data class Questions(
    val id: Int = 0,
    val question: String = "",
    val answer: String = ""
)

val questionList = listOf(
    Questions(
        1,
        "What did you like about your day?"
    ),
    Questions(
        2,
        "Have you completed all the tasks on your to-do list?"
    ),
    Questions(
        3,
        "Could you have done anything differently?"
    ),
    Questions(
        4,
        "On the scale of 1-10, how distracted where you today?"
    ),
    Questions(
        5,
        "Are there any aspects that you would like to remain the same?"
    ),
    Questions(
        6,
        "Do you want to add any To-do for tomorrow?"
    )
)
