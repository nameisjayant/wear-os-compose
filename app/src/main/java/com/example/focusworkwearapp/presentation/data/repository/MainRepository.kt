package com.example.focusworkwearapp.presentation.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.example.focusworkwearapp.presentation.data.models.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import com.example.focusworkwearapp.presentation.common.Result

class MainRepository @Inject constructor(
    private val db: DatabaseReference
) {

    fun getTask(): Flow<Result<List<Task.TaskResponse>>> = callbackFlow {

        trySend(Result.Loading)

        val valueEvent = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map {
                    Task.TaskResponse(
                        it.getValue(Task::class.java),
                        key = it.key ?: "0"
                    )
                }
                trySend(Result.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.Failure(error.toException()))
            }

        }

        db.addValueEventListener(valueEvent)
        awaitClose {
            db.removeEventListener(valueEvent)
            close()
        }
    }


}