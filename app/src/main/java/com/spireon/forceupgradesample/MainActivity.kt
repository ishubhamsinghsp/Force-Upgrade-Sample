package com.spireon.forceupgradesample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.spireon.forceupgradesample.core.data.Constants
import com.spireon.forceupgradesample.core.data.RemoteValues
import com.spireon.forceupgradesample.core.utils.DialogUtil
import com.spireon.forceupgradesample.core.utils.FirebaseUtils

/**
 * Created by Shubham Singh on 20/12/22.
 */
class MainActivity: AppCompatActivity() {


    private var forceUpgradeDialog: AlertDialog? = null

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            messageReceiver, IntentFilter(
                Constants.INTENT_CHECK_FORCE_UPGRADE
            )
        )
    }

    val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (needForceUpgrade()) {
                showForceUpgradeDialog()
            } else {
                if (forceUpgradeDialog?.isShowing == true) {
                    forceUpgradeDialog?.dismiss()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    private fun needForceUpgrade(): Boolean {
        return RemoteValues.ANDROID_FORCE_UPGRADE && FirebaseUtils.needUpdate(
            BuildConfig.VERSION_NAME,
            RemoteValues.ANDROID_VERSION_CODE
        )
    }

    private fun showForceUpgradeDialog() {
        if (forceUpgradeDialog != null && forceUpgradeDialog?.isShowing == true) {
            return
        }
        if (RemoteValues.ANDROID_FORCE_UPGRADE_SHOW_CONTINUE) {
            forceUpgradeDialog = DialogUtil.showDialogWithTwoButtons(
                this,
                R.string.new_update,
                R.string.message_new_version_available,
                R.string.lbl_not_now,
                R.string.get_the_update,
                { _, _ ->
                    bypassForceUpgrade()
                },
                { _, _ ->
                    // re-direct to Play Store
                    startActivity(
                        Intent(
                            "android.intent.action.VIEW",
                            Uri.parse(RemoteValues.ANDROID_PLAY_STORE_LINK)
                        )
                    )
                    finish()
                }
            )
        } else {
            forceUpgradeDialog = DialogUtil.showDialog(
                this,
                R.string.new_update,
                R.string.message_new_version_available,
                R.string.get_the_update
            ) { _, _ ->
                // re-direct to Play Store
                startActivity(Intent("android.intent.action.VIEW", Uri.parse(RemoteValues.ANDROID_PLAY_STORE_LINK)))
                finish()
            }
        }
    }

    private fun bypassForceUpgrade() {
        forceUpgradeDialog?.dismiss()
    }
}