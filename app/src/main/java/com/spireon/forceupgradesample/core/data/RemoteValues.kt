package com.spireon.forceupgradesample.core.data

import com.spireon.forceupgradesample.BuildConfig

/**
 * Created by Shubham Singh on 23/12/22.
 */
object RemoteValues {

    var ANDROID_VERSION_CODE = BuildConfig.VERSION_NAME
    var ANDROID_FORCE_UPGRADE = false
    var ANDROID_FORCE_UPGRADE_SHOW_CONTINUE = true
    var ANDROID_PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.spireon.forceupgradesample"


    fun print(): String {
        return  "ANDROID_VERSION_CODE => " + ANDROID_VERSION_CODE + "\n" +
                "ANDROID_FORCE_UPGRADE => " + ANDROID_FORCE_UPGRADE + "\n" +
                "ANDROID_FORCE_UPGRADE_SHOW_CONTINUE => " + ANDROID_FORCE_UPGRADE_SHOW_CONTINUE + "\n" +
                "ANDROID_PLAY_STORE_LINK => " + ANDROID_PLAY_STORE_LINK
    }
}