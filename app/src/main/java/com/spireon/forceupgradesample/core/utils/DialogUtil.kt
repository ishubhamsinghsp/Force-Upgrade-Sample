package com.spireon.forceupgradesample.core.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.spireon.forceupgradesample.R

/**
 * Created by Shubham Singh on 23/12/22.
 */
object DialogUtil {

    private var dialog: AlertDialog? = null

    fun showDialogWithTwoButtons(
        context: Context?,
        titleId: Int,
        messageId: Int,
        positiveButtonText: Int,
        negativeButtonText: Int,
        positiveButtonClickListener: DialogInterface.OnClickListener,
        negativeButtonClickListener: DialogInterface.OnClickListener
    ): AlertDialog? {
        context?.let {
            val alertErrorBuilder = AlertDialog.Builder(it)
            alertErrorBuilder.setMessage(messageId)
                .setCancelable(false)
                .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                .setNegativeButton(negativeButtonText, negativeButtonClickListener)

            if (titleId != 0) {
                alertErrorBuilder.setTitle(titleId)
            }

            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
            dialog = alertErrorBuilder.create()
            dialog?.show()
            setDialogButtonColor(context)
        }
        return dialog
    }

    fun showDialog(
        context: Context,
        titleId: Int,
        messageId: Int,
        buttonTextId: Int,
        buttonClickListener: (Any, Any) -> Unit
    ): AlertDialog? {
        val alertErrorBuilder = AlertDialog.Builder(context)
        alertErrorBuilder.setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(buttonTextId, buttonClickListener)

        if (titleId != 0) {
            alertErrorBuilder.setTitle(titleId)
        }

        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = alertErrorBuilder.create()
        dialog?.show()
        setDialogButtonColor(context)
        return dialog
    }

    private fun setDialogButtonColor(context: Context) {
        dialog?.let {
            if (it.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
                it.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.purple_200))
            }
            if (it.getButton(DialogInterface.BUTTON_NEGATIVE) != null) {
                it.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.purple_200))
            }

            if (it.getButton(DialogInterface.BUTTON_NEUTRAL) != null) {
                it.getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(context, R.color.purple_200))
            }
        }
    }

}