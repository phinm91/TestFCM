package com.phinm.testfcm

import android.content.Context
import com.phinm.testfcm.data.EventDatabase
import com.phinm.testfcm.data.EventRepository

class AppContainer(
    private val context: Context
) {
    val eventRepository: EventRepository by lazy {
        EventRepository(
            EventDatabase.getDatabase(context).eventDao()
        )
    }
}