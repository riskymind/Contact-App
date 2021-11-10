package com.asterisk.contactapp.ui.deleteAllContacts

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.asterisk.contactapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllContactsFragmentDialog : DialogFragment() {

    private val viewModel by viewModels<DeleteAllContactsViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirmation_title))
            .setMessage(getString(R.string.deletion_warning))
            .setNegativeButton(getString(R.string.cancel_text), null)
            .setPositiveButton(getString(R.string.continue_text)) { _, _ ->
                viewModel.onConfirmClick()
            }
            .create()
}