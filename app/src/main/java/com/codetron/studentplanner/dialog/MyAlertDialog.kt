package com.codetron.studentplanner.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.codetron.studentplanner.R

class MyAlertDialog(
    private val title: String?,
    private val message: String?,
    private val dialogInterface: DialogInterface.OnClickListener
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context?.getString(R.string.yes), dialogInterface)
            .setNegativeButton(context?.getString(R.string.no), dialogInterface)

        isCancelable = false
        retainInstance = true
        return builder.create()
    }

}