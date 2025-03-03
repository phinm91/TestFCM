package com.phinm.testfcm.ui.view

import androidx.lifecycle.ViewModel
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.data.EventRepository

class NewEventViewModel(
    private val eventRepository: EventRepository,
) : ViewModel() {
    suspend fun addEvent(eventConfig: EventConfig) {
        eventRepository.insertEvent(eventConfig)
    }
}