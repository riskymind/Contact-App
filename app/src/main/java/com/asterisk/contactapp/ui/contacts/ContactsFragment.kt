package com.asterisk.contactapp.ui.contacts

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asterisk.contactapp.R
import com.asterisk.contactapp.data.Contact
import com.asterisk.contactapp.data.SortOrder
import com.asterisk.contactapp.databinding.FragmentContactsBinding
import com.asterisk.contactapp.util.onQueryTextChanger
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts), ContactAdapter.OnItemClickListener {

    private val viewModel by viewModels<ContactViewModel>()

    private val contactAdapter = ContactAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val binding = FragmentContactsBinding.bind(view)

        binding.apply {
            rvContacts.apply {
                adapter = contactAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val contact = contactAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onContactSwiped(contact)
                }
            }).attachToRecyclerView(rvContacts)

            fabAddContact.setOnClickListener {
                viewModel.onAddContactClick()
            }
        }


        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.contacts.observe(viewLifecycleOwner) {
            contactAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.contactsEvent.collect { event ->
                when (event) {
                    is ContactViewModel.ContactsEvent.ShowUndoDeletedContactMessage -> {
                        Snackbar.make(requireView(), "Contact Deleted!!", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.undoDeletedContact(event.contact)
                            }.show()
                    }
                    is ContactViewModel.ContactsEvent.NavigateToAddContactScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToAddEditContactFragment(
                                null, "New Contact"
                            )
                        findNavController().navigate(action)
                    }
                    is ContactViewModel.ContactsEvent.NavigateToEditContactScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToAddEditContactFragment(
                                event.contact, "Edit Contact"
                            )
                        findNavController().navigate(action)
                    }
                    is ContactViewModel.ContactsEvent.ShowContactSaveConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    ContactViewModel.ContactsEvent.NavigateToDeleteAllContactsScreen -> {
                        val action = ContactsFragmentDirections.actionGlobalDeleteAllContactsFragmentDialog()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_contacts, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanger {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_fav).isChecked =
                viewModel.preferencesFlow.first().hideFav
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortedOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date -> {
                viewModel.onSortedOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_fav -> {
                item.isChecked = !item.isChecked
                viewModel.onHideFavClicked(item.isChecked)
                true
            }

            R.id.action_delete_all -> {
                viewModel.deleteAllContacts()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    companion object {
        const val TAG = "ContactsFragment"
    }

    override fun onItemClicked(contact: Contact) {
        viewModel.onContactSelected(contact)
    }

    override fun onFavClick(contact: Contact) {
        viewModel.onContactFavClick(contact)
    }

}