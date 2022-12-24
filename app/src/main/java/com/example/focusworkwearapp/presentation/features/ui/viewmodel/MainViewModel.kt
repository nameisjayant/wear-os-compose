package com.example.focusworkwearapp.presentation.features.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusworkwearapp.presentation.common.Result
import com.example.focusworkwearapp.presentation.common.doOnFailure
import com.example.focusworkwearapp.presentation.common.doOnLoading
import com.example.focusworkwearapp.presentation.common.doOnSuccess
import com.example.focusworkwearapp.presentation.data.models.Task
import com.example.focusworkwearapp.presentation.data.repository.MainRepository
import com.example.focusworkwearapp.presentation.data.repository.PreferenceStore
import com.example.focusworkwearapp.utils.Questions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val preferenceStore: PreferenceStore
) : ViewModel() {

    private val _tasksResponse: MutableState<TaskStates> = mutableStateOf(TaskStates())
    val taskResponse: State<TaskStates> = _tasksResponse
    private val _task: MutableState<Task.TaskResponse> = mutableStateOf(Task.TaskResponse())
    val task: State<Task.TaskResponse> = _task

    private val _addQuestionEventFlow =
        MutableSharedFlow<Result<String>>()
    val addQuestionEventFlow = _addQuestionEventFlow.asSharedFlow()

    var state by mutableStateOf(MainScreenState())
        private set

    fun changeTextValue(text: String) {
        viewModelScope.launch {
            state = state.copy(
                text = text
            )
        }
    }

    fun setTaskData(data: Task.TaskResponse) {
        _task.value = data
    }

    fun onEvent(events: MainScreenEvents) {
        when (events) {
            is MainScreenEvents.AddQuestionEvent -> {
                viewModelScope.launch {
                    repository.addQuestions(events.data)
                        .doOnSuccess {
                            _addQuestionEventFlow.emit(Result.Success(it))
                        }
                        .doOnFailure {
                            _addQuestionEventFlow.emit(
                                Result.Failure(
                                    it ?: Throwable("Something went wrong!!")
                                )
                            )
                        }
                        .doOnLoading {
                            _addQuestionEventFlow.emit(Result.Loading)
                        }
                        .collect()
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.getTask()
                .doOnSuccess {
                    _tasksResponse.value = TaskStates(
                        data = it
                    )
                }.doOnFailure {
                    _tasksResponse.value = TaskStates(
                        msg = it?.message ?: "Something went wrong"
                    )
                }.doOnLoading {
                    _tasksResponse.value = TaskStates(
                        isLoading = true
                    )
                }.collect()
        }
    }

    fun setPref(key: Preferences.Key<Boolean>, value: Boolean) = viewModelScope.launch {
        preferenceStore.setBooleanPref(key, value)
    }

    fun setPref(key: Preferences.Key<Long>, value: Long) = viewModelScope.launch {
        preferenceStore.setLongPref(key, value)
    }

    fun getLongPref(key: Preferences.Key<Long>) = preferenceStore.getLongPref(key)

    fun getStringPref(key: Preferences.Key<String>) = preferenceStore.getStringPref(key)

    fun setStringPref(key: Preferences.Key<String>, value: String) = viewModelScope.launch {
        preferenceStore.setStringPref(key, value)
    }


    fun getBooleanPref(key: Preferences.Key<Boolean>) = preferenceStore.getBooleanPref(key)
    fun setBooleanPref(key: Preferences.Key<Boolean>, value: Boolean) = viewModelScope.launch {
        preferenceStore.setBooleanPref(key, value)
    }

}

data class TaskStates(
    val data: List<Task.TaskResponse> = emptyList(),
    val msg: String = "",
    val isLoading: Boolean = false
)

data class MainScreenState(
    var text: String? = null
)

sealed class MainScreenEvents {

    data class AddQuestionEvent(val data: List<Questions>) : MainScreenEvents()

}