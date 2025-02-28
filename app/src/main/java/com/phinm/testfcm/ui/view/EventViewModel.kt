package com.phinm.testfcm.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.phinm.testfcm.MainApplication
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.data.EventRepository
import com.phinm.testfcm.data.FirebaseUser
import com.phinm.testfcm.util.deviceUUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn

class EventViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _events: StateFlow<List<EventConfig>> = eventRepository.getEvents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val events: StateFlow<List<EventConfig>> = _events

    private val firebaseEvents : DatabaseReference by lazy {
        Firebase.database("https://testfcm-35082-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Events")
    }

    suspend fun addEvent(eventConfig: EventConfig) {
        eventRepository.insertEvent(eventConfig)
        eventRepository.getEvents().collectLatest {
            pushEventToFirebase(it)
        }
    }

    suspend fun deleteEvent(eventConfig: EventConfig) {
        eventRepository.deleteEvent(eventConfig)
        eventRepository.getEvents().collectLatest {
            pushEventToFirebase(it)
        }
    }

    suspend fun updateEvent(eventConfig: EventConfig) {
        eventRepository.updateEvent(eventConfig)
        eventRepository.getEvents().collectLatest {
            pushEventToFirebase(it)
        }
    }

    private fun pushEventToFirebase(eventConfigs : List<EventConfig>){
        val uuid = MainApplication.getAppContext().deviceUUID()
        val user = FirebaseUser(
            notifyToken = MainApplication.fcmToken(),
            events = eventConfigs.map { it.toFirebaseEvent() }
        )
        firebaseEvents.child(uuid)
            .setValue(user)
    }
}