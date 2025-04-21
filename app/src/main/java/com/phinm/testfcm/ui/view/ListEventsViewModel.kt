package com.phinm.testfcm.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.phinm.testfcm.MainApplication
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.data.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class ListEventsViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _events: StateFlow<List<EventConfig>> = eventRepository.getEvents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val events: StateFlow<List<EventConfig>> = _events

    init {
        viewModelScope.launch {
            eventRepository.getEvents().collectLatest {
                pushEventToFirebase(it)
            }
        }
    }

    private val firestore: FirebaseFirestore by lazy {
        Firebase.firestore("test-fcm")
    }

    suspend fun deleteEvent(eventConfig: EventConfig) {
        eventRepository.deleteEvent(eventConfig)
    }

    private fun pushEventToFirebase(eventConfigs: List<EventConfig>) {
        val token = MainApplication.fcmToken() ?: run {
            Timber.e("FCM token is null")
            return@pushEventToFirebase
        }
        val uid = MainApplication.uid() ?: run {
            Timber.e("UID token is null")
            return@pushEventToFirebase
        }
        val events = eventConfigs.flatMap { it.toFirebaseEvent() }

        val userDocumentReference = firestore.collection("reminders").document(uid)
        userDocumentReference.set(hashMapOf("notifyToken" to token))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.d("Token updated successfully")
                } else {
                    Timber.e("Failed to update token: ${it.exception?.message}")
                }
            }
        val eventCollectionReference = userDocumentReference.collection("events")
        eventCollectionReference.get().addOnSuccessListener { querySnapshot ->
            val deleteTasks = mutableListOf<Task<Void>>()
            for (document in querySnapshot.documents) {
                deleteTasks.add(document.reference.delete())
            }
            Tasks.whenAllComplete(deleteTasks)
                .addOnCompleteListener { allTasks ->
                    if (allTasks.isSuccessful) {
                        Timber.d("All events deleted successfully")
                        val batch = firestore.batch()
                        for (event in events) {
                            val newDocDef = eventCollectionReference.document()
                            batch.set(newDocDef, event)
                        }
                        batch.commit()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Timber.d("Events added successfully")
                                } else {
                                    Timber.e("Failed to add events : ${it.exception?.message}")
                                }
                            }
                    }
                }
        }
    }
}