package com.asterisk.contactapp.ui.add_edit_contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.asterisk.contactapp.R
import com.asterisk.contactapp.databinding.FragmentAddEditContactBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val PICK_PHOTO_CODE = 101

@AndroidEntryPoint
class AddEditContactFragment : Fragment(R.layout.fragment_add_edit_contact) {
    private lateinit var binding: FragmentAddEditContactBinding


    private val viewModel by viewModels<AddEditContactViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddEditContactBinding.bind(view)

        binding.apply {
            etContactName.setText(viewModel.contactName)
            etContactNumber.setText(viewModel.contactPhone)
            Glide.with(this@AddEditContactFragment)
                .load(viewModel.contactImage)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(R.drawable.default_avatar)
                .into(ivContactImage)
            tvCreatedDate.isVisible = viewModel.contact != null
            tvCreatedDate.text = "Created: ${viewModel.contact?.createdFormattedDate}"
            ivFavContact.setImageResource(if (viewModel.isFav) R.drawable.ic_heart_active else R.drawable.ic_heart_inactive)


            etContactName.addTextChangedListener {
                viewModel.contactName = it.toString()
            }

            etContactNumber.addTextChangedListener {
                viewModel.contactPhone = it.toString()
            }

            ivContactImage.setOnClickListener {
                val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
                imagePickerIntent.type = "image/*"
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }

            fabSaveContact.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditContactEvent.collect { event ->
                when (event) {
                    is AddEditContactViewModel.AddEditContactEvent.NavigateBackWithResult -> {
                        binding.apply {
                            etContactNumber.clearFocus()
                            etContactName.clearFocus()
                        }

                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()

                    }
                    is AddEditContactViewModel.AddEditContactEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val photoUri = data?.data
                binding.ivContactImage.setImageURI(photoUri)
                viewModel.contactImage = photoUri.toString()
            } else {
                Snackbar.make(requireView(), "Image picker cancelled", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

}