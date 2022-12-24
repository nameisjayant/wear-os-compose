package com.example.focusworkwearapp.utils

data class Questions(
    val id: Int = 0,
    val question: String = "",
    val answer: String = ""
)

val questionList = listOf(
    Questions(
        1,
        "What did i like about my dad?"
    ),
    Questions(
        2,
        "Did you accomplish everything on your To-Do list?"
    ),
    Questions(
        3,
        "Is there anything you could have done differently?"
    ),
    Questions(
        4,
        "Is there anything you would like to keep the same?"
    ),
    Questions(
        5,
        "Is there any negativity you don't want to cary into tomorrow?"
    ),
    Questions(
        6,
        "Did you behave well with the distractions?"
    )
)
