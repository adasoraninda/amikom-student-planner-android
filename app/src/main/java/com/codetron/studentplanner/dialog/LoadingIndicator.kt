package com.codetron.studentplanner.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.codetron.studentplanner.R
import javax.inject.Inject

class LoadingIndicator @Inject constructor() : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.layout_loading_indicator)
        isCancelable = false
        retainInstance = true
        return builder.create()
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}