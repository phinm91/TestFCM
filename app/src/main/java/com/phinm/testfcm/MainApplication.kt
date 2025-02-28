package com.phinm.testfcm

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainApplication: Application() {

    companion object {
        private const val TAG = "MainApplication"
        private lateinit var instance: MainApplication

        fun fcmToken() = instance.fcmToken

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    lateinit var appContainer: AppContainer
    lateinit var fcmToken: String

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)

        FirebaseApp.initializeApp(this)
        initFCMToken()
    }

    private fun initFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcmToken = task.result
            Log.d(TAG, "FCM Token:\n$fcmToken")
        })
    }
}