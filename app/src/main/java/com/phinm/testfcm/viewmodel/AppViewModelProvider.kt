package com.phinm.testfcm.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.phinm.testfcm.MainApplication
import com.phinm.testfcm.ui.view.EventViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            EventViewModel(
                application().appContainer.eventRepository
            )
        }
    }
}

fun CreationExtras.application(): MainApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)