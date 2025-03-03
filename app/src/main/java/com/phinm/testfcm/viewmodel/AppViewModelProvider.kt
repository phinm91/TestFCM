package com.phinm.testfcm.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.phinm.testfcm.MainApplication
import com.phinm.testfcm.ui.view.EditEventViewModel
import com.phinm.testfcm.ui.view.ListEventsViewModel
import com.phinm.testfcm.ui.view.NewEventViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ListEventsViewModel(
                application().appContainer.eventRepository
            )
        }

        initializer {
            NewEventViewModel(
                application().appContainer.eventRepository
            )
        }

        initializer {
            EditEventViewModel(
                this.createSavedStateHandle(),
                application().appContainer.eventRepository,
            )
        }
    }
}

fun CreationExtras.application(): MainApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)