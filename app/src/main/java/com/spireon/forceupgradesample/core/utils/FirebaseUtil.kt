package com.spireon.forceupgradesample.core.utils

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.spireon.forceupgradesample.BuildConfig
import com.spireon.forceupgradesample.core.data.RemoteValues

/**
 * Created by Shubham Singh on 20/12/22.
 */
class FirebaseUtils {

    companion object {
        private lateinit var firebaseUtils: FirebaseUtils
        private const val ANDROID_PLAY_STORE_LINK = "android_play_store_link"
        private const val ANDROID_VERSION_CODE = "android_version_code"
        private const val ANDROID_FORCE_UPGRADE = "android_force_upgrade"
        private const val ANDROID_FORCE_UPGRADE_SHOW_CONTINUE ="android_force_upgrade_show_continue"

        fun getInstance() : FirebaseUtils {
            if(::firebaseUtils.isInitialized.not()) {
                firebaseUtils = FirebaseUtils()
            }
            return  firebaseUtils
        }

        fun needUpdate(currentVersion: String, remoteVersion: String): Boolean {
            val currentVersionArray = currentVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val remoteVersionArray = remoteVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val maxLength = currentVersionArray.size.coerceAtLeast(remoteVersionArray.size)

            var needsUpdate = false
            for (i in 0 until maxLength) {
                val cV = if (i < currentVersionArray.size) Integer.parseInt(currentVersionArray[i]) else 0
                val rV = if (i < remoteVersionArray.size) Integer.parseInt(remoteVersionArray[i]) else 0
                if (cV != rV) {
                    needsUpdate = cV < rV
                    break
                }
            }
            return needsUpdate
        }
    }

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private val TAG = FirebaseUtils::class.java.simpleName
    private var cacheExpiration: Long = 3600 // 1 hour in seconds
    private lateinit var listener: OnFirebaseUtilListener

    fun setOnFirebaseUtilListener(firebaseUtilListener: OnFirebaseUtilListener) {
        this.listener = firebaseUtilListener
    }

    private fun initialize() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(cacheExpiration)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(getDefaultValues())
    }

    /**
     * to fetch the values from firebase server
     */
    fun fetch(isForcedFetch: Boolean = false) {
        if (!::mFirebaseRemoteConfig.isInitialized) {
            initialize()
        }

        val minFetchInterval: Long = when {
            BuildConfig.BUILD_TYPE.equals("debug", ignoreCase = true) -> 20
            else -> cacheExpiration
        }

        mFirebaseRemoteConfig
            .fetch(if (isForcedFetch) 0 else minFetchInterval)
            .addOnFailureListener(mFailureListener)
            .addOnCompleteListener(mCompleteListener)
    } // end of fetch

    private val mFailureListener = OnFailureListener { e ->

        if (!::listener.isInitialized) {
            listener.onFirebaseConfigReceived(false)
        }
        e.printStackTrace()
    }

    private val mCompleteListener = OnCompleteListener<Void> { task ->
        if (task.isSuccessful) {
            mFirebaseRemoteConfig.activate().addOnCompleteListener {
                if (it.isSuccessful) {
                    extractValues()
                    if (::listener.isInitialized) {
                        listener.onFirebaseConfigReceived(true)
                    }
                }
            }
        }
    }

    private fun getDefaultValues(): Map<String, Any> {
        val map: HashMap<String, Any> = HashMap()
        map[ANDROID_FORCE_UPGRADE] = RemoteValues.ANDROID_FORCE_UPGRADE
        map[ANDROID_VERSION_CODE] = RemoteValues.ANDROID_VERSION_CODE
        map[ANDROID_PLAY_STORE_LINK] = RemoteValues.ANDROID_PLAY_STORE_LINK
        map[ANDROID_FORCE_UPGRADE_SHOW_CONTINUE] = RemoteValues.ANDROID_FORCE_UPGRADE_SHOW_CONTINUE

        return map
    }

    private fun extractValues() {
        RemoteValues.ANDROID_PLAY_STORE_LINK = mFirebaseRemoteConfig.getString(ANDROID_PLAY_STORE_LINK)
        RemoteValues.ANDROID_VERSION_CODE = mFirebaseRemoteConfig.getString(ANDROID_VERSION_CODE)
        RemoteValues.ANDROID_FORCE_UPGRADE = mFirebaseRemoteConfig.getBoolean(ANDROID_FORCE_UPGRADE)
        RemoteValues.ANDROID_FORCE_UPGRADE_SHOW_CONTINUE = mFirebaseRemoteConfig.getBoolean(ANDROID_FORCE_UPGRADE_SHOW_CONTINUE)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, RemoteValues.print())
        }
    }

}

interface OnFirebaseUtilListener {
    fun onFirebaseConfigReceived(isSuccess: Boolean)
}