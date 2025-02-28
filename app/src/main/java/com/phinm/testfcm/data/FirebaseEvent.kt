package com.phinm.testfcm.data

data class FirebaseUser(
    val notifyToken: String = "",
    val events : List<FirebaseEvent> = listOf()
)

data class FirebaseEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val notifyDate: String = "",
    val notifyTimes : List<String> = listOf(),

)
