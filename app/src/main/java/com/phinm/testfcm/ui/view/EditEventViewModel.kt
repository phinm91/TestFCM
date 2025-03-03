package com.phinm.testfcm.ui.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.data.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EditEventViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle[EditEventDestination.itemIdArg])

    private val _event: StateFlow<EventConfig?> = eventRepository.getEventById(itemId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventConfig(),
    )
    val event: StateFlow<EventConfig?> = _event

    suspend fun updateEvent(eventConfig: EventConfig) {
        eventRepository.updateEvent(eventConfig)
    }
}