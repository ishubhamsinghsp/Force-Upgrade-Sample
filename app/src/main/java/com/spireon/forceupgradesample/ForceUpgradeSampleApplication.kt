package com.spireon.forceupgradesample

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.spireon.forceupgradesample.core.data.Constants
import com.spireon.forceupgradesample.core.utils.FirebaseMessagingServiceImpl
import com.spireon.forceupgradesample.core.utils.FirebaseUtils
import com.spireon.forceupgradesample.core.utils.OnFirebaseUtilListener

/**
 * Created by Shubham Singh on 15/12/22.
 */
class ForceUpgradeSampleApplication: Application(), OnFirebaseUtilListener {

    var isAppHidden: Boolean = false
    private lateinit var preferences: SharedPreferences

    private var lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                onAppBackgrounded()
            }
            Lifecycle.Event.ON_START -> {
                onAppForegrounded()
            }
            else -> {}
        }
    }

    override fun onCreate() {
        super.onCreate()

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
        FirebaseMessagingServiceImpl.subscribeToRemoteConfigChangePush()

        getRemoteValues()
    }

    private fun getRemoteValues() {
        val firebaseUtils = FirebaseUtils.getInstance()
        firebaseUtils.setOnFirebaseUtilListener(this)
        firebaseUtils.fetch()
    }

    private fun onAppForegrounded() {
        isAppHidden = false
        if (preferences.getBoolean(Constants.IS_REMOTE_CONFIG_STALE, false)) {
            getRemoteValues()
        }
    }

    private fun onAppBackgrounded() {
        isAppHidden = true
    }

    override fun onFirebaseConfigReceived(isSuccess: Boolean) {
        if (isSuccess) {
            preferences.edit { putBoolean(Constants.IS_REMOTE_CONFIG_STALE, false) }
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(Constants.INTENT_CHECK_FORCE_UPGRADE))
        }
    }
}