package com.phinm.testfcm

import android.app.Application
import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class MainApplication: Application() {

    companion object {
        private lateinit var instance: MainApplication

        fun fcmToken(): String? {
            return if (instance::fcmToken.isInitialized) instance.fcmToken
            else null
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }

        fun initFCMToken(result: (Boolean) -> Unit = {}) {
            instance.initFCMToken(result)
        }
    }

    lateinit var appContainer: AppContainer
    lateinit var fcmToken: String

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        instance = this
        appContainer = AppContainer(this)

        FirebaseApp.initializeApp(this)
    }

    fun initFCMToken(result: (Boolean) -> Unit = {}) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.v(task.exception, "Fetching FCM registration token failed")
                result(false)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcmToken = task.result
            Timber.v("FCM Token:\n$fcmToken")
            result(true)
        })
    }
}