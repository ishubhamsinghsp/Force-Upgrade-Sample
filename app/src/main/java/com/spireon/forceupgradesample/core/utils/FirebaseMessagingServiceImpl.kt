package com.spireon.forceupgradesample.core.utils

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.spireon.forceupgradesample.ForceUpgradeSampleApplication
import com.spireon.forceupgradesample.core.data.Constants

/**
 * Created by Shubham Singh on 22/12/22.
 */
class FirebaseMessagingServiceImpl : FirebaseMessagingService() {

    private val firebaseUtils: FirebaseUtils = FirebaseUtils.getInstance()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        if (message.data.containsKey("CONFIG_STATE") && message.data["CONFIG_STATE"] == "STALE") {
            preferences.edit { putBoolean(Constants.IS_REMOTE_CONFIG_STALE, true) }
            if ((this.application.applicationContext as ForceUpgradeSampleApplication).isAppHidden.not()) { // Only call when app is in foreground
                firebaseUtils.fetch(isForcedFetch = true)
            }
        }
    }

    companion object {
        fun subscribeToRemoteConfigChangePush() {
            FirebaseMessaging.getInstance().subscribeToTopic("PUSH_RC")
        }
    }
}